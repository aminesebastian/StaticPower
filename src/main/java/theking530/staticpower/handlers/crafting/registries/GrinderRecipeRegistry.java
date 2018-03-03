package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper.GrinderOutput;

public class GrinderRecipeRegistry {

	private static final GrinderRecipeRegistry GRINDER_BASE = new GrinderRecipeRegistry();
	
	private Map<Ingredient, GrinderOutputWrapper> grindingList = new HashMap<Ingredient, GrinderOutputWrapper>();

	public static GrinderRecipeRegistry Grinding() {
		return GRINDER_BASE;
	}	
	private GrinderRecipeRegistry() {
		
	}
	public void addRecipe(Ingredient input, GrinderOutput... outputs){
		GrinderOutputWrapper tempWrapper = null;
		GrinderOutput nullOutput = new GrinderOutput(null, 0);
		if(outputs.length == 1) {
			tempWrapper = new GrinderOutputWrapper(input, outputs[0], nullOutput, nullOutput);
		}else if(outputs.length == 2) {
			tempWrapper = new GrinderOutputWrapper(input, outputs[0], outputs[1], nullOutput);
		}else if(outputs.length == 3) {
			tempWrapper = new GrinderOutputWrapper(input, outputs[0], outputs[1], outputs[2]);
		}
		putLists(input, tempWrapper);
	}
	public void putLists(Ingredient input, GrinderOutputWrapper outputs){
		grindingList.put(input, outputs);
	}
    public Map<Ingredient, GrinderOutputWrapper> getGrindingList() {
        return this.grindingList;
    }
	public GrinderOutputWrapper getGrindingRecipe(ItemStack itemstack) {
		Iterator<Entry<Ingredient, GrinderOutputWrapper>> iterator = grindingList.entrySet().iterator();
		Entry<Ingredient, GrinderOutputWrapper> entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = iterator.next();
		} while (!canBeGrinded(itemstack, entry.getKey()));
		return (GrinderOutputWrapper) entry.getValue();
	}
	private boolean canBeGrinded(ItemStack itemstack, Ingredient ingredient) {
		return ingredient.apply(itemstack);
	}
}
