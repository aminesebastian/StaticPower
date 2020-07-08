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
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.integration.JEI.lumbermill.JEILumberMillRecipeHandler;
import theking530.staticpower.integration.JEI.lumbermill.LumberMillRecipeCategory;
import theking530.staticpower.items.cableattachments.digistorecraftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.tileentities.powered.lumbermill.GuiLumberMill;
import theking530.staticpower.utilities.Reference;

@JeiPlugin
public class PluginJEI implements IModPlugin {
	public static IJeiRuntime RUNTIME;
	@Nullable
	private LumberMillRecipeCategory lumberMillCategory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(StaticPowerContainerGui.class, new JEITabSlotAdjuster());
		registration.addRecipeClickArea(GuiLumberMill.class, 59, 32, 32, 16, LumberMillRecipeCategory.LUMBER_MILL_UID);
	}

	@SuppressWarnings("unused")
	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		Textures textures = Internal.getTextures();
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		IModIdHelper modIdHelper = jeiHelpers.getModIdHelper();
		lumberMillCategory = new LumberMillRecipeCategory(guiHelper);
		registration.addRecipeCategories(lumberMillCategory);
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
		registration.addRecipes(JEILumberMillRecipeHandler.getRecipes(), LumberMillRecipeCategory.LUMBER_MILL_UID);
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
