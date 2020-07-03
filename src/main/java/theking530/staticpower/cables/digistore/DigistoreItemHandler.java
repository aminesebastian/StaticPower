package theking530.staticpower.cables.digistore;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class DigistoreItemHandler implements IItemHandler {
	private final DigistoreNetworkModule owningModule;

	public DigistoreItemHandler(DigistoreNetworkModule module) {
		this.owningModule = module;
	}

	@Override
	public int getSlots() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return null;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return null;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return null;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return false;
	}

}
