package theking530.staticpower.integration.JEI.solderingtable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.integration.JEI.StaticPowerJEIPlugin;
import theking530.staticpower.utils.GuiTextures;

public class SolderingTableRecipeCategory implements IRecipeCategory<SolderingTableRecipeWrapper>{
	 private final String locTitle;
	    private IDrawable background;

	    public SolderingTableRecipeCategory(IGuiHelper guiHelper) {
	        locTitle = "Soldering Table";
	        //background = guiHelper.createBlankDrawable(150, 110);
	        background = guiHelper.createDrawable(GuiTextures.SOLDERING_TABLE_GUI, 0, 0, 176, 69);
	    }

	    @Override
	    @Nonnull
	    public String getUid() {
	        return StaticPowerJEIPlugin.SOLDERING_TABLE_UID;
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

	    @Nullable
	    @Override
	    public IDrawable getIcon() {
	        return null;
	    }

	    @Override
	    public void drawExtras(@Nonnull Minecraft minecraft) {}

	    @Override
	    public void setRecipe(IRecipeLayout recipeLayout, SolderingTableRecipeWrapper recipeWrapper, IIngredients ingredients) {
	        IGuiItemStackGroup guiStacks = recipeLayout.getItemStacks();

	        int slotId = 0;

	        //Input Slots
	        for (int l = 0; l < 3; l++) {
	            for (int i1 = 0; i1 < 3; i1++){
	                guiStacks.init(slotId++, true, 62 + i1 * 18, 17 + l * 18);
	            }
	        }
	        
	        //Output Slot
	        guiStacks.init(slotId, false, 140, 39);
	        guiStacks.set(ingredients);
	    }
}
