package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.FluidInfuserOutputWrapper;

public class InfuserRecipeRegistry {

	private static final InfuserRecipeRegistry INFUSER_BASE = new InfuserRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map infusionList = new HashMap();
	
	public static InfuserRecipeRegistry Infusing() {
		return INFUSER_BASE;
	}	
	private InfuserRecipeRegistry() {
		
	}
	public void addRecipe(ItemStack input, ItemStack output, FluidStack requiredFluidStack){
		FluidInfuserOutputWrapper tempWrapper = new FluidInfuserOutputWrapper(output, requiredFluidStack);
		infusionList.put(input, tempWrapper);
	}
    public Map getInfusingRecipes() {
        return this.infusionList;
    }
    /** Given input item stack and the fluidstack in the infuser */
	@SuppressWarnings("rawtypes")
	public ItemStack getInfusingItemStackResult(ItemStack inputItemstack, FluidStack infuserfluidstack) {
		Iterator iterator = this.infusionList.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, inputItemstack, infuserfluidstack));
		FluidInfuserOutputWrapper tempWrapper = (FluidInfuserOutputWrapper)entry.getValue();
		return tempWrapper.getOutputItem();
	}
	@SuppressWarnings("rawtypes")
	public int getInfusingFluidCost(ItemStack inputItemstack, FluidStack infuserfluidstack) {
		Iterator iterator = this.infusionList.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, inputItemstack, infuserfluidstack));
		FluidInfuserOutputWrapper tempWrapper = (FluidInfuserOutputWrapper)entry.getValue();
		FluidStack recipeFluidStack = tempWrapper.getRequiredFluid();
		return recipeFluidStack.amount;
	}
	private boolean isValidCombination(Entry entry, ItemStack inputItemstack, FluidStack infuserfluidstack) {
		ItemStack recipeInputStack = (ItemStack)entry.getKey();
		FluidInfuserOutputWrapper tempWrapper = (FluidInfuserOutputWrapper)entry.getValue();
		FluidStack recipeFluidStack = tempWrapper.getRequiredFluid();
		
		if(recipeInputStack != null && inputItemstack != null && infuserfluidstack != null) {
			return inputItemstack.isItemEqual(recipeInputStack) && infuserfluidstack.isFluidEqual(recipeFluidStack);
		}
		return false;
	}
}
