package theking530.staticpower.integration.JEI.fluidinfuser;

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
import theking530.staticpower.assists.Reference;
import theking530.staticpower.blocks.ModBlocks;
import theking530.staticpower.client.gui.widgets.valuebars.GuiFluidBar;
import theking530.staticpower.client.gui.widgets.valuebars.GuiPowerBar;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FluidInfuserOutputWrapper;
import theking530.staticpower.integration.JEI.BaseJEIRecipeCategory;
import theking530.staticpower.integration.JEI.StaticPowerJEIPlugin;
import theking530.staticpower.tileentity.solderingtable.GuiSolderingTable;
import theking530.staticpower.utils.GuiTextures;

public class FluidInfuserRecipeCategory extends BaseJEIRecipeCategory<FluidInfuserRecipeWrapper>{
	 private final String locTitle;
	    private IDrawable background;

	    public FluidInfuserRecipeCategory(IGuiHelper guiHelper) {
	        locTitle = "Fluid Infuser";
	        background = guiHelper.createDrawable(GuiTextures.FLUID_INFUSER_GUI, 24, 6, 144, 65);
	    }
	    public void initialize(@Nonnull IModRegistry registry) {
	        registry.handleRecipes(FluidInfuserOutputWrapper.class, FluidInfuserRecipeWrapper.FACTORY, StaticPowerJEIPlugin.FLUID_INFUSER_UID);
	        registry.addRecipes(InfuserRecipeRegistry.Infusing().getInfusingRecipes().values(), StaticPowerJEIPlugin.FLUID_INFUSER_UID);
	        registry.addRecipeClickArea(GuiSolderingTable.class, 111, 69, 26, 19, StaticPowerJEIPlugin.FLUID_INFUSER_UID);
	    	registry.addRecipeCatalyst(new ItemStack(Item.getItemFromBlock(ModBlocks.FluidInfuser)), StaticPowerJEIPlugin.FLUID_INFUSER_UID);
	        
	    }
	    @Override
	    @Nonnull
	    public String getUid() {
	        return StaticPowerJEIPlugin.FLUID_INFUSER_UID;
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
	    	GuiPowerBar.drawPowerBar(26, 62, 6, 60, 1.0f, 5000, 10000, minecraft.getRenderPartialTicks());
	    }

	    @Override
	    public void setRecipe(IRecipeLayout recipeLayout, FluidInfuserRecipeWrapper recipeWrapper, IIngredients ingredients) {
	        IGuiItemStackGroup guiStacks = recipeLayout.getItemStacks();
	        IGuiFluidStackGroup guiFluidStacks = recipeLayout.getFluidStacks();

	        int slotId = 0;

	        //Input
	        guiStacks.init(slotId++, true, 49, 25);        
	        guiFluidStacks.init(0, true, 6, 2, 16, 60, (int)(recipeWrapper.inputFluid.amount*1.5f), true, null);
	        
	        //Output
	        guiStacks.init(slotId, false, 106, 25);
	        
	        guiStacks.set(ingredients);
	        guiFluidStacks.set(ingredients);
	    }
}
