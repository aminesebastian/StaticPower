package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.SqueezerOutputWrapper;

public class SqueezerRecipeRegistry {

	private static final SqueezerRecipeRegistry SQUEEZER_BASE = new SqueezerRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map SQUEEZE_MAP = new HashMap();
	
	public static SqueezerRecipeRegistry Squeezing() {
		return SQUEEZER_BASE;
	}	
	public void addRecipe(ItemStack input, ItemStack output, FluidStack outputFluidStack){
		SQUEEZE_MAP.put(input, new SqueezerOutputWrapper(output, outputFluidStack));
	}
	public Map getSqueezingRecipes() {
		return SQUEEZE_MAP;
	}
	public ItemStack getSqueezingItemResult(ItemStack input) {
	    Iterator iterator = SQUEEZE_MAP.entrySet().iterator();
	    Entry pair;
	    do {
	    	if(!iterator.hasNext()) {
	    		return null;
	    	}
	    	pair = (Entry) iterator.next();
	    }while(!isValidRecipe(pair, input));
	    
	    SqueezerOutputWrapper outputWrapper = (SqueezerOutputWrapper)pair.getValue();
		return outputWrapper.getOutputItem();
	}
	public FluidStack getSqueezingFluidResult(ItemStack input) {
	    Iterator iterator = SQUEEZE_MAP.entrySet().iterator();
	    Entry pair;
	    do {
	    	if(!iterator.hasNext()) {
	    		return null;
	    	}
	    	pair = (Entry) iterator.next();
	    }while(!isValidRecipe(pair, input));
	    
	    SqueezerOutputWrapper outputWrapper = (SqueezerOutputWrapper)pair.getValue();	
		return outputWrapper.getOutputFluid();
	}
	public boolean isValidRecipe(Entry entry, ItemStack input) {
		if(entry == null) {
			return false;
		}
		ItemStack recipeInput = (ItemStack)entry.getKey();
		
		if(input != null && recipeInput != null) {
			if(input.isItemEqual(recipeInput)) {
				return true;
			}
		}
		return false;
	}
}
