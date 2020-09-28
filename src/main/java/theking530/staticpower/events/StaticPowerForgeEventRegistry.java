package theking530.staticpower.events;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;
import theking530.api.heat.HeatTooltipUtilities;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.data.PacketSyncTiers;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.TierReloadListener;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.RecipeReloadListener;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
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
	public static void onPlayerJoinedGame(PlayerEvent.PlayerLoggedInEvent playerLoggedIn) {
		if (!playerLoggedIn.getPlayer().getEntityWorld().isRemote) {
			NetworkMessage msg = new PacketSyncTiers(TierReloadListener.TIERS.values());
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) playerLoggedIn.getPlayer()), msg);
			StaticPower.LOGGER.info(String.format("Synced tier configuration to player: %1$s!", playerLoggedIn.getPlayer().getDisplayName().getString()));
		}
	}

	@SubscribeEvent
	public static void onServerAboutToStart(FMLServerAboutToStartEvent serverStarted) {
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) serverStarted.getServer().getDataPackRegistries().getResourceManager();
		resourceManager.addReloadListener(new TierReloadListener());
		resourceManager.addReloadListener(new RecipeReloadListener(serverStarted.getServer().getRecipeManager()));
		StaticPower.LOGGER.info("Server resource reload listener created!");

		TierReloadListener.updateOnServer(resourceManager);
		StaticPower.LOGGER.info("Ore generators registered!");
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
	public static void onBiomeLoading(BiomeLoadingEvent event) {
		ModOres.addOreGenFeatures(event);
		ModTrees.addTreeFeatures(event);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		// Add thermal rate tooltips.
		if (FMLEnvironment.dist == Dist.CLIENT) {
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
