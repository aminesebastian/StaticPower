package theking530.staticpower.integration.JEI;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.handlers.crafting.registries.SolderingRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.SolderingRecipeWrapper;
import theking530.staticpower.integration.JEI.solderingtable.SolderingTableRecipeCategory;
import theking530.staticpower.integration.JEI.solderingtable.SolderingTableRecipeWrapper;
import theking530.staticpower.tileentity.solderingtable.GuiSolderingTable;

@JEIPlugin
public class StaticPowerJEIPlugin implements IModPlugin{
	
	public static IJeiHelpers jeiHelpers;
	public static final String SOLDERING_TABLE_UID = Reference.MODID + ":solderingTable";
	
    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
    	jeiHelpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new SolderingTableRecipeCategory(jeiHelpers.getGuiHelper()));
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
        //LoaderHelper.reloadLocalOreDict();
        registry.handleRecipes(SolderingRecipeWrapper.class, SolderingTableRecipeWrapper.FACTORY, SOLDERING_TABLE_UID);
        registry.addRecipes(SolderingRecipeRegistry.Soldering().getRecipeList(), SOLDERING_TABLE_UID);
        registry.addRecipeClickArea(GuiSolderingTable.class, 111, 69, 26, 19, SOLDERING_TABLE_UID);
        //registry.addRecipeCatalyst(new ItemStack(SHBlocks.blockHammerCraft), Reference.JEI.HAMMER_CRAFTING_UID);
    }
}
