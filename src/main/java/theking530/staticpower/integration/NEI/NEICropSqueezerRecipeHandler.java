package theking530.staticpower.integration.NEI;

import java.awt.Rectangle;
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
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.client.gui.widgets.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.GuiPowerBar;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.SqueezerOutputWrapper;
import theking530.staticpower.machines.cropsqueezer.GuiCropSqueezer;
import theking530.staticpower.utils.GuiTextures;

public class NEICropSqueezerRecipeHandler extends TemplateRecipeHandler {
	
	GuiFluidBar fluidBar = new GuiFluidBar();
    GuiPowerBar powerBar = new GuiPowerBar();
    Minecraft mc = Minecraft.getMinecraft();
    
	@Override
	public String getRecipeName() {
		return "Crop Squeezer";
	}
	@Override
	public String getGuiTexture() {
		return GuiTextures.SQUEEZER_GUI.toString();
	}

	public class SqueezingPair extends CachedRecipe {
		public FluidStack resultingFluid;
        public SqueezingPair(ItemStack input, SqueezerOutputWrapper output) {
        	input.stackSize = 1;
            this.ingred = new PositionedStack(input, 79, 5);
            this.result = new PositionedStack(output.getOutputItem(), 79, 45);
            resultingFluid = output.getOutputFluid();
        }

        public List<PositionedStack> getIngredients() {
            return getCycledIngredients(cycleticks / 48, Arrays.asList(ingred));
        }

        public PositionedStack getResult() {
            return result;
        }

        PositionedStack ingred;
        PositionedStack result;
	}
    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(74, 23, 24, 18), "squeezing"));
    }
    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCropSqueezer.class;
    }
    @Override
    public TemplateRecipeHandler newInstance() {
        return super.newInstance();
    }
    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("smelting") && getClass() == NEICropSqueezerRecipeHandler.class) {//don't want subclasses getting a hold of this
        	Map<ItemStack, SqueezerOutputWrapper> recipes = (Map<ItemStack, SqueezerOutputWrapper>) SqueezerRecipeRegistry.Squeezing().getSqueezingRecipes();
            for (Entry<ItemStack, SqueezerOutputWrapper> recipe : recipes.entrySet())
                arecipes.add(new SqueezingPair(recipe.getKey(), recipe.getValue()));
        } else
            super.loadCraftingRecipes(outputId, results);
    }
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        Map<ItemStack, SqueezerOutputWrapper> recipes = (Map<ItemStack, SqueezerOutputWrapper>) SqueezerRecipeRegistry.Squeezing().getSqueezingRecipes();
        for (Entry<ItemStack, SqueezerOutputWrapper> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameType(recipe.getValue().getOutputItem(), result))
                arecipes.add(new SqueezingPair(recipe.getKey(), recipe.getValue()));
    }
    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("fuel") && getClass() == NEICropSqueezerRecipeHandler.class)//don't want subclasses getting a hold of this
            loadCraftingRecipes("smelting");
        else
            super.loadUsageRecipes(inputId, ingredients);
    }
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        Map<ItemStack, SqueezerOutputWrapper> recipes = (Map<ItemStack, SqueezerOutputWrapper>) SqueezerRecipeRegistry.Squeezing().getSqueezingRecipes();
        for (Entry<ItemStack, SqueezerOutputWrapper> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getKey(), ingredient)) {
            	SqueezingPair arecipe = new SqueezingPair(recipe.getKey(), recipe.getValue());
                arecipe.setIngredientPermutation(Arrays.asList(arecipe.ingred), ingredient);
                arecipes.add(arecipe);
            }
    }
    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(83, 23, 176, 72, 20, 16, 30, 1);
        float xpos = 6;
        float ypos = 57;
        float width = 16;
        float height = 56;
        float zLevel = 0;
        
        
        SqueezingPair recipePair = (SqueezingPair)(arecipes.get(recipe));    
        FluidStack fluidStack = recipePair.resultingFluid;
        if(fluidStack != null) {
            fluidBar.drawFluidBar(fluidStack, 200, (cycleticks%30+100), 6, 57, 1, 16, 50);
        }
        powerBar.drawPowerBar(26, 57, 6, 50, 1, 200-(cycleticks%50+100), 100);
        
        GL11.glScalef(0.8f, 0.8f, 0.8f);
        String powerUse = 1000 + " RF";
        mc.fontRendererObj.drawString(powerUse, 45, 0, 010000);
        GL11.glScalef(1/.8f, 1/.8f, 1/.8f);
        
        GL11.glScalef(0.65f, 0.65f, 0.65f);
        String fluidUse = fluidStack.amount + "mb " + fluidStack.getLocalizedName();
        mc.fontRendererObj.drawString(fluidUse, 0, 95, 010000);
        GL11.glScalef(1/.65f, 1/.65f, 1/.65f);
        
    }
    @Override
    public String getOverlayIdentifier() {
        return "squeezing";
    }
}
