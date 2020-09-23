package theking530.staticpower.events;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModFluids;

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
	@OnlyIn(Dist.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		StaticPowerClientEventHandler.render(event);
	}

	@SubscribeEvent
	public static void onLootLoad(LootTableLoadEvent event) {
		StaticPowerDataRegistry.onLootTableLoaded(event);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		// Add thermal rate tooltips.
		if (Screen.hasShiftDown()) {
			RecipeMatchParameters matchParameters = new RecipeMatchParameters(event.getItemStack());

			FluidUtil.getFluidContained(event.getItemStack()).ifPresent(fluid -> {
				matchParameters.setFluids(fluid.copy());
			});

			StaticPowerRecipeRegistry.getRecipe(ThermalConductivityRecipe.RECIPE_TYPE, matchParameters).ifPresent(recipe -> {
				event.getToolTip().add(HeatTooltipUtilities.getHeatRateTooltip(recipe.getThermalConductivity()));
			});
		}
	}

	/**
	 * This event is raised when the resources are loaded/reloaded.
	 */
	@SubscribeEvent
	public static void resourcesReloadedEvent(RecipesUpdatedEvent event) {
		StaticPowerRecipeRegistry.onResourcesReloaded(event);
		StaticPowerDataRegistry.onResourcesReloaded();
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
