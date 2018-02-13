package theking530.staticpower.integration.JEI.former;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.item.ItemStack;
import theking530.staticpower.handlers.crafting.wrappers.FormerRecipeWrapper;

public class JEIFormerRecipeWrapper implements IRecipeWrapper{

	 public static final Factory FACTORY = new Factory();

	    private ItemStack output;
    	List<ItemStack> inputs;
    	
	    public JEIFormerRecipeWrapper(FormerRecipeWrapper recipe) {
	        inputs = new ArrayList<ItemStack>();
	        
	        inputs.add(recipe.getInputItemStack());
	        inputs.add(new ItemStack(recipe.getRequiredMold()));
	        
	        output = recipe.getOutputItemStack();
	    }

	    @Override
	    public void getIngredients(IIngredients ingredients) {
	        ingredients.setInputs(ItemStack.class, inputs);
	        ingredients.setOutput(ItemStack.class, output);
	    }	    
	    private static class Factory implements IRecipeWrapperFactory<FormerRecipeWrapper> {
	        @Override
	        public IRecipeWrapper getRecipeWrapper(FormerRecipeWrapper recipe) {
	            return new JEIFormerRecipeWrapper(recipe);
	        }
	    }
}
