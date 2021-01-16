package theking530.staticpower.events;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.attributes.AttributeUtilities;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticcore.utilities.ITooltipProvider;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.RecipeReloadListener;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.world.ore.ModOres;
import theking530.staticpower.world.trees.ModTrees;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class StaticPowerForgeEventRegistry {
	@SubscribeEvent
	public static void worldTickEvent(TickEvent.WorldTickEvent event) {
		if (!event.world.isRemote) {
			if (event.phase == TickEvent.Phase.END) {
				CableNetworkManager.get(event.world).tick();
			}
		}
	}

	@SubscribeEvent
	public static void onServerAboutToStart(FMLServerAboutToStartEvent serverStarted) {
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) serverStarted.getServer().getDataPackRegistries().getResourceManager();
		resourceManager.addReloadListener(new RecipeReloadListener(serverStarted.getServer().getRecipeManager()));
		StaticPowerRecipeRegistry.onResourcesReloaded(serverStarted.getServer().getRecipeManager());
		StaticPower.LOGGER.info("Server resource reload listener created!");
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		StaticPowerClientEventHandler.onWorldRender(event);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void renderBlockHighlights(DrawHighlightEvent.HighlightBlock event) {
		StaticPowerClientEventHandler.renderMultiHarvestBoundingBoxes(event);
	}

	@SubscribeEvent
	public static void onBiomeLoading(BiomeLoadingEvent event) {
		ModOres.addOreGenFeatures(event);
		ModTrees.addTreeFeatures(event);
		ModEntities.addSpawns(event);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			// Capture the original tooltips.
			// Allocate the basic tooltips.
			List<ITextComponent> basicTooltips = new ArrayList<ITextComponent>();

			// Get the advanced tooltips.
			List<ITextComponent> advancedToolTips = new ArrayList<ITextComponent>();

			// If the item is an ITooltipProvider, capture the tooltips.
			if (event.getItemStack().getItem() instanceof ITooltipProvider) {
				ITooltipProvider spItem = (ITooltipProvider) event.getItemStack().getItem();
				if (spItem != null && event.getPlayer() != null && event.getPlayer().getEntityWorld() != null) {
					spItem.getTooltip(event.getItemStack(), event.getPlayer().getEntityWorld(), basicTooltips, Screen.hasControlDown());
					spItem.getAdvancedTooltip(event.getItemStack(), event.getPlayer().world, advancedToolTips);
				}
			}

			// Add thermal rate tooltips.
			if (Screen.hasControlDown()) {
				// Add the basic tooltips if any are presented.
				if (basicTooltips.size() > 0) {
					event.getToolTip().addAll(basicTooltips);
				}

				// Add the advanced.
				if (advancedToolTips.size() > 0) {
					event.getToolTip().addAll(advancedToolTips);
				}

				// Create recipe match parameters for checking for heat tooltip values.
				RecipeMatchParameters matchParameters = new RecipeMatchParameters(event.getItemStack());

				// Populate the fluid in the item if one exists.
				FluidUtil.getFluidContained(event.getItemStack()).ifPresent(fluid -> {
					matchParameters.setFluids(fluid.copy());
				});

				// Add the blocks.
				if (event.getItemStack().getItem() instanceof BlockItem) {
					BlockState blockState = ((BlockItem) event.getItemStack().getItem()).getBlock().getDefaultState();
					matchParameters.setBlocks(blockState);
				}

				// Add the tooltip if the control key is down.
				StaticPowerRecipeRegistry.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, matchParameters).ifPresent(recipe -> {
					// Add heat conductivity tooltip.
					event.getToolTip().add(HeatTooltipUtilities.getHeatConductivityTooltip(recipe.getThermalConductivity()));

					// Add heat generation tooltip.
					if (recipe.getHeatAmount() > 0) {
						event.getToolTip().add(HeatTooltipUtilities.getHeatGenerationTooltip(recipe.getHeatAmount()));
					}

					// Add overheating tooltip.
					if (recipe.hasOverheatingBehaviour()) {
						event.getToolTip().add(HeatTooltipUtilities.getOverheatingTooltip(recipe.getOverheatedTemperature()));
					}
				});

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(), Screen.hasControlDown());
			} else {
				// Add the basic tooltips if any are presented.
				if (basicTooltips.size() > 0) {
					event.getToolTip().addAll(basicTooltips);
				}

				// Add attributable tooltips.
				AttributeUtilities.addTooltipsForAttribute(event.getItemStack(), event.getToolTip(), Screen.hasControlDown());

				// Add the "Hold Control" indentifier.
				if (advancedToolTips.size() > 0) {
					event.getToolTip().add(new StringTextComponent(" "));
					event.getToolTip().add(new TranslationTextComponent("gui.staticpower.hold_control").mergeStyle(TextFormatting.ITALIC).mergeStyle(TextFormatting.GRAY));
				}
			}
		}
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	@SubscribeEvent
	public static void resourcesReloadedEvent(RecipesUpdatedEvent event) {
		StaticPowerRecipeRegistry.onResourcesReloaded(event.getRecipeManager());
	}

	@SubscribeEvent
	public static void onPlayerInteract(RightClickBlock event) {
		if (event.getPlayer().getHeldItemMainhand().getItem() == Items.MILK_BUCKET) {
			// Create a proxy item to handle all the fluid placement logic for the milk.
			BucketItem milkeBucketProxy = new BucketItem(() -> ModFluids.Milk.Fluid, new Item.Properties());

			// Call the on item rightclick logic.
			milkeBucketProxy.onItemRightClick(event.getWorld(), event.getPlayer(), event.getHand());

			// If not creative, set the held item to the proxy bucket.
			if (!event.getPlayer().isCreative()) {
				event.getPlayer().setHeldItem(event.getHand(), new ItemStack(milkeBucketProxy));
			}

			// Cancel the event so no further events occur.
			event.setCanceled(true);
		}
	}
}
