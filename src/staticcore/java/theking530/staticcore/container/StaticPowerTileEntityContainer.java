package theking530.staticcore.container;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.network.BlockEntityBasicSyncPacket;
import theking530.staticcore.utilities.item.InventoryUtilities;

public abstract class StaticPowerTileEntityContainer<T extends BlockEntityBase> extends StaticCoreContainerMenu {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerTileEntityContainer.class);
	public static final int DEFAULT_SYNC_TIME = 1;

	private final T owningTileEntity;
	private int syncTime;
	private int syncTimer;

	public StaticPowerTileEntityContainer(ContainerTypeAllocator<? extends StaticCoreContainerMenu, ?> allocator, int windowId, Inventory inv, T owner) {
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
	 * Override of the parent method to send a {@link BlockEntityBasicSyncPacket} on
	 * a set interval as defined by {@link #syncTime}.
	 */
	@Override
	public void broadcastChanges() {
		super.broadcastChanges();
		syncTimer++;

		// If the sync timer has passed a sync time interval, perform a sync.
		if (syncTimer % DEFAULT_SYNC_TIME == 0 && containerListeners.size() > 0) {
			if (getPlayerInventory().player instanceof ServerPlayer) {
				getTileEntity().synchronizeDataToContainerListener((ServerPlayer) getPlayerInventory().player);
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
	 * @return The {@link BlockEntityBase} for this container or null if none was
	 *         encountered.
	 */
	protected static BlockEntityBase resolveTileEntityFromDataPacket(final Inventory playerInventory, final FriendlyByteBuf data) {
		final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
		if (tileAtPos instanceof BlockEntityBase) {
			return (BlockEntityBase) tileAtPos;
		} else {
			LOGGER.error(String.format("Encountered invalid tile entity: %1$s at position: %2$s.", tileAtPos, data.readBlockPos()));
			return null;
		}
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
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
