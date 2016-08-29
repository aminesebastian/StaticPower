package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.FermenterOutputWrapper;

public class FermenterRecipeRegistry {

	private static final FermenterRecipeRegistry FERENTER_BASE = new FermenterRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map fermenting_list = new HashMap();
	
	public static FermenterRecipeRegistry Fermenting() {
		return FERENTER_BASE;
	}	
	private FermenterRecipeRegistry() {
		
	}
	public void addRecipe(ItemStack input, FluidStack output){
		FermenterOutputWrapper tempWrapper = new FermenterOutputWrapper(input, output);
		fermenting_list.put(input, tempWrapper);
	}
    public Map getFermentingRecipes() {
        return this.fermenting_list;
    }
    /** Given input item stack and the fluidstack in the infuser */
	@SuppressWarnings("rawtypes")
	public FluidStack getFluidResult(ItemStack inputItemstack) {
		Iterator iterator = this.fermenting_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, inputItemstack));
		FermenterOutputWrapper tempWrapper = (FermenterOutputWrapper)entry.getValue();
		return tempWrapper.getOutput();
	}
	private boolean isValidCombination(Entry entry, ItemStack inputItemstack) {
		ItemStack recipeInputStack = (ItemStack)entry.getKey();
		FermenterOutputWrapper tempWrapper = (FermenterOutputWrapper)entry.getValue();
		
		if(recipeInputStack != null && inputItemstack != null) {
			return inputItemstack.isItemEqual(recipeInputStack);
		}
		return false;
	}
}
