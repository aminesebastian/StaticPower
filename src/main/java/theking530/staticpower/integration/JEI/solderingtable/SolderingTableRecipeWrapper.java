package theking530.staticpower.integration.JEI.solderingtable;

import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.item.ItemStack;
import theking530.staticpower.handlers.crafting.wrappers.SolderingRecipeWrapper;

public class SolderingTableRecipeWrapper implements IRecipeWrapper{

	 public static final Factory FACTORY = new Factory();

	    private List<ItemStack> input;
	    private ItemStack output;

	    public SolderingTableRecipeWrapper(SolderingRecipeWrapper recipe)
	    {
	    	List<ItemStack> temp = new LinkedList<ItemStack>();
	    	for(int i=0; i<recipe.recipeItems.length; i++) {
	    		temp.add(recipe.recipeItems[i]);
	    	}
	        input = temp;
	        output = recipe.getRecipeOutput();
	    }

	    @Override
	    public void getIngredients(IIngredients ingredients)
	    {
	        ingredients.setInputs(ItemStack.class, input);
	        ingredients.setOutput(ItemStack.class, output);
	    }

	    private static class Factory implements IRecipeWrapperFactory<SolderingRecipeWrapper>
	    {
	        @Override
	        public IRecipeWrapper getRecipeWrapper(SolderingRecipeWrapper recipe)
	        {
	            return new SolderingTableRecipeWrapper(recipe);
	        }
	    }
}
