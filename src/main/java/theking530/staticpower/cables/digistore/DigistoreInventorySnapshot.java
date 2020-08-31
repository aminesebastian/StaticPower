package theking530.staticpower.cables.digistore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.api.digistore.IDigistoreInventory;
import theking530.staticpower.cables.attachments.digistore.digistoreterminal.DigistoreInventorySortType;
import theking530.staticpower.utilities.ItemUtilities;

public class DigistoreInventorySnapshot implements IItemHandler {
	public static final DigistoreInventorySnapshot EMPTY = new DigistoreInventorySnapshot();
	private final List<ItemStack> stacks;
	private final DigistoreNetworkModule module;
	private final String filterString;
	private final DigistoreInventorySortType sortType;
	private final boolean sortDescending;
	private final boolean isEmpty;

	public DigistoreInventorySnapshot(DigistoreNetworkModule module, String filter, DigistoreInventorySortType sortType, boolean sortDescending) {
		this.isEmpty = false;
		this.module = module;
		this.sortType = sortType;
		this.sortDescending = sortDescending;
		stacks = new ArrayList<ItemStack>();
		filterString = filter.toLowerCase();

		// Perform an initial update when first created.
		update();
	}

	private DigistoreInventorySnapshot() {
		this.isEmpty = true;
		this.module = null;
		this.sortType = DigistoreInventorySortType.COUNT;
		this.sortDescending = true;
		stacks = new ArrayList<ItemStack>();
		filterString = "";

	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void update() {
		// Start profiling.
		Minecraft.getInstance().getProfiler().startSection("DigistoreInventoryBuilding");

		// First clear the stack array.
		stacks.clear();

		// Populate the stacks.
		for (IDigistoreInventory digistore : module.getAllDigistores()) {
			for (int i = 0; i < digistore.getUniqueItemCapacity(); i++) {
				// Stack in slot.
				ItemStack stackInSlot = digistore.getDigistoreStack(i).getStoredItem();

				// Skip empty slots.
				if (stackInSlot.isEmpty()) {
					continue;
				}

				// Skip items that don't match the filter.
				if (filterString.length() > 0) {
					if (filterString.startsWith("@") && filterString.length() > 1) {
						if (!stackInSlot.getItem().getRegistryName().getNamespace().toLowerCase().contains(filterString.substring(1))) {
							continue;
						}
					} else if (filterString.startsWith("$") && filterString.length() > 1) {
						// Set up a flag to indicate if it was found by tag.
						boolean found = false;

						// Loop through the tags and indicate if we find a match.
						for (ResourceLocation tag : stackInSlot.getItem().getTags()) {
							if (tag.getPath().toLowerCase().contains(filterString.substring(1))) {
								found = true;
								break;
							}
						}

						// If no match is found, skip this item.
						if (!found) {
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
					stacks.get(indexOfItem).grow(digistore.getDigistoreStack(i).getCount());
				} else {
					stacks.add(ItemHandlerHelper.copyStackWithSize(stackInSlot, digistore.getDigistoreStack(i).getCount()));
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
