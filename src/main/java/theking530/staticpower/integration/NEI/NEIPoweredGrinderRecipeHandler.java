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
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.machines.poweredgrinder.GuiPoweredGrinder;
import theking530.staticpower.utils.GuiTextures;

public class NEIPoweredGrinderRecipeHandler extends TemplateRecipeHandler {
	
    GuiPowerBar powerBar = new GuiPowerBar();
    Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public String getRecipeName() {
		return "Powered Grinder";
	}
	@Override
	public String getGuiTexture() {
		return GuiTextures.GRINDER_GUI.toString();
	}

	public class GrindingPair extends CachedRecipe {
		
        PositionedStack ingred;
        PositionedStack result;
		PositionedStack result2;
		PositionedStack result3;
		GrinderOutputWrapper wrapper;
		
        public GrindingPair(ItemStack input, GrinderOutputWrapper output) {
        	input.stackSize = 1;
        	wrapper = output;
            ingred = new PositionedStack(input, 75, 7);
            result = new PositionedStack(output.getOutputItems().get(0).getOutput(), 75, 49);
            if(output.getOutputItemCount() > 1) {
                if(output.getOutputItems().get(1).isValid()) {
                    result2 = new PositionedStack(output.getOutputItems().get(1).getOutput(), 50, 35);	
                }
            }
            if(output.getOutputItemCount() > 2) {
                if(output.getOutputItems().get(2).isValid()) {
                    result2 = new PositionedStack(output.getOutputItems().get(2).getOutput(), 101, 35);	
                }
            }
        }

        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred));
        }
        @Override
        public List<PositionedStack> getOtherStacks() {
        	List<PositionedStack> tempList = new ArrayList();
        	if(result2 != null) {
            	tempList.add(result2);        		
        	}
        	if(result3 != null) {
            	tempList.add(result3);        		
        	}
          return tempList;
        }
        public PositionedStack getResult() {
            return result;
        }
	}
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "grinding"));
    }
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiPoweredGrinder.class;
    }
    @Override
    public TemplateRecipeHandler newInstance() {
        return super.newInstance();
    }
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("grinding") && getClass() == NEIPoweredGrinderRecipeHandler.class) {//don't want subclasses getting a hold of this
        	Map<ItemStack, GrinderOutputWrapper> recipes = (Map<ItemStack, GrinderOutputWrapper>) GrinderRecipeRegistry.Grinding().getGrindingList();
            for (Entry<ItemStack, GrinderOutputWrapper> recipe : recipes.entrySet())
                arecipes.add(new GrindingPair(recipe.getKey(), recipe.getValue()));
        } else
            super.loadCraftingRecipes(outputId, results);
    }
    @Override
    public void loadCraftingRecipes(ItemStack result) {
    	Map<ItemStack, GrinderOutputWrapper> recipes = (Map<ItemStack, GrinderOutputWrapper>) GrinderRecipeRegistry.Grinding().getGrindingList();
        for (Entry<ItemStack, GrinderOutputWrapper> recipe : recipes.entrySet()) {
        	for(int i=0; i<recipe.getValue().getOutputItemCount(); i++) {
        		if (NEIServerUtils.areStacksSameType(recipe.getValue().getOutputItems().get(i).getOutput(), result)) {
                    arecipes.add(new GrindingPair(recipe.getKey(), recipe.getValue()));	
        		}
        	}
        }
    }
    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("grinding") && getClass() == NEIPoweredGrinderRecipeHandler.class)//don't want subclasses getting a hold of this
            loadCraftingRecipes("Powered Grinder");
        else
            super.loadUsageRecipes(inputId, ingredients);
    }
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	Map<ItemStack, GrinderOutputWrapper> recipes = (Map<ItemStack, GrinderOutputWrapper>) GrinderRecipeRegistry.Grinding().getGrindingList();
        for (Entry<ItemStack, GrinderOutputWrapper> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getKey(), ingredient)) {
            	GrindingPair arecipe = new GrindingPair(recipe.getKey(), recipe.getValue());
                arecipe.setIngredientPermutation(Arrays.asList(arecipe.ingred), ingredient);
                arecipes.add(arecipe);
            }
    }
    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(71, 26, 176, 68, 24, 16, 30, 1);       
        
        GrindingPair recipePair = (GrindingPair)(arecipes.get(recipe));    

        powerBar.drawPowerBar(3, 57, 16, 50, 1, 200-(cycleticks%50+100), 100);
   
        GL11.glScalef(0.8f, 0.8f, 0.8f);
        String powerUse = "2000 RF";
        mc.fontRendererObj.drawString(powerUse, 30, 5, 255 << 24 | 15 << 16 | 35 << 8 | 80);
        GL11.glScalef(1/.8f, 1/.8f, 1/.8f);
        
        GL11.glScalef(0.65f, 0.65f, 0.65f);
        String percent1 = new java.text.DecimalFormat("#").format(recipePair.wrapper.getOutputItems().get(0).getPercentage()*100) + "%";
        mc.fontRendererObj.drawString(percent1, 117, 65, 255 << 24 | 15 << 16 | 35 << 8 | 35);
        
        if(recipePair.wrapper.getOutputItemCount()>1) {
            String percent2 = new java.text.DecimalFormat("#").format(recipePair.wrapper.getOutputItems().get(1).getPercentage()*100)  + "%";
            mc.fontRendererObj.drawString(percent2, 80, 43, 255 << 24 | 15 << 16 | 35 << 8 | 35); 	
        }
        if(recipePair.wrapper.getOutputItemCount()>2) {
            String percent3 = new java.text.DecimalFormat("#").format(recipePair.wrapper.getOutputItems().get(2).getPercentage()*100)  + "%";
            mc.fontRendererObj.drawString(percent3, 164, 43, 255 << 24 | 15 << 16 | 35 << 8 | 35);
        }   
        GL11.glScalef(1/.65f, 1/.65f, 1/.65f);
    }
    @Override  
    public String getOverlayIdentifier() {
        return "grinding";
    }
}
