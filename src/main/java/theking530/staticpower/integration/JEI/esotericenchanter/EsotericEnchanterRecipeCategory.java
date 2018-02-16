package theking530.staticpower.integration.JEI.esotericenchanter;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.GuiTextures;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBar;
import theking530.staticpower.handlers.crafting.registries.EsotericEnchanterRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.EsotericEnchanterRecipeWrapper;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.PluginJEI;

public class EsotericEnchanterRecipeCategory extends BaseJEIRecipeCategory<JEIEsotericEnchanterRecipeWrapper>{
	 private final String locTitle;
	    private IDrawable background;
	    private int currentPower;
	    
	    public EsotericEnchanterRecipeCategory(IGuiHelper guiHelper) {
	        locTitle = "Esoteric Enchanter";
	        background = guiHelper.createDrawable(GuiTextures.FLUID_INFUSER_GUI, 24, 6, 144, 65);
	    }
	    public void initialize(@Nonnull IModRegistry registry) {
	    	registry.handleRecipes(EsotericEnchanterRecipeWrapper.class, recipe -> new JEIEsotericEnchanterRecipeWrapper(recipe), PluginJEI.ESOTERIC_ENCHANTER_UID); 
	        registry.addRecipes(EsotericEnchanterRecipeRegistry.Enchanting().getEnchantingRecipes(), PluginJEI.ESOTERIC_ENCHANTER_UID);
	    	registry.addRecipeCatalyst(new ItemStack(Item.getItemFromBlock(ModBlocks.EsotericEnchanter)), PluginJEI.ESOTERIC_ENCHANTER_UID);   
	        
	    	currentPower = 10000;
	    }
	    @Override
	    @Nonnull
	    public String getUid() {
	        return PluginJEI.ESOTERIC_ENCHANTER_UID;
	    }

	    @Override
	    @Nonnull
	    public String getTitle() {
	        return locTitle;
	    }

	    @Override
	    public String getModName() {
	        return Reference.MOD_NAME;
	    }

	    @Override
	    @Nonnull
	    public IDrawable getBackground() {
	        return background;
	    }
	    @Override
	    public void drawExtras(@Nonnull Minecraft minecraft) {
	    	GuiPowerBar.drawPowerBar(26, 62, 6, 60, 1.0f, currentPower, 10000);

	    	currentPower -= 2;
	    	if(currentPower <= 0) {
	    		currentPower = 10000;
	    	}
	    }

	    @Override
	    public void setRecipe(IRecipeLayout recipeLayout, JEIEsotericEnchanterRecipeWrapper recipeWrapper, IIngredients ingredients) {
	        IGuiItemStackGroup guiStacks = recipeLayout.getItemStacks();
	        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

	        int slotId = 0;

	        //Input
	        guiStacks.init(slotId++, true, 49, 25);      
	        guiStacks.init(slotId++, true, 49, 25);  
	        guiFluidStacks.init(0, true, 6, 2, 16, 60, ingredients.getInputs(FluidStack.class).get(0).get(0).amount*2, false, null);
	        
	        //Output
	        guiStacks.init(slotId, false, 106, 25);
	        
	        guiStacks.set(ingredients);
	        guiFluidStacks.set(ingredients);
	    }
}
