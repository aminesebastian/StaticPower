package theking530.staticpower.integration.JEI.grinder;

import java.util.LinkedList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import theking530.staticpower.assists.utilities.GuiUtilities;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;

public class PoweredGrinderRecipeWrapper implements IRecipeWrapper{

	 public static final Factory FACTORY = new Factory();

	    private List<ItemStack> output;
	    private ItemStack input;
	    public GrinderOutputWrapper recipe;
	    
	    public PoweredGrinderRecipeWrapper(GrinderOutputWrapper recipe) {
	        this.recipe = recipe;
	        input = recipe.getInputItem();

	    	List<ItemStack> temp = new LinkedList<ItemStack>();
	    	for(int i=0; i<recipe.getOutputItems().size(); i++) {
	    		temp.add(recipe.getOutputItems().get(i).getOutput());
	    	}

	        output = temp;

	    }

	    @Override
	    public void getIngredients(IIngredients ingredients) {
	        ingredients.setInput(ItemStack.class, input);
	        ingredients.setOutputs(ItemStack.class, output);
	    }
	    @Override
		public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
	    	for(int i=0; i<recipe.getOutputItems().size(); i++) {
				if (recipe.getOutputItems().get(i).getPercentage() > 0) {
					String dispChance = GuiUtilities.formatIntegerWithCommas((int) (recipe.getOutputItems().get(i).getPercentage()*100.0f)) + "%";
					int xPos = 0;
					int yPos = 0;
					
					if(i==0) {
						xPos = 74; //+12
						yPos = 73; //+22
					}else if(i==1) {
						xPos = 93;
						yPos = 59;
					}else{
						xPos = 40;
						yPos = 59;
					}		
					minecraft.fontRenderer.drawString(dispChance, xPos - 7 * dispChance.length(), yPos, GuiUtilities.getColor(150, 150, 150));
				}
	    	}
		}
	    
	    private static class Factory implements IRecipeWrapperFactory<GrinderOutputWrapper> {
	        @Override
	        public IRecipeWrapper getRecipeWrapper(GrinderOutputWrapper recipe) {
	            return new PoweredGrinderRecipeWrapper(recipe);
	        }
	    }
}
