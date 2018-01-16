package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.CondenserRecipeWrapper;

public class CondenserRecipeRegistry {

private static final CondenserRecipeRegistry CONDENSER_BASE = new CondenserRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map condensing_list = new HashMap();
	
	public static CondenserRecipeRegistry Condensing() {
		return CONDENSER_BASE;
	}	
	private CondenserRecipeRegistry() {
		
	}
	public void addRecipe(FluidStack fluidInput, FluidStack fluidOutput, int condensingTime){
		CondenserRecipeWrapper tempWrapper = new CondenserRecipeWrapper(fluidInput, fluidOutput, condensingTime);
		condensing_list.put(fluidInput, tempWrapper);
	}
    public Map getCondenserRecipes() {
        return this.condensing_list;
    }
	@SuppressWarnings("rawtypes")
	public int getFluidInputAmount(FluidStack fluidInput) {
		Iterator iterator = this.condensing_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput));
		CondenserRecipeWrapper tempWrapper = (CondenserRecipeWrapper)entry.getValue();
		return tempWrapper.getInputFluid().amount;
	}
	@SuppressWarnings("rawtypes")
	public FluidStack getFluidOutput(FluidStack fluidInput) {
		Iterator iterator = this.condensing_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput));
		CondenserRecipeWrapper tempWrapper = (CondenserRecipeWrapper)entry.getValue();
		return tempWrapper.getOutputFluid();
	}
	@SuppressWarnings("rawtypes")
	public int getCondensingTime(FluidStack fluidInput) {
		Iterator iterator = this.condensing_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput));
		CondenserRecipeWrapper tempWrapper = (CondenserRecipeWrapper)entry.getValue();
		return tempWrapper.getCondensingTime();
	}
	private boolean isValidCombination(Entry entry, FluidStack fluidInput) {
		CondenserRecipeWrapper tempWrapper = (CondenserRecipeWrapper)entry.getValue();
		
		if(tempWrapper.getInputFluid() != null && fluidInput != null && tempWrapper.getInputFluid().isFluidEqual(fluidInput)) {
				return true;
		}
		return false;
	}
	
}
