package theking530.staticpower.events;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.RecipeReloadListener;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.entities.player.datacapability.PacketSyncStaticPowerPlayerDataCapability;
import theking530.staticpower.entities.player.datacapability.StaticPowerPlayerCapabilityProvider;
import theking530.staticpower.entities.player.datacapability.StaticPowerPlayerData;
import theking530.staticpower.init.ModCommands.ResearchCommands;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.items.tools.Hammer;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.TeamManager;
import theking530.staticpower.world.fluid.ModWorldFluids;
import theking530.staticpower.world.ore.ModOres;
import theking530.staticpower.world.trees.ModTrees;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class StaticPowerForgeEventsCommon {
	public static final ResourceLocation STATIC_POWER_PLAYER_DATA = new ResourceLocation(StaticPower.MOD_ID, "player_data");
	public static Path DATA_PATH;

	@SubscribeEvent
	public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
		ResearchCommands.register(commandDispatcher);
	}

	@SubscribeEvent
	public static void worldTickEvent(TickEvent.WorldTickEvent event) {
		if (!event.world.isClientSide) {
			if (event.phase == TickEvent.Phase.END) {
				CableNetworkManager.get(event.world).tick();
				StaticPowerGameDataManager.tickGameData();
			}
		}
	}

	@SubscribeEvent
	public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			event.player.getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((playerCap) -> {
				playerCap.tick(event);
			});
		}
	}

	@SubscribeEvent
	public static void onServerAboutToStart(ServerAboutToStartEvent serverStarted) {
		DATA_PATH = serverStarted.getServer().getWorldPath(new LevelResource("data"));
		StaticPowerRecipeRegistry.onResourcesReloaded(serverStarted.getServer().getRecipeManager());
		StaticPowerGameDataManager.clearAllGameData();
	}

	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent reloadEvent) {
		reloadEvent.addListener(new RecipeReloadListener(reloadEvent.getServerResources().getRecipeManager()));
		StaticPower.LOGGER.info("Server resource reload listener created!");
	}

	@SubscribeEvent
	public static void onLoad(Load load) {
		if (!load.getWorld().isClientSide()) {
			StaticPowerGameDataManager.loadDataFromDisk(load);
		}
	}

	@SubscribeEvent
	public static void onSave(Save save) {
		if (!save.getWorld().isClientSide()) {
			StaticPowerGameDataManager.saveDataToDisk(save);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoad(PlayerEvent.LoadFromFile load) {
		// When called on the server, add the player to a team if one does not exist.
		// When called on the client, clear the local data registry.
		if (!load.getPlayer().getLevel().isClientSide()) {
			// TODO: Change this back later, for now there will only be one team.
			if (TeamManager.get().getTeamForPlayer(load.getPlayer()) == null) {
				if (TeamManager.get().getTeams().size() == 0) {
					TeamManager.get().createTeam(load.getPlayer());
				} else {
					TeamManager.get().getTeams().get(0).addPlayer(load.getPlayer());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent loggedIn) {
		// If on the server, send a sync packet for the static power player data
		// capability.
		if (!loggedIn.getEntity().getCommandSenderWorld().isClientSide()) {
			loggedIn.getPlayer().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
				StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) loggedIn.getPlayer(),
						new PacketSyncStaticPowerPlayerDataCapability(((StaticPowerPlayerData) data).serializeNBT()));
			});

			// Also synchronize all the game data.
			StaticPowerGameDataManager.loadDataForClients();
		}
	}

	@SubscribeEvent
	public static void onBiomeLoading(BiomeLoadingEvent event) {
		ModOres.addOreGenFeatures(event);
		ModWorldFluids.addFluidGenFeatures(event);
		ModTrees.addTreeFeatures(event);
		ModEntities.addSpawns(event);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			if (!event.getObject().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).isPresent()) {
				event.addCapability(STATIC_POWER_PLAYER_DATA, new StaticPowerPlayerCapabilityProvider());
			}
		}
	}

	@SubscribeEvent
	public static void onItemCrafted(ItemCraftedEvent event) {
		if (event.getEntityLiving() instanceof Player) {
			event.getEntityLiving().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
				data.addToCraftingHistory(event.getCrafting(), event.getInventory());
			});
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			// Capture the original tooltips.
			// Allocate the basic tooltips.
			List<Component> basicTooltips = new ArrayList<Component>();

			// Get the advanced tooltips.
			List<Component> advancedToolTips = new ArrayList<Component>();

			// If the item is an ITooltipProvider, capture the tooltips.
			if (event.getItemStack().getItem() instanceof ITooltipProvider) {
				ITooltipProvider spItem = (ITooltipProvider) event.getItemStack().getItem();
				if (spItem != null && event.getPlayer() != null && event.getPlayer().getCommandSenderWorld() != null) {
					spItem.getTooltip(event.getItemStack(), event.getPlayer().getCommandSenderWorld(), basicTooltips, Screen.hasShiftDown());
					spItem.getAdvancedTooltip(event.getItemStack(), event.getPlayer().level, advancedToolTips);
				}
			}

			// Add thermal rate tooltips.
			if (Screen.hasShiftDown()) {
				// Add the basic tooltips if any are presented.
				if (basicTooltips.size() > 0) {
					event.getToolTip().addAll(basicTooltips);
				}

				// Add the advanced.
				if (advancedToolTips.size() > 0) {
					for (Component comp : advancedToolTips) {
						List<String> lines = GuiDrawUtilities.wrapString(comp.getString(), 150);
						for (String line : lines) {
							event.getToolTip().add(new TextComponent(line).setStyle(comp.getStyle()));
						}
					}
				}

				// Create recipe match parameters for checking for heat tooltip values.
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(event.getItemStack());

				// Populate the fluid in the item if one exists.
				FluidUtil.getFluidContained(event.getItemStack()).ifPresent(fluid -> {
					matchParameters.setFluids(fluid.copy());
				});

				// Add the blocks.
				if (event.getItemStack().getItem() instanceof BlockItem) {
					BlockState blockState = ((BlockItem) event.getItemStack().getItem()).getBlock().defaultBlockState();
					matchParameters.setBlocks(blockState);
				}

				// Add the tooltip if the shift key is down.
				StaticPowerRecipeRegistry.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, matchParameters).ifPresent(recipe -> {
					// Add heat conductivity tooltip.
					event.getToolTip().add(HeatTooltipUtilities.getThermalOffsetTooltip(recipe.getThermalOffset()));

					// Add heat generation tooltip.
					if (recipe.getThermalOffset() > 0) {
						event.getToolTip().add(HeatTooltipUtilities.getHeatGenerationTooltip(recipe.getThermalOffset()));
					}

					// Add overheating tooltip.
					if (recipe.hasOverheatingBehaviour()) {
						event.getToolTip().add(HeatTooltipUtilities.getOverheatingTooltip(recipe.getOverheatedTemperature()));
					}
				});

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(), Screen.hasShiftDown());
			} else {
				// Add the basic tooltips if any are presented.
				if (basicTooltips.size() > 0) {
					event.getToolTip().addAll(basicTooltips);
				}

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(), Screen.hasShiftDown());

				// Add the "Hold Shift" indentifier.
				if (advancedToolTips.size() > 0) {
					event.getToolTip().add(new TextComponent(" "));
					event.getToolTip().add(new TranslatableComponent("gui.staticpower.hold_shift").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void onKeyEvent(KeyInputEvent event) {
		ModKeyBindings.onKeyEvent(event);
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	@SubscribeEvent
	public static void resourcesReloadedEvent(RecipesUpdatedEvent event) {
		StaticPowerRecipeRegistry.onResourcesReloaded(event.getRecipeManager());
	}

	@SubscribeEvent
	public static void onPlayerLeftClick(LeftClickBlock event) {
		if (event.getPlayer().getMainHandItem().getItem() instanceof Hammer) {
			Hammer hammer = (Hammer) event.getPlayer().getMainHandItem().getItem();
			if (!event.getPlayer().getCooldowns().isOnCooldown(hammer)) {
				if (hammer.onHitBlockLeftClick(event.getItemStack(), event.getPlayer(), event.getPos(), event.getFace())) {
					event.setCanceled(true);
				}
			}
		}
	}

}
