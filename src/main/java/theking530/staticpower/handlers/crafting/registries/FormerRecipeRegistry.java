package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import theking530.staticpower.handlers.crafting.wrappers.FormerRecipeWrapper;

public class FormerRecipeRegistry {
	
	private static final FormerRecipeRegistry FORMER_BASE = new FormerRecipeRegistry();
	
	private Map<ItemStack, FormerRecipeWrapper> formingList = new HashMap<ItemStack, FormerRecipeWrapper>();

	public static FormerRecipeRegistry Forming() {
		return FORMER_BASE;
	}	
	private FormerRecipeRegistry() {
		
	}
	public void addRecipe(ItemStack output, ItemStack input, Item mold){
		putLists(input, new FormerRecipeWrapper(output, input, mold));
	}
	public void putLists(ItemStack input, FormerRecipeWrapper outputs){
		formingList.put(input, outputs);
	}
    public Map<ItemStack, FormerRecipeWrapper> getFormingList() {
        return this.formingList;
    }
	public FormerRecipeWrapper getFormingResult(ItemStack inputItem, Item inputMold) {
		Iterator<Entry<ItemStack, FormerRecipeWrapper>> iterator = formingList.entrySet().iterator();
		Entry<ItemStack, FormerRecipeWrapper> entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = iterator.next();
		} while (!canBeFormed(inputItem, inputMold, entry.getValue()));
		return entry.getValue();
	}
	private boolean canBeFormed(ItemStack inputItem, Item inputMold, FormerRecipeWrapper recipe) {
		return recipe.satisfiesRecipe(inputItem, inputMold);
	}
}