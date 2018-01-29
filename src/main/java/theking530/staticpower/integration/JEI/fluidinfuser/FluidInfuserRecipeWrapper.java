package theking530.staticpower.integration.JEI.fluidinfuser;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.wrappers.FluidInfuserOutputWrapper;

public class FluidInfuserRecipeWrapper implements IRecipeWrapper{

	 public static final Factory FACTORY = new Factory();

	 	public ItemStack input;
	    public ItemStack output;
	    public FluidStack inputFluid;
	    
	    public FluidInfuserRecipeWrapper(FluidInfuserOutputWrapper recipe) {
	        input = recipe.getInputItemStack();
	        inputFluid = recipe.getRequiredFluidStack();
	        output = recipe.getOutputItemStack();
	    }

	    @Override
	    public void getIngredients(IIngredients ingredients) {
	        ingredients.setInput(ItemStack.class, input);
	        ingredients.setInput(FluidStack.class, inputFluid);
	        ingredients.setOutput(ItemStack.class, output);
	    }

	    private static class Factory implements IRecipeWrapperFactory<FluidInfuserOutputWrapper> {
	        @Override
	        public IRecipeWrapper getRecipeWrapper(FluidInfuserOutputWrapper recipe) {
	            return new FluidInfuserRecipeWrapper(recipe);
	        }
	    }
}
