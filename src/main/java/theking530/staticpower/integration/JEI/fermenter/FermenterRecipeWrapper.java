package theking530.staticpower.integration.JEI.fermenter;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.FermenterOutputWrapper;

public class FermenterRecipeWrapper implements IRecipeWrapper{

	 public static final Factory FACTORY = new Factory();

	 	public ItemStack inputItem;
	    public FluidStack outputFluid;
	    
	    public FermenterRecipeWrapper(FermenterOutputWrapper recipe) {
	    	inputItem = recipe.getInput();
	    	outputFluid = recipe.getOutput();
	    }

	    @Override
	    public void getIngredients(IIngredients ingredients) {
	        ingredients.setInput(ItemStack.class, inputItem);
	        ingredients.setOutput(FluidStack.class, outputFluid);
	    }

	    private static class Factory implements IRecipeWrapperFactory<FermenterOutputWrapper> {
	        @Override
	        public IRecipeWrapper getRecipeWrapper(FermenterOutputWrapper recipe) {
	            return new FermenterRecipeWrapper(recipe);
	        }
	    }
}
