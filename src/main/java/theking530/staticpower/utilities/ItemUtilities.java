package theking530.staticpower.utilities;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemUtilities {
	public static boolean filterItems(List<ItemStack> filterItems, ItemStack itemToCheck, boolean whitelist, boolean matchMetadata, boolean matchNBT, boolean matchOreDict, boolean matchMod) {
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

		// Check metadata only if one of the previous three matches passed.
		if (match && matchMetadata) {
			match = false;
			for (int i = 0; i < filterItems.size(); i++) {
				if (!filterItems.get(i).isEmpty()) {
					if (ItemStack.areItemsEqual(filterItems.get(i), itemToCheck)) {
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
		if (item1.getItem() != item2.getItem()) {
			return false;
		} else if (item1.getTag() == null && item2.getTag() != null) {
			return false;
		} else {
			return item1.getTag() == null || item1.getTag().equals(item2.getTag()) && item1.areCapsCompatible(item2);
		}
	}
}
