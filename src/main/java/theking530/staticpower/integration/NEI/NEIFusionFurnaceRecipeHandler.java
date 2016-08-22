package theking530.staticpower.integration.NEI;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import codechicken.nei.api.stack.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.util.NEIServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import theking530.staticpower.client.gui.widgets.GuiPowerBar;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FusionFurnaceRecipeWrapper;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.integration.NEI.NEIPoweredGrinderRecipeHandler.GrindingPair;
import theking530.staticpower.machines.fusionfurnace.GuiFusionFurnace;
import theking530.staticpower.utils.GuiTextures;

public class NEIFusionFurnaceRecipeHandler extends TemplateRecipeHandler {
	
    GuiPowerBar powerBar = new GuiPowerBar();
    Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public String getRecipeName() {
		return "Fusion Furnace";
	}
	@Override
	public String getGuiTexture() {
		return GuiTextures.FUISION_FURNACE_GUI.toString();
	}

	public class FusionRecipe extends CachedRecipe {
		
        PositionedStack ingred1;
        PositionedStack ingred2;
		PositionedStack ingred3;
		PositionedStack ingred4;
		PositionedStack ingred5;
		
		PositionedStack result;
		
		FusionFurnaceRecipeWrapper wrapper;
		
        public FusionRecipe(FusionFurnaceRecipeWrapper inputs, ItemStack output) {
        	output.stackSize = 1;
        	wrapper = inputs;
            this.result = new PositionedStack(output, 75, 48);
            
            ingred1 = new PositionedStack(inputs.getInputs().get(0), 39, 33);

            if(inputs.getInputItemCount() > 1) {
                if(inputs.getInputs().get(1) != null) {
                    this.ingred2 = new PositionedStack(inputs.getInputs().get(1), 54, 13);	
                }
            }
            if(inputs.getInputItemCount() > 2) {
                if(inputs.getInputs().get(2) != null) {
                    this.ingred3 = new PositionedStack(inputs.getInputs().get(2), 75, 6);	
                }
            }
            if(inputs.getInputItemCount() > 3) {
                if(inputs.getInputs().get(3) != null) {
                    this.ingred4 = new PositionedStack(inputs.getInputs().get(3), 96, 13);	
                }
            }
            if(inputs.getInputItemCount() > 4) {
                if(inputs.getInputs().get(4) != null) {
                    this.ingred5 = new PositionedStack(inputs.getInputs().get(4), 111, 33);	
                }
            }
        }

        public List<PositionedStack> getIngredients() {
            if(wrapper.getInputItemCount() > 1) {
                return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred1, ingred2));
            }
            if(wrapper.getInputItemCount() > 2) {
                return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred1, ingred2, ingred3));
            }
            if(wrapper.getInputItemCount() > 3) {
                return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred1, ingred2, ingred3, ingred4));
            }
            if(wrapper.getInputItemCount() > 4) {
                return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred1, ingred2, ingred3, ingred4, ingred5));
            }
            return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred1));
        }
        @Override
        public List<PositionedStack> getOtherStacks() {
        	List<PositionedStack> tempList = new ArrayList();
        	if(ingred1 != null) {
            	tempList.add(ingred1);        		
        	}
        	if(ingred2 != null) {
            	tempList.add(ingred2);        		
        	}
        	if(ingred3 != null) {
            	tempList.add(ingred3);        		
        	}
        	if(ingred4 != null) {
            	tempList.add(ingred4);        		
        	}
        	if(ingred5 != null) {
            	tempList.add(ingred5);        		
        	}
          return tempList;
        }
        public PositionedStack getResult() {
            return result;
        }
	}
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "fusing"));
    }
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiFusionFurnace.class;
    }
    @Override
    public TemplateRecipeHandler newInstance() {
        return super.newInstance();
    }
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("fusing") && getClass() == NEIFusionFurnaceRecipeHandler.class) {//don't want subclasses getting a hold of this
        	Map<FusionFurnaceRecipeWrapper, ItemStack> recipes = (Map<FusionFurnaceRecipeWrapper, ItemStack>) FusionRecipeRegistry.Fusing().getFusionList();
            for (Entry<FusionFurnaceRecipeWrapper, ItemStack> recipe : recipes.entrySet())
                arecipes.add(new FusionRecipe(recipe.getKey(), recipe.getValue()));
        } else
            super.loadCraftingRecipes(outputId, results);
    }
    @Override
    public void loadCraftingRecipes(ItemStack result) {
    	Map<FusionFurnaceRecipeWrapper, ItemStack> recipes = (Map<FusionFurnaceRecipeWrapper, ItemStack>) FusionRecipeRegistry.Fusing().getFusionList();
        for (Entry<FusionFurnaceRecipeWrapper, ItemStack> recipe : recipes.entrySet()) {
    		if (NEIServerUtils.areStacksSameType(recipe.getValue(), result)) {
                arecipes.add(new FusionRecipe(recipe.getKey(), recipe.getValue()));	
        	}
        }
    }
    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("fusing") && getClass() == NEIFusionFurnaceRecipeHandler.class)//don't want subclasses getting a hold of this
            loadCraftingRecipes("Fusion Furnace");
        else
            super.loadUsageRecipes(inputId, ingredients);
    }
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	Map<FusionFurnaceRecipeWrapper, ItemStack> recipes = (Map<FusionFurnaceRecipeWrapper, ItemStack>) FusionRecipeRegistry.Fusing().getFusionList();
        for (Entry<FusionFurnaceRecipeWrapper, ItemStack> recipe : recipes.entrySet()) {
        	for(int i=0; i<((FusionFurnaceRecipeWrapper)recipe.getKey()).getInputItemCount(); i++) {
                if (NEIServerUtils.areStacksSameTypeCrafting(((FusionFurnaceRecipeWrapper)recipe.getKey()).getInputs().get(i), ingredient)) {
                	FusionRecipe arecipe = new FusionRecipe(recipe.getKey(), recipe.getValue());
                    arecipe.setIngredientPermutation(Arrays.asList(arecipe.ingred1, arecipe.ingred2, arecipe.ingred3, arecipe.ingred4, arecipe.ingred5), ingredient);
                    arecipes.add(arecipe);
                }
        	}
        }
    }
    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(71, 24, 176, 68, 24, 16, 30, 1);       
        
        powerBar.drawPowerBar(3, 57, 16, 50, 1, 200-(cycleticks%50+100), 100);
   
        GL11.glScalef(0.8f, 0.8f, 0.8f);
        String powerUse = "2000 RF";
        mc.fontRendererObj.drawString(powerUse, 30, 5, 255 << 24 | 15 << 16 | 35 << 8 | 80);
        GL11.glScalef(1/.8f, 1/.8f, 1/.8f);

    }
    @Override  
    public String getOverlayIdentifier() {
        return "fusing";
    }
}
