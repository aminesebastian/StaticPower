package theking530.staticpower.integration.JEI;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import theking530.staticpower.assists.Reference;
import theking530.staticpower.integration.JEI.fluidinfuser.FluidInfuserRecipeCategory;
import theking530.staticpower.integration.JEI.solderingtable.SolderingTableRecipeCategory;

@JEIPlugin
public class StaticPowerJEIPlugin implements IModPlugin{
	public static IJeiHelpers jeiHelpers;
	
	public static final String SOLDERING_TABLE_UID = Reference.MOD_ID + ":solderingTable";
	public static final String FLUID_INFUSER_UID = Reference.MOD_ID + ":fluidInfuser";
	
	private static SolderingTableRecipeCategory solderingTable;
	private static FluidInfuserRecipeCategory fluidInfuser;
	
    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
    	jeiHelpers = registry.getJeiHelpers();
    	
    	solderingTable = new SolderingTableRecipeCategory(jeiHelpers.getGuiHelper());
    	fluidInfuser = new FluidInfuserRecipeCategory(jeiHelpers.getGuiHelper());
    	
        registry.addRecipeCategories(solderingTable);
        registry.addRecipeCategories(fluidInfuser);
    }

    @Override
    public void register(@Nonnull IModRegistry registry) {
    	solderingTable.initialize(registry);
    	fluidInfuser.initialize(registry);
               
        registry.addAdvancedGuiHandlers(new JEITabSlotAdjuster());
    }
}
