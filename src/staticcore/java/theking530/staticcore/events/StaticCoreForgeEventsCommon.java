package theking530.staticcore.events;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
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
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
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
import theking530.api.attributes.capability.AttributableCapabilityProvider;
import theking530.api.attributes.rendering.ItemAttributeRegistration;
import theking530.api.attributes.type.AttributeType;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.StaticCore;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.commands.ResearchCommands;
import theking530.staticcore.commands.TeamCommands;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.RecipeReloadListener;
import theking530.staticcore.crafting.StaticCoreRecipeManager;
import theking530.staticcore.crafting.thermal.ThermalConductivityRecipe;
import theking530.staticcore.data.gamedata.StaticCoreGameDataManager.StaticCoreDataAccessor;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.gui.GuiDrawUtilities;
import theking530.staticcore.init.StaticCoreKeyBindings;
import theking530.staticcore.init.StaticCoreRecipeTypes;
import theking530.staticcore.teams.TeamManager;
import theking530.staticcore.utilities.ITooltipProvider;

@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class StaticCoreForgeEventsCommon {
	public static Path dataPath;

	@SubscribeEvent
	public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
		ResearchCommands.register(commandDispatcher);
		TeamCommands.register(commandDispatcher);
	}

	@SubscribeEvent
	public static void serverTickEvent(TickEvent.ServerTickEvent event) {
		Level overworld = event.getServer().overworld();

		if (event.phase == TickEvent.Phase.START) {
			CableNetworkAccessor.get(overworld).preWorldTick();
		}

		if (event.phase == TickEvent.Phase.END) {
			CableNetworkAccessor.get(event.getServer().overworld()).tick();
			StaticCoreDataAccessor.getServer().tickGameData(overworld);
		}
	}

	@SubscribeEvent
	public static void mappingChanged(IdMappingEvent evt) {
		FluidIngredient.invalidateAll();
	}

	@SubscribeEvent
	public static void onServerAboutToStartEvent(ServerAboutToStartEvent serverStarted) {
		dataPath = serverStarted.getServer().getWorldPath(new LevelResource("staticcore"));

		// The following attempts to create the data path but does nothing if it already
		// exists.
		try {
			Files.createDirectories(dataPath);
		} catch (IOException e) {
			throw new RuntimeException("An error occured when attempting to create the Static Core data storage path.",
					e);
		}
		StaticCoreDataAccessor.createForServer();
		StaticCoreDataAccessor.getServer().load();
	}

	@SubscribeEvent
	public static void onServerStopping(ServerStoppedEvent serverStopped) {
		StaticCoreDataAccessor.unloadForServer();
	}

	@SubscribeEvent
	public static void onSave(Save save) {
		if (!save.getLevel().isClientSide() && StaticCoreDataAccessor.getServer() != null) {
			StaticCoreDataAccessor.getServer().save();
		}
	}

	@SubscribeEvent
	public static void onPlayerLoad(PlayerEvent.LoadFromFile load) {

	}

	@SubscribeEvent
	public static void onServerPlayerLoggedIn(PlayerLoggedInEvent loggedIn) {
		StaticCoreDataAccessor.getServer().loadDataForClient((ServerPlayer) loggedIn.getEntity());
		// TODO: Change this back later, for now there will only be one team.
		if (TeamManager.get(loggedIn.getEntity().getLevel()).getTeamForPlayer(loggedIn.getEntity()) == null) {
			if (TeamManager.get(loggedIn.getEntity().getLevel()).getTeams().size() == 0) {
				TeamManager.get(loggedIn.getEntity().getLevel()).createTeam(loggedIn.getEntity());
			} else {
				TeamManager.get(loggedIn.getEntity().getLevel()).getTeams().get(0).addPlayer(loggedIn.getEntity());
			}
		}
	}

	@SubscribeEvent
	public static void addReloadListenerEvent(AddReloadListenerEvent reloadEvent) {
		reloadEvent.addListener(new RecipeReloadListener(reloadEvent.getServerResources().getRecipeManager()));
		StaticCore.LOGGER.info("Server resource reload listener created!");
	}

	@SubscribeEvent
	public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject() instanceof ItemStack) {
			ItemAttributeRegistration registration = ItemAttributeRegistry.get(event.getObject().getItem());
			if (registration == null) {
				return;
			}

			// Add all the attributes to the item stack.
			AttributableCapabilityProvider handler = new AttributableCapabilityProvider();
			for (AttributeType<?> attribute : registration.getAttributes()) {
				handler.addAttribute(registration.getAttribute(attribute));
			}
			event.addCapability(new ResourceLocation(StaticCore.MOD_ID, "attributes"), handler);
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
					spItem.getTooltip(event.getItemStack(), event.getEntity().getCommandSenderWorld(), basicTooltips,
							Screen.hasShiftDown());
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
				@SuppressWarnings("resource")
				Level level = Minecraft.getInstance().level;
				RecipeManager recipeManager = level.getRecipeManager();
				Optional<ThermalConductivityRecipe> thermalRecipe = recipeManager.getRecipeFor(
						StaticCoreRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(), matchParameters, level);
				thermalRecipe.ifPresent(recipe -> {
					// Add heat conductivity tooltip.
					event.getToolTip().add(HeatTooltipUtilities.getHeatConductivityTooltip(recipe.getConductivity()));

					// Add temperature tooltip.
					if (recipe.hasActiveTemperature()) {
						event.getToolTip()
								.add(HeatTooltipUtilities.getActiveTemperatureTooltip(recipe.getTemperature()));
					}

					// Add overheating tooltip.
					if (recipe.hasOverheatingBehaviour()) {
						event.getToolTip().add(HeatTooltipUtilities
								.getOverheatingTooltip(recipe.getOverheatingBehaviour().getTemperature()));
					}

					// Add freezing tooltip.
					if (recipe.hasFreezeBehaviour()) {
						event.getToolTip().add(HeatTooltipUtilities
								.getFreezingTooltip(recipe.getFreezingBehaviour().getTemperature()));
					}
				});

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(),
						Screen.hasShiftDown());
			} else {
				// Add the basic tooltips if any are presented.
				for (Component original : basicTooltips) {
					List<String> lines = GuiDrawUtilities.wrapString(original.getString(), 175);
					for (String line : lines) {
						event.getToolTip().add(Component.literal(line).setStyle(original.getStyle()));
					}
				}

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(),
						Screen.hasShiftDown());

				// Add the "Hold Shift" indentifier.
				if (advancedToolTips.size() > 0) {
					event.getToolTip().add(Component.translatable("gui.staticcore.hold_shift")
							.withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
	public static void onKeyEvent(InputEvent.Key event) {
		StaticCoreKeyBindings.onKeyEvent(event);
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	@SubscribeEvent
	public static void resourcesReloadedEvent(RecipesUpdatedEvent event) {
		StaticCoreRecipeManager.onResourcesReloaded(event.getRecipeManager());
	}
}
