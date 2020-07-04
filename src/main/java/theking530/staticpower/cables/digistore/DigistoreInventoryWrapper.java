package theking530.staticpower.cables.digistore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreInventoryWrapper implements IItemHandler {
	private final List<ItemStack> stacks;
	private final DigistoreNetworkModule module;

	public DigistoreInventoryWrapper(DigistoreNetworkModule module) {
		this.module = module;
		stacks = new ArrayList<ItemStack>();

		// Perform an initial update when first created.
		update();
	}

	public void update() {
		// Start profiling.
		Minecraft.getInstance().getProfiler().startSection("DigistoreInventoryBuilding");
		
		// First clear the stack array.
		stacks.clear();

		// Popualate the stacks.
		for (TileEntityDigistore digistore : module.getAllDigistores()) {
			if (digistore.isEmpty()) {
				continue;
			}
			int indexOfItem = getItemIndex(digistore.getStoredItem());
			if (indexOfItem >= 0) {
				stacks.get(indexOfItem).grow(digistore.getStoredAmount());
			} else {
				ItemStack digistoreCopy = digistore.getStoredItem().copy();
				digistoreCopy.setCount(digistore.getStoredAmount());
				stacks.add(digistoreCopy);
			}
		}

		// Sort by stack size descending.
		stacks.sort(new Comparator<ItemStack>() {
			@Override
			public int compare(ItemStack o1, ItemStack o2) {
				return o2.getCount() - o1.getCount();
			}
		});
		
		// End profiling.
		Minecraft.getInstance().getProfiler().endSection();
	}

	@Override
	public int getSlots() {
		return stacks.size();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return stacks.get(slot);
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return module.insertItem(stack, simulate);
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stackInSlot = stacks.get(slot);
		return module.extractItem(stackInSlot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	protected int getItemIndex(ItemStack stack) {
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack test = stacks.get(i);
			if (ItemUtilities.areItemStacksStackable(test, stack)) {
				return i;
			}
		}
		return -1;
	}
}
