package theking530.staticpower.tileentities.powered.autosolderingtable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.tileentities.nonpowered.solderingtable.AbstractContainerSolderingTable;

public class ContainerAutoSolderingTable extends AbstractContainerSolderingTable<TileEntityAutoSolderingTable> {

	public ContainerAutoSolderingTable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityAutoSolderingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoSolderingTable(int windowId, PlayerInventory playerInventory, TileEntityAutoSolderingTable owner) {
		super(ModContainerTypes.AUTO_SOLDERING_TABLE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		enableSolderingIronSlot = false;
		craftingGridXOffset = -18;
		super.initializeContainer();

		// Add the soldering iron slot.
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 57));
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		// Get the slot and the slot's contents.
		Slot slot = inventorySlots.get(slotIndex);
		ItemStack stack = slot.getStack();

		// If we shift clicked an item in the soldering table inventory, move it to the
		// player inventory. If we shift clicked in the player inventory, attempt to
		// move the item to the soldering iron inventory.
		if (slotIndex <= 19) {
			if (!mergeItemStack(stack, 19, 48, false)) {
				return stack;
			}
		} else {
			if (!mergeItemStack(stack, 9, 18, false)) {
				return stack;
			}
		}

		return stack;
	}

	@Override
	protected void addOutputSlot() {
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 129, 38));
	}
}
