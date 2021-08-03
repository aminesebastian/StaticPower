package theking530.staticpower.tileentities.digistorenetwork.digistore;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.items.DigistoreCard;
import theking530.staticpower.tileentities.TileEntityUpdateRequest;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;

public class DigistoreInventoryComponent extends InventoryComponent implements IDigistoreInventory {
	public DigistoreInventoryComponent(String name, int slotCount) {
		super(name, slotCount, MachineSideMode.Regular);
		setFilter(new ItemStackHandlerFilter() {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				return stack.getCapability(CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY).isPresent();
			}
		});
	}

	@Override
	public CompoundNBT serializeNBT() {
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

	@Override
	public void setLockState(boolean locked) {
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			DigistoreCard.getInventory(card).setLockState(locked);
		}
	}

	@Override
	public int getCountForItem(ItemStack stack) {
		int outputCount = 0;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			outputCount += DigistoreCard.getInventory(card).getCountForItem(stack);
		}
		return outputCount;
	}

	@Override
	public DigistoreStack getDigistoreStack(int index) {
		int curr = index;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			IDigistoreInventory cardInv = DigistoreCard.getInventory(card);
			if (curr > cardInv.getUniqueItemCapacity() - 1) {
				curr -= cardInv.getUniqueItemCapacity();
			} else {
				return cardInv.getDigistoreStack(curr);
			}
		}
		return null;
	}

	@Override
	public int getCurrentUniqueItemTypeCount() {
		int outputCount = 0;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			outputCount += DigistoreCard.getInventory(card).getCurrentUniqueItemTypeCount();
		}
		return outputCount;
	}

	@Override
	public int getUniqueItemCapacity() {
		int outputCount = 0;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			outputCount += DigistoreCard.getInventory(card).getUniqueItemCapacity();
		}
		return outputCount;
	}

	@Override
	public int getItemCapacity() {
		int outputCount = 0;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			outputCount += DigistoreCard.getInventory(card).getItemCapacity();
		}
		return outputCount;
	}

	@Override
	public int getTotalContainedCount() {
		int outputCount = 0;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			outputCount += DigistoreCard.getInventory(card).getTotalContainedCount();
		}
		return outputCount;
	}

	@Override
	public int getRemainingStorage(boolean ignoreVoid) {
		int outputCount = 0;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			outputCount += DigistoreCard.getInventory(card).getRemainingStorage(ignoreVoid);
		}
		return outputCount;
	}

	@Override
	public ItemStack insertItem(ItemStack stack, boolean simulate) {
		int initialCount = stack.getCount();

		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			stack = DigistoreCard.getInventory(card).insertItem(stack, simulate);
			if (stack.isEmpty()) {
				break;
			}
		}

		// Raise the onChanged method if the contents changed.
		if (stack.getCount() != initialCount) {
			onChanged();
		}

		return stack;
	}

	@Override
	public ItemStack extractItem(ItemStack stack, int count, boolean simulate) {
		ItemStack output = ItemStack.EMPTY;
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			ItemStack extracted = DigistoreCard.getInventory(card).extractItem(stack, count - output.getCount(), simulate);
			if (!extracted.isEmpty()) {
				if (!output.isEmpty()) {
					output.grow(extracted.getCount());
				} else {
					output = extracted.copy();
				}
			}
			if (output.getCount() >= count) {
				break;
			}
		}
		// Raise the onChanged method if we were able to extract contents.
		if (output.getCount() > 0) {
			onChanged();
		}
		return output;
	}

	@Override
	public boolean shouldVoidExcess() {
		return false;
	}

	@Override
	public boolean canAcceptItem(ItemStack item) {
		for (ItemStack card : this) {
			if (card.isEmpty()) {
				continue;
			}
			if (DigistoreCard.getInventory(card).canAcceptItem(item)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityDigistoreInventory.DIGISTORE_INVENTORY_CAPABILITY) {
			return LazyOptional.of(() -> {
				return this;
			}).cast();
		}

		return super.provideCapability(cap, side);
	}

	public float getFilledRatio() {
		return (float) getTotalContainedCount() / (float) getItemCapacity();
	}

	public void onChanged() {
		// We do this only to sync the mono card render bar.
		if (getWorld().isRemote()) {
			getTileEntity().requestModelDataUpdate();
		} else {
			getTileEntity().addUpdateRequest(TileEntityUpdateRequest.syncDataOnly(), true);
		}
	}
}
