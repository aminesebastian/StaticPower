package theking530.staticpower.cables.digistore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.DigistoreInventoryComponent;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreInventoryWrapper implements IItemHandler {
	private final List<ItemStack> stacks;
	private final DigistoreNetworkModule module;
	private final String filterString;
	private final DigistoreInventorySortType sortType;
	private final boolean sortDescending;

	public DigistoreInventoryWrapper(DigistoreNetworkModule module, String filter, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.module = module;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
		stacks = new ArrayList<ItemStack>();
		filterString = filter.toLowerCase();

		// Perform an initial update when first created.
		update();
	}

	public void update() {
		// Start profiling.
		Minecraft.getInstance().getProfiler().startSection("DigistoreInventoryBuilding");

		// First clear the stack array.
		stacks.clear();

		// Populate the stacks.
		for (DigistoreInventoryComponent digistore : module.getAllDigistores()) {
			for (int i = 0; i < digistore.getSlots(); i++) {
				// Stack in slot.
				ItemStack stackInSlot = digistore.getStackInSlot(i);

				// Skip items that don't match the filter.
				if (filterString.length() > 0) {
					if (filterString.startsWith("@") && filterString.length() > 1) {
						if (!stackInSlot.getItem().getRegistryName().getNamespace().toLowerCase().contains(filterString.substring(1))) {
							continue;
						}
					} else if (!stackInSlot.getDisplayName().getFormattedText().toLowerCase().contains(this.filterString)) {
						continue;
					}
				}

				// Get the index of the item if we have already stored it.
				int indexOfItem = getItemIndex(stackInSlot);

				// Increment the count if we have, otherwise create a new entry.
				if (indexOfItem >= 0) {
					stacks.get(indexOfItem).grow(digistore.getCountInSlot(i));
				} else {
					stacks.add(ItemHandlerHelper.copyStackWithSize(stackInSlot, digistore.getCountInSlot(i)));
				}
			}
		}

		// Get the sort direction modifier.
		int sortModifier = sortDescending ? 1 : -1;

		// Sort by the requested sort type.
		if (sortType == DigistoreInventorySortType.COUNT) {
			// Sort by stack size.
			stacks.sort(new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					return (o2.getCount() - o1.getCount()) * sortModifier;
				}
			});
		} else {
			// Sort by stack name.
			stacks.sort(new Comparator<ItemStack>() {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					return (o1.getDisplayName().getFormattedText().compareToIgnoreCase(o2.getDisplayName().getFormattedText())) * sortModifier;
				}
			});
		}

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
