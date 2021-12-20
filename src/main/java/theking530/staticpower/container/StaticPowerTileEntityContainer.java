package theking530.staticpower.container;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.PacketDistributor;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.network.TileEntityBasicSyncPacket;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public abstract class StaticPowerTileEntityContainer<T extends TileEntityBase> extends StaticPowerContainer {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerTileEntityContainer.class);
	public static final int DEFAULT_SYNC_TIME = 2;

	private final T owningTileEntity;
	private int syncTime;
	private int syncTimer;

	public StaticPowerTileEntityContainer(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int windowId, PlayerInventory inv, T owner) {
		super(allocator, windowId, inv);
		owningTileEntity = owner;
		// This has to be called here and not in the super as the super initializes
		// values that may be needed during the initialization.
		initializeContainer();
		syncTime = DEFAULT_SYNC_TIME;
	}

	/**
	 * Gets the owning tile entity for this container.
	 * 
	 * @return The owning tile entity for this container.
	 */
	public T getTileEntity() {
		return owningTileEntity;
	}

	/**
	 * Override of the parent method to send a {@link TileEntityBasicSyncPacket} on
	 * a set interval as defined by {@link #syncTime}.
	 */
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		// If the sync timer is less than the sync time, increment.
		if (syncTimer < syncTime) {
			syncTimer++;
		}

		// If the sync timer is greater than the sync time, send the machine update
		// packet.
		if (syncTimer >= syncTime) {
			syncTimer = 0;

			// Send a packet to all listening players.
			for (IContainerListener listener : this.listeners) {
				if (listener instanceof ServerPlayerEntity) {
					NetworkMessage msg = new TileEntityBasicSyncPacket(getTileEntity(), false);
					StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) listener), msg);
				}
			}
		}
	}

	/**
	 * Sets how frequently (in ticks) that the update packet is sent to the client.
	 * The higher the number, the better.
	 * 
	 * @param syncTime
	 */
	public void setSyncTime(int syncTime) {
		this.syncTime = syncTime;
	}

	/**
	 * Forces the machine update packet to be sent on the next tick.
	 */
	public void scheduleSync() {
		this.syncTimer = syncTime;
	}

	/**
	 * Gets the tile entity from the provided data packet.
	 * 
	 * @param playerInventory The player's inventory (used to get the world
	 *                        instance).
	 * @param data            The data packet.
	 * @return The {@link TileEntityBase} for this container or null if none was
	 *         encountered.
	 */
	protected static TileEntityBase resolveTileEntityFromDataPacket(final PlayerInventory playerInventory, final PacketBuffer data) {
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof TileEntityBase) {
			return (TileEntityBase) tileAtPos;
		} else {
			LOGGER.error(String.format("Encountered invalid tile entity: %1$s at position: %2$s.", tileAtPos, data.readBlockPos()));
			return null;
		}
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		// Get the priority ordered inventories.
		List<InventoryComponent> inventories = getTileEntity().getPriorityOrderedInventories();

		// Attempt to insert the items.
		for (InventoryComponent inventory : inventories) {
			ItemStack remaining = InventoryUtilities.insertItemIntoInventory(inventory, stack, false);
			if (remaining.getCount() != stack.getCount()) {
				stack.shrink(stack.getCount() - remaining.getCount());
				return true;
			}
		}

		return false;
	}
}
