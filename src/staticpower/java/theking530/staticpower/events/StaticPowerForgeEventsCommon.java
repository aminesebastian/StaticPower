package theking530.staticpower.events;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.level.LevelEvent.Load;
import net.minecraftforge.event.level.LevelEvent.Save;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IdMappingEvent;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.attributes.ItemAttributeRegistry;
import theking530.api.attributes.capability.AttributeableHandler;
import theking530.api.attributes.rendering.ItemAttributeRegistration;
import theking530.api.attributes.type.AttributeType;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.RecipeReloadListener;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.entities.player.datacapability.PacketSyncStaticPowerPlayerDataCapability;
import theking530.staticpower.entities.player.datacapability.StaticPowerPlayerCapabilityProvider;
import theking530.staticpower.entities.player.datacapability.StaticPowerPlayerData;
import theking530.staticpower.init.ModCommands.ResearchCommands;
import theking530.staticpower.init.ModKeyBindings;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.items.backpack.Backpack;
import theking530.staticpower.items.tools.Hammer;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.TeamManager;

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
	public static void worldTickEvent(TickEvent.LevelTickEvent event) {
		if (!event.level.isClientSide) {
			if (event.phase == TickEvent.Phase.START) {
				CableNetworkAccessor.get(event.level).preWorldTick();
			} else if (event.phase == TickEvent.Phase.END) {
				CableNetworkAccessor.get(event.level).tick();
				StaticPowerGameDataManager.tickGameData(event.level);
			}
		}
	}

	@SubscribeEvent
	public static void mappingChanged(IdMappingEvent evt) {
		FluidIngredient.invalidateAll();
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
		StaticPowerGameDataManager.clearAllGameData();
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent serverStopped) {
		StaticPowerGameDataManager.clearAllGameData();
	}

	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent reloadEvent) {
		reloadEvent.addListener(new RecipeReloadListener(reloadEvent.getServerResources().getRecipeManager()));
		StaticPower.LOGGER.info("Server resource reload listener created!");
	}

	@SubscribeEvent
	public static void onLoad(Load load) {
		if (!load.getLevel().isClientSide()) {
			StaticPowerGameDataManager.loadDataFromDisk(load);
		}
	}

	@SubscribeEvent
	public static void onSave(Save save) {
		if (!save.getLevel().isClientSide()) {
			StaticPowerGameDataManager.saveDataToDisk(save);
		}
	}

	@SubscribeEvent
	public static void onPlayerLoad(PlayerEvent.LoadFromFile load) {
		// When called on the server, add the player to a team if one does not exist.
		// When called on the client, clear the local data registry.
		if (!load.getEntity().getLevel().isClientSide()) {
			// TODO: Change this back later, for now there will only be one team.
			if (TeamManager.get(load.getEntity().getLevel()).getTeamForPlayer(load.getEntity()) == null) {
				if (TeamManager.get(load.getEntity().getLevel()).getTeams().size() == 0) {
					TeamManager.get(load.getEntity().getLevel()).createTeam(load.getEntity());
				} else {
					TeamManager.get(load.getEntity().getLevel()).getTeams().get(0).addPlayer(load.getEntity());
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent loggedIn) {
		// If on the server, send a sync packet for the static power player data
		// capability.
		if (!loggedIn.getEntity().level.isClientSide()) {
			loggedIn.getEntity().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
				StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) loggedIn.getEntity(),
						new PacketSyncStaticPowerPlayerDataCapability(((StaticPowerPlayerData) data).serializeNBT()));
			});

			// Also synchronize all the game data.
			StaticPowerGameDataManager.loadDataForClients();
		}
	}

	@SubscribeEvent
	public static void onAttachEventCapabilities(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			if (!event.getObject().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).isPresent()) {
				event.addCapability(STATIC_POWER_PLAYER_DATA, new StaticPowerPlayerCapabilityProvider());
			}
		}
	}

	@SubscribeEvent
	public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject() instanceof ItemStack) {
			ItemAttributeRegistration registration = ItemAttributeRegistry.get(event.getObject().getItem());
			if (registration == null) {
				return;
			}

			// Add all the attributes to the item stack.
			AttributeableHandler handler = new AttributeableHandler();
			for (AttributeType<?> attribute : registration.getAttributes()) {
				handler.addAttribute(registration.getAttribute(attribute));
			}
			event.addCapability(new ResourceLocation(StaticPower.MOD_ID, "attributes"), handler);
		}
	}

	@SubscribeEvent
	public static void onItemCrafted(ItemCraftedEvent event) {
		event.getEntity().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
			data.addToCraftingHistory(event.getCrafting(), event.getInventory());
		});
	}

	@SubscribeEvent
	public static void onItemPickedUp(EntityItemPickupEvent event) {
		for (int i = 0; i < event.getEntity().getInventory().getContainerSize(); i++) {
			if (event.getEntity().getInventory().getItem(i).getItem() instanceof Backpack) {
				Backpack backpackItem = (Backpack) event.getEntity().getInventory().getItem(i).getItem();
				backpackItem.playerPickedUpItem(event.getEntity().getInventory().getItem(i), event.getItem(), event.getEntity());

				// If there is no more item to insert, just break.
				if (event.getItem().getItem().isEmpty()) {
					break;
				}
			}
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
				if (spItem != null && event.getEntity() != null && event.getEntity().getCommandSenderWorld() != null) {
					spItem.getTooltip(event.getItemStack(), event.getEntity().getCommandSenderWorld(), basicTooltips, Screen.hasShiftDown());
					spItem.getAdvancedTooltip(event.getItemStack(), event.getEntity().level, advancedToolTips);
				}
			}

			// Add thermal rate tooltips.
			if (Screen.hasShiftDown()) {
				// Add the basic tooltips if any are presented.
				if (basicTooltips.size() > 0) {
					event.getToolTip().addAll(basicTooltips);
				}

				// Add the advanced.
				for (Component comp : advancedToolTips) {
					List<String> lines = GuiDrawUtilities.wrapString(comp.getString(), 175);
					for (String line : lines) {
						event.getToolTip().add(Component.literal(line).setStyle(comp.getStyle()));
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
				StaticPowerRecipeRegistry.getRecipe(ModRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(), matchParameters).ifPresent(recipe -> {
					// Add heat conductivity tooltip.
					event.getToolTip().add(HeatTooltipUtilities.getHeatConductivityTooltip(recipe.getConductivity()));

					// Add temperature tooltip.
					if (recipe.hasActiveTemperature()) {
						event.getToolTip().add(HeatTooltipUtilities.getActiveTemperatureTooltip(recipe.getTemperature()));
					}

					// Add overheating tooltip.
					if (recipe.hasOverheatingBehaviour()) {
						event.getToolTip().add(HeatTooltipUtilities.getOverheatingTooltip(recipe.getOverheatedTemperature()));
					}

					// Add freezing tooltip.
					if (recipe.hasFreezeBehaviour()) {
						event.getToolTip().add(HeatTooltipUtilities.getFreezingTooltip(recipe.getFreezingTemperature()));
					}
				});

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(), Screen.hasShiftDown());
			} else {
				// Add the basic tooltips if any are presented.
				for (Component original : basicTooltips) {
					List<String> lines = GuiDrawUtilities.wrapString(original.getString(), 175);
					for (String line : lines) {
						event.getToolTip().add(Component.literal(line).setStyle(original.getStyle()));
					}
				}

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(), Screen.hasShiftDown());

				// Add the "Hold Shift" indentifier.
				if (advancedToolTips.size() > 0) {
					event.getToolTip().add(Component.translatable("gui.staticpower.hold_shift").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public static void onKeyEvent(InputEvent.Key event) {
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
		if (event.getEntity().getMainHandItem().getItem() instanceof Hammer) {
			Hammer hammer = (Hammer) event.getEntity().getMainHandItem().getItem();
			if (!event.getEntity().getCooldowns().isOnCooldown(hammer)) {
				if (hammer.onHitBlockLeftClick(event.getItemStack(), event.getEntity(), event.getPos(), event.getFace())) {
					event.setCanceled(true);
				}
			}
		}
	}

}
