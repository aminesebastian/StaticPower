package theking530.staticpower.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticpower.StaticPower;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.entities.player.datacapability.PacketSyncStaticPowerPlayerDataCapability;
import theking530.staticpower.entities.player.datacapability.StaticPowerPlayerCapabilityProvider;
import theking530.staticpower.entities.player.datacapability.StaticPowerPlayerData;
import theking530.staticpower.items.backpack.Backpack;
import theking530.staticpower.items.tools.Hammer;
import theking530.staticpower.network.StaticPowerMessageHandler;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class StaticPowerForgeEventsCommon {
	public static final ResourceLocation STATIC_POWER_PLAYER_DATA = new ResourceLocation(StaticPower.MOD_ID, "player_data");

	@SubscribeEvent
	public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			event.player.getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((playerCap) -> {
				playerCap.tick(event);
			});
		}
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent serverStopped) {
		StaticPowerGameDataManager.clearAllGameData();
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
	public static void onItemCrafted(ItemCraftedEvent event) {
		event.getEntity().getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
			data.addToCraftingHistory(event.getCrafting(), event.getInventory());
		});
	}

	@SubscribeEvent
	public static void onItemPickedUp(EntityItemPickupEvent event) {
		handleBackpackPickups(event);
	}

	private static void handleBackpackPickups(EntityItemPickupEvent event) {
		ItemStack remaining = event.getItem().getItem().copy();
		ItemStack originalItem = remaining.copy();

		for (int i = 0; i < event.getEntity().getInventory().getContainerSize(); i++) {
			if (event.getEntity().getInventory().getItem(i).getItem() instanceof Backpack) {
				Backpack backpackItem = (Backpack) event.getEntity().getInventory().getItem(i).getItem();
				remaining = backpackItem.playerPickedUpItem(event.getEntity().getInventory().getItem(i), remaining, event.getEntity());

				// If there is no more item to insert, just break.
				if (remaining.isEmpty()) {
					break;
				}
			}
		}

		// Note, we do NOT discard the item or block the event here, there is logic in
		// the item that does
		// so on tick anyway.
		// I'd rather not emulate vanilla code here in case that behaviour changes in
		// the future.
		// We do fire the events here however.
		if (originalItem.getCount() != remaining.getCount()) {
			event.getItem().setItem(remaining);

			ItemStack pickedUpItem = originalItem.copy();
			pickedUpItem.setCount(originalItem.getCount() - remaining.getCount());

			event.getEntity().awardStat(Stats.ITEM_PICKED_UP.get(pickedUpItem.getItem()), pickedUpItem.getCount());
			net.minecraftforge.event.ForgeEventFactory.firePlayerItemPickupEvent(event.getEntity(), event.getItem(), pickedUpItem.copy());
			event.getEntity().onItemPickup(event.getItem());
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onAddItemTooltip(ItemTooltipEvent event) {
		if (FMLEnvironment.dist == Dist.CLIENT) {

		}
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
