package theking530.staticpower.handlers.crafting.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import theking530.staticpower.handlers.crafting.wrappers.FusionFurnaceRecipeWrapper;

public class FusionRecipeRegistry {

	private static final FusionRecipeRegistry FUSION_BASE = new FusionRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map fusionList = new HashMap();

	public static FusionRecipeRegistry Fusing() {
		return FUSION_BASE;
	}	
	public void addRecipe(ItemStack output, ItemStack... inputs){
		FusionFurnaceRecipeWrapper tempWrapper = new FusionFurnaceRecipeWrapper(output, inputs);	
		fusionList.put(tempWrapper, output);
	}
    public Map getFusionList() {
        return this.fusionList;
    }
	@SuppressWarnings("rawtypes")
	public FusionFurnaceRecipeWrapper getFusionResult(ItemStack... inputs) {
		Iterator iterator = this.fusionList.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = (Entry) iterator.next();
		} while (!canBeFused(((FusionFurnaceRecipeWrapper)entry.getKey()).getInputs(), inputs));
		return (FusionFurnaceRecipeWrapper) entry.getKey();
	}
	private boolean canBeFused(ArrayList<ItemStack> recipeInputs, ItemStack...inputs) {
		ArrayList<ItemStack> tempInputs = new ArrayList();
		tempInputs.addAll(recipeInputs);
		if(inputs.length < tempInputs.size()) {
			return false;
		}else{
			for(int i=0; i<inputs.length; i++) {
				if(inputs[i] != null) {
					for(int j=0; j<tempInputs.size(); j++) {
						if(tempInputs.get(j).isItemEqual(inputs[i])) {
							tempInputs.remove(j);
							break;
						}
					}
				}
			}
		}
		if(tempInputs.size() > 0) {
			return false;
		}
		return true;
	}
}
