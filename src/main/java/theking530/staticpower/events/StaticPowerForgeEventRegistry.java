package theking530.staticpower.events;

import net.minecraft.item.Items;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.data.StaticPowerDataRegistry;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.utilities.Reference;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class StaticPowerForgeEventRegistry {
	@SubscribeEvent
	public static void worldTickEvent(TickEvent.WorldTickEvent event) {
		if (!event.world.isRemote) {
			StaticPowerServerEventHandler.onWorldTickEvent(event);
		}
	}

	@SubscribeEvent
	public static void render(RenderWorldLastEvent event) {
		StaticPowerClientEventHandler.render(event);
	}

	@SubscribeEvent
	public static void registerItemColors(ColorHandlerEvent.Item event) {
		StaticPowerRegistry.onRegisterItemColors(event);
	}

	@SubscribeEvent
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		StaticPowerClientEventHandler.onAddItemTooltip(event);
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
			StaticPowerCommonEventHandler.onMilkBucketUsed(event);
		}
	}
}
