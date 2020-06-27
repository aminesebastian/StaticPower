package theking530.staticpower.utilities;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemUtilities {
	public static boolean filterItems(IItemHandler inventoryOfFilterItems, ItemStack itemToCheck, boolean whitelist, boolean matchNBT, boolean matchOreDict, boolean matchMod) {
		List<ItemStack> invItems = new LinkedList<ItemStack>();
		for (int i = 0; i < inventoryOfFilterItems.getSlots(); i++) {
			invItems.add(inventoryOfFilterItems.getStackInSlot(i));
		}
		return filterItems(invItems, itemToCheck, whitelist, matchNBT, matchOreDict, matchMod);
	}

	public static boolean filterItems(List<ItemStack> filterItems, ItemStack itemToCheck, boolean whitelist, boolean matchNBT, boolean matchOreDict, boolean matchMod) {
		if (itemToCheck.isEmpty()) {
			return false;
		}

		boolean match = false;
		for (int i = 0; i < filterItems.size(); i++) {
			if (!filterItems.get(i).isEmpty() && !itemToCheck.isEmpty()) {
				if (filterItems.get(i).getItem() == itemToCheck.getItem()) {
					match = true;
				}
			}
		}
		if (!match && matchMod) {
			for (int i = 0; i < filterItems.size(); i++) {
				if (!filterItems.get(i).isEmpty()) {
					if (filterItems.get(i).getItem().getRegistryName().getNamespace() == itemToCheck.getItem().getRegistryName().getNamespace()) {
						match = true;
						break;
					}
				}
			}
		}

		// Check for ore dictionary (tags).
		if (!match && matchOreDict) {
			for (ItemStack filterItem : filterItems) {
				for (ResourceLocation filterItemTags : filterItem.getItem().getTags()) {
					if (itemToCheck.getItem().getTags().contains(filterItemTags)) {
						match = true;
						break;
					}
				}
			}
		}

		// Check metadata only if one of the first three matches passed.
		if (match && matchNBT) {
			match = false;
			for (int i = 0; i < filterItems.size(); i++) {
				if (!filterItems.get(i).isEmpty()) {
					if (filterItems.get(i).hasTag() && itemToCheck.hasTag() && ItemStack.areItemStackTagsEqual(filterItems.get(i), itemToCheck)) {
						match = true;
						break;
					}
				}
			}
		}
		if (match) {
			return whitelist ? true : false;
		}
		return whitelist ? false : true;
	}

	/**
	 * Compares the provided {@link ItemStack}s and returns true if they are equal
	 * in all things except count.
	 * 
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static boolean areItemStacksStackable(ItemStack item1, ItemStack item2) {
		return ItemHandlerHelper.canItemStacksStackRelaxed(item1, item2);
	}
}
