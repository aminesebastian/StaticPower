package theking530.staticpower.handlers.crafting.registries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.DistilleryRecipeWrapper;

public class DistilleryRecipeRegistry {

private static final DistilleryRecipeRegistry DISTILLERY_BASE = new DistilleryRecipeRegistry();
	
	@SuppressWarnings("rawtypes")
	private Map distillery_list = new HashMap();
	
	public static DistilleryRecipeRegistry Distillery() {
		return DISTILLERY_BASE;
	}	
	private DistilleryRecipeRegistry() {
		
	}
	public void addRecipe(FluidStack fluidInput, FluidStack fluidOutput, int heatMin, int heatCost){
		DistilleryRecipeWrapper tempWrapper = new DistilleryRecipeWrapper(fluidInput, fluidOutput, heatMin, heatCost);
		distillery_list.put(fluidInput, tempWrapper);
	}
    public Map getDistilleryRecipes() {
        return this.distillery_list;
    }   
	@SuppressWarnings("rawtypes")
	public int getFluidInputAmount(FluidStack fluidInput, int currentHeat) {
		Iterator iterator = this.distillery_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput, currentHeat));
		DistilleryRecipeWrapper tempWrapper = (DistilleryRecipeWrapper)entry.getValue();
		return tempWrapper.getInputFluid().amount;
	}
	@SuppressWarnings("rawtypes")
	public FluidStack getFluidOutput(FluidStack fluidInput, int currentHeat) {
		Iterator iterator = this.distillery_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput, currentHeat));
		DistilleryRecipeWrapper tempWrapper = (DistilleryRecipeWrapper)entry.getValue();
		return tempWrapper.getOutputFluid();
	}
	@SuppressWarnings("rawtypes")
	public int getHeatCost(FluidStack fluidInput, int currentHeat) {
		Iterator iterator = this.distillery_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput, currentHeat));
		DistilleryRecipeWrapper tempWrapper = (DistilleryRecipeWrapper)entry.getValue();
		return tempWrapper.getHeatCost();
	}
	@SuppressWarnings("rawtypes")
	public int getHeatMin(FluidStack fluidInput, int currentHeat) {
		Iterator iterator = this.distillery_list.entrySet().iterator();
		Entry entry;
		do {
			if (!iterator.hasNext()) {
				return 0;
			}
			entry = (Entry) iterator.next();
		} while (!isValidCombination(entry, fluidInput, currentHeat));
		DistilleryRecipeWrapper tempWrapper = (DistilleryRecipeWrapper)entry.getValue();
		return tempWrapper.getMinHeat();
	}
	private boolean isValidCombination(Entry entry, FluidStack fluidInput, int currentHeat) {
		DistilleryRecipeWrapper tempWrapper = (DistilleryRecipeWrapper)entry.getValue();
		
		if(tempWrapper.getInputFluid() != null && fluidInput != null && tempWrapper.getInputFluid().isFluidEqual(fluidInput)) {
			if(currentHeat >= tempWrapper.getMinHeat() && currentHeat >= tempWrapper.getHeatCost()) {
				return true;
			}
		}
		return false;
	}
	
}
