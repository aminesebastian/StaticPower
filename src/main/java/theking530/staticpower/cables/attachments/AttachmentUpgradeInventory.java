package theking530.staticpower.cables.attachments;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandlerModifiable;
import theking530.api.IUpgradeItem;
import theking530.api.IUpgradeItem.UpgradeType;
import theking530.staticpower.utilities.ItemUtilities;

public class AttachmentUpgradeInventory implements IItemHandlerModifiable {
	protected ItemStack owningUpgrade;

	public AttachmentUpgradeInventory(ItemStack owningUpgrade) {
		this.owningUpgrade = owningUpgrade;
	}

	@Override
	public int getSlots() {
		return getInventory().size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ItemStack.read(getInventory().getCompound(slot));
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		CompoundNBT itemNbt = new CompoundNBT();
		stack.write(itemNbt);
		getInventory().set(slot, stack.write(itemNbt));
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		// If the item is not valid, do nothing.
		if (!isItemValid(slot, stack)) {
			return stack;
		}

		// Check to see if the items are stackable.
		ItemStack stackInSlot = getStackInSlot(slot);
		if (!stackInSlot.isEmpty() && !ItemUtilities.areItemStacksStackable(stackInSlot, stack)) {
			return stack;
		}

		// Check to see if the slot isn't full.
		if (stackInSlot.getCount() == stack.getMaxStackSize()) {
			return stack;
		}

		// Calculate the amount to transfer. If not simulating, perform the insert.
		// Then, shrink the input by the amount transfered and return it.
		int amountToTransfer = Math.min(stack.getCount(), stack.getMaxStackSize() - stackInSlot.getCount());
		if (!simulate) {
			stackInSlot.grow(amountToTransfer);
			setStackInSlot(slot, stackInSlot);
		}
		stack.shrink(amountToTransfer);
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		// Check to see if the items are stackable.
		ItemStack stackInSlot = getStackInSlot(slot);
		if (stackInSlot.isEmpty()) {
			return stackInSlot;
		}
		// Calculate the amount to transfer. If not simulating, perform the extract.
		// Otherwise, simulate it.
		int amountToTransfer = Math.min(amount, stackInSlot.getCount());
		ItemStack output = stackInSlot.copy();
		output.setCount(amountToTransfer);
		;

		if (!simulate) {
			stackInSlot.shrink(amountToTransfer);
			setStackInSlot(slot, stackInSlot);
		}

		return output;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 64;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if (stack.getItem() instanceof IUpgradeItem) {
			IUpgradeItem upgradeItem = (IUpgradeItem) stack.getItem();
			return upgradeItem.isOfType(UpgradeType.DIGISTORE_ATTACHMENT);
		}
		return false;
	}

	protected ListNBT getInventory() {
		return owningUpgrade.getTag().getList(AbstractCableAttachment.UPGRADE_INVENTORY_TAG, Constants.NBT.TAG_COMPOUND);
	}
}
