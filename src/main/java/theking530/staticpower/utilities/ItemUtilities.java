package theking530.staticpower.utilities;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

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
	 * Checks to see if stack2 can be used to replace stack1 based on their tags.
	 * Example, two different kinds of wood logs would return true here.
	 * 
	 * @param stack1 The master item to replace.
	 * @param stack2 The possible replacement item.
	 * @return True if stack2 is usable to replace stack1.
	 */
	public static boolean doStacksOverlapTags(ItemStack stack1, ItemStack stack2) {
		for (ResourceLocation filterItemTags : stack1.getItem().getTags()) {
			if (stack2.getItem().getTags().contains(filterItemTags)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Compares the provided {@link ItemStack}s and returns true if they are equal
	 * in all things except count. NOTE: This does not actually mean that the items
	 * can stack (ie. Passing two iron helmets in here will still return true.).
	 * This just means that they are equal in all criteria required to form a stack.
	 * 
	 * @param item1
	 * @param item2
	 * @return
	 */
	public static boolean areItemStacksStackable(ItemStack item1, ItemStack item2) {
		if (item1.isEmpty() || item2.isEmpty() || item1.getItem() != item2.getItem())
			return false;

		// Metadata value only matters when the item has subtypes
		// Vanilla stacks non-subtype items with different metadata together
		// TODO Item subtypes, is this still necessary?
		/*
		 * e.g. a stick with metadata 0 and a stick with metadata 1 stack if
		 * (a.getHasSubtypes() && a.getMetadata() != b.getMetadata()) return false;
		 */
		if (item1.hasTag() != item1.hasTag())
			return false;

		return (!item1.hasTag() || item1.getTag().equals(item2.getTag())) && item1.areCapsCompatible(item2);
	}
}
