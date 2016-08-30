package theking530.staticpower.integration.NEI;

import static codechicken.lib.gui.GuiDraw.getMousePosition;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codechicken.nei.api.stack.PositionedStack;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerTooltipHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import codechicken.nei.util.NEIServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.client.gui.widgets.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.GuiPowerBar;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FluidInfuserOutputWrapper;
import theking530.staticpower.machines.cropsqueezer.GuiCropSqueezer;
import theking530.staticpower.utils.GuiTextures;

public class NEIFluidInfuserRecipeHandler extends TemplateRecipeHandler {
	
	public GuiFluidBar fluidBar = new GuiFluidBar();
	public GuiPowerBar powerBar = new GuiPowerBar();
    public Minecraft mc = Minecraft.getMinecraft();
    //public static NEIFluidInfuserTooltipHandler tooltip;
    
    static {
    	//tooltip = new NEIFluidInfuserTooltipHandler();
       // GuiContainerManager.addTooltipHandler(tooltip);
    }
    
	@Override
	public String getRecipeName() {
		return "Fluid Infuser";
	}
	@Override
	public String getGuiTexture() {
		return GuiTextures.INFUSER_GUI.toString();
	}

	public class InfusingPair extends CachedRecipe {
		public FluidStack resultingFluid;
        public InfusingPair(ItemStack input, FluidInfuserOutputWrapper output) {
        	input.stackSize = 1;
            this.ingred = new PositionedStack(input, 50, 21);
            this.result = new PositionedStack(output.getOutputItem(), 107, 21);
            resultingFluid = output.getRequiredFluid();
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
        if (outputId.equals("smelting") && getClass() == NEIFluidInfuserRecipeHandler.class) {//don't want subclasses getting a hold of this
        	Map<ItemStack, FluidInfuserOutputWrapper> recipes = (Map<ItemStack, FluidInfuserOutputWrapper>) InfuserRecipeRegistry.Infusing().getInfusingRecipes();
            for (Entry<ItemStack, FluidInfuserOutputWrapper> recipe : recipes.entrySet())
                arecipes.add(new InfusingPair(recipe.getKey(), recipe.getValue()));
        } else
            super.loadCraftingRecipes(outputId, results);
    }
    @Override
    public void loadCraftingRecipes(ItemStack result) {
    	Map<ItemStack, FluidInfuserOutputWrapper> recipes = (Map<ItemStack, FluidInfuserOutputWrapper>) InfuserRecipeRegistry.Infusing().getInfusingRecipes();
        for (Entry<ItemStack, FluidInfuserOutputWrapper> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameType(recipe.getValue().getOutputItem(), result))
                arecipes.add(new InfusingPair(recipe.getKey(), recipe.getValue()));
    }
    @Override
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("fuel") && getClass() == NEIFluidInfuserRecipeHandler.class)//don't want subclasses getting a hold of this
            loadCraftingRecipes("smelting");
        else
            super.loadUsageRecipes(inputId, ingredients);
    }
    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
    	Map<ItemStack, FluidInfuserOutputWrapper> recipes = (Map<ItemStack, FluidInfuserOutputWrapper>) InfuserRecipeRegistry.Infusing().getInfusingRecipes();
        for (Entry<ItemStack, FluidInfuserOutputWrapper> recipe : recipes.entrySet())
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getKey(), ingredient)) {
            	InfusingPair arecipe = new InfusingPair(recipe.getKey(), recipe.getValue());
                arecipe.setIngredientPermutation(Arrays.asList(arecipe.ingred), ingredient);
                arecipes.add(arecipe);
            }
    }
    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(74, 20, 176, 68, 24, 16, 30, 0);       
        
        InfusingPair recipePair = (InfusingPair)(arecipes.get(recipe));    
        FluidStack fluidStack = recipePair.resultingFluid;
        if(fluidStack != null) {
            fluidBar.drawFluidBar(fluidStack, 100, 200-(cycleticks%30+100), 6, 57, 1, 16, 50);
        }
        powerBar.drawPowerBar(26, 57, 6, 50, 1, 200-(cycleticks%50+100), 100);
        
        String powerUse = fluidStack.amount*5 + " RF";
        mc.fontRendererObj.drawString(powerUse, 35, 45, 010000);

        String fluidUse = fluidStack.amount + " mb " + fluidStack.getLocalizedName();
        mc.fontRendererObj.drawString(fluidUse, 35, 5, 010000);
        
       // tooltip.FLUID_TIP.clear();
       // tooltip.FLUID_TIP.add(fluidStack.getLocalizedName());
       // tooltip.FLUID_TIP.add(fluidStack.amount + "mb");
        
        Point pos = getMousePosition();
        Point relMouse = new Point(pos.x - 0 - 0, pos.y - 0 - 0);    
    }
    @Override
    public String getOverlayIdentifier() {
        return "infusing";
    }
}
class NEIFluidInfuserTooltipHandler implements IContainerTooltipHandler {
	public List<String> FLUID_TIP = new ArrayList();
	public List<String> POWER_TIP;
	
	@Override
	public List<String> handleTooltip(GuiContainer gui, int mousex, int mousey, List<String> currenttip) {
		currenttip.add("HI");
		if(FLUID_TIP != null) {
			currenttip.addAll(FLUID_TIP);	
		}
		return currenttip;
	}

	@Override
	public List<String> handleItemDisplayName(GuiContainer gui, ItemStack itemstack, List<String> currenttip) {
		return currenttip;
	}

	@Override
	public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey,
			List<String> currenttip) {
		return currenttip;
	}
	
}
