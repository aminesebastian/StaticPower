package theking530.staticpower.integration.JEI;

import javax.annotation.Nullable;

import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.textures.Textures;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.integration.JEI.fermenter.FermenterRecipeCategory;
import theking530.staticpower.integration.JEI.former.FormerRecipeCategory;
import theking530.staticpower.integration.JEI.lumbermill.LumberMillRecipeCategory;
import theking530.staticpower.integration.JEI.poweredfurnace.PoweredFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.poweredgrinder.PoweredGrinderRecipeCategory;
import theking530.staticpower.items.cableattachments.digistorecraftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.tileentities.powered.fermenter.GuiFermenter;
import theking530.staticpower.tileentities.powered.former.GuiFormer;
import theking530.staticpower.tileentities.powered.lumbermill.GuiLumberMill;
import theking530.staticpower.tileentities.powered.poweredfurnace.GuiPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.GuiPoweredGrinder;
import theking530.staticpower.utilities.Reference;

@JeiPlugin
public class PluginJEI implements IModPlugin {
	public static IJeiRuntime RUNTIME;
	@Nullable
	private LumberMillRecipeCategory lumberMillCategory;
	@Nullable
	private FormerRecipeCategory formerCategory;
	@Nullable
	private PoweredGrinderRecipeCategory poweredGrinderRecipe;
	@Nullable
	private PoweredFurnaceRecipeCategory poweredFurnaceRecipe;
	@Nullable
	private FermenterRecipeCategory fermenterRecipe;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(StaticPowerContainerGui.class, new JEITabSlotAdjuster());

		registration.addRecipeClickArea(GuiLumberMill.class, 59, 32, 24, 16, LumberMillRecipeCategory.LUMBER_MILL_UID);
		registration.addRecipeClickArea(GuiFormer.class, 82, 34, 24, 16, FormerRecipeCategory.FORMER_UID);
		registration.addRecipeClickArea(GuiPoweredFurnace.class, 59, 32, 24, 16, PoweredFurnaceRecipeCategory.POWERED_FURNACE_UID);
		registration.addRecipeClickArea(GuiPoweredGrinder.class, 82, 34, 24, 16, PoweredGrinderRecipeCategory.GRINDER_UID);
		registration.addRecipeClickArea(GuiFermenter.class, 82, 34, 24, 16, FermenterRecipeCategory.FERMENTER_UID);
	}

	@SuppressWarnings("unused")
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		Textures textures = Internal.getTextures();
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		IModIdHelper modIdHelper = jeiHelpers.getModIdHelper();

		// Lumber Mill
		lumberMillCategory = new LumberMillRecipeCategory(guiHelper);
		registration.addRecipeCategories(lumberMillCategory);

		// Former
		formerCategory = new FormerRecipeCategory(guiHelper);
		registration.addRecipeCategories(formerCategory);

		// Powered Furnace
		poweredFurnaceRecipe = new PoweredFurnaceRecipeCategory(guiHelper);
		registration.addRecipeCategories(poweredFurnaceRecipe);

		// Powered Grinder
		poweredGrinderRecipe = new PoweredGrinderRecipeCategory(guiHelper);
		registration.addRecipeCategories(poweredGrinderRecipe);

		// Fermenter
		fermenterRecipe = new FermenterRecipeCategory(guiHelper);
		registration.addRecipeCategories(fermenterRecipe);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		RUNTIME = jeiRuntime;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(ModItems.CableCover);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(LumberMillRecipe.RECIPE_TYPE), LumberMillRecipeCategory.LUMBER_MILL_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FormerRecipe.RECIPE_TYPE), FormerRecipeCategory.FORMER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.FURNACE_RECIPES, PoweredFurnaceRecipeCategory.POWERED_FURNACE_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(GrinderRecipe.RECIPE_TYPE), PoweredGrinderRecipeCategory.GRINDER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FermenterRecipe.RECIPE_TYPE), FermenterRecipeCategory.FERMENTER_UID);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerDigistoreCraftingTerminal.class), VanillaRecipeCategoryUid.CRAFTING);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Reference.MOD_ID, "plguin_jei");
	}
}
