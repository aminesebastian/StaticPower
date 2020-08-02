package theking530.staticpower.integration.JEI;

import javax.annotation.Nullable;

import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.helpers.IModIdHelper;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.gui.textures.Textures;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.bottler.BottleRecipeCategory;
import theking530.staticpower.integration.JEI.centrifuge.CentrifugeRecipeCategory;
import theking530.staticpower.integration.JEI.covers.CoverRecipeCategory;
import theking530.staticpower.integration.JEI.fermenter.FermenterRecipeCategory;
import theking530.staticpower.integration.JEI.fluidinfuser.FluidInfuserRecipeCategory;
import theking530.staticpower.integration.JEI.former.FormerRecipeCategory;
import theking530.staticpower.integration.JEI.lumbermill.LumberMillRecipeCategory;
import theking530.staticpower.integration.JEI.poweredfurnace.PoweredFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.poweredgrinder.PoweredGrinderRecipeCategory;
import theking530.staticpower.integration.JEI.solderingtable.SolderingTableRecipeCategory;
import theking530.staticpower.integration.JEI.solidgenerator.SolidGeneratorRecipeCategory;
import theking530.staticpower.integration.JEI.squeezer.SqueezerRecipeCategory;
import theking530.staticpower.items.cableattachments.digistorecraftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.tileentities.nonpowered.solderingtable.ContainerSolderingTable;
import theking530.staticpower.tileentities.powered.autocrafter.ContainerAutoCraftingTable;
import theking530.staticpower.tileentities.powered.autosolderingtable.ContainerAutoSolderingTable;
import theking530.staticpower.tileentities.powered.bottler.GuiBottler;
import theking530.staticpower.tileentities.powered.fermenter.GuiFermenter;
import theking530.staticpower.tileentities.powered.fluidinfuser.GuiFluidInfuser;
import theking530.staticpower.tileentities.powered.poweredgrinder.GuiPoweredGrinder;
import theking530.staticpower.tileentities.powered.squeezer.GuiSqueezer;
import theking530.staticpower.utilities.Reference;

@JeiPlugin
public class PluginJEI implements IModPlugin {
	public static IJeiRuntime RUNTIME;
	@Nullable
	private LumberMillRecipeCategory lumberMillCategory;
	@Nullable
	private FormerRecipeCategory formerCategory;
	@Nullable
	private PoweredGrinderRecipeCategory poweredGrinderCategory;
	@Nullable
	private PoweredFurnaceRecipeCategory poweredFurnaceCategory;
	@Nullable
	private FermenterRecipeCategory fermenterCategory;
	@Nullable
	private SqueezerRecipeCategory squeezerCategory;
	@Nullable
	private BottleRecipeCategory bottlerCategory;
	@Nullable
	private SolidGeneratorRecipeCategory solidGeneratorCategory;
	@Nullable
	private SolderingTableRecipeCategory solderingTableCategory;
	@Nullable
	private FluidInfuserRecipeCategory fluidInfuserCategory;
	@Nullable
	private CentrifugeRecipeCategory centrifugeCategory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(StaticPowerContainerGui.class, new JEITabSlotAdjuster());

		registration.addRecipeClickArea(GuiPoweredGrinder.class, 79, 39, 18, 17, PoweredGrinderRecipeCategory.GRINDER_UID);
		registration.addRecipeClickArea(GuiFermenter.class, 97, 40, 48, 5, FermenterRecipeCategory.FERMENTER_UID);
		registration.addRecipeClickArea(GuiSqueezer.class, 74, 34, 28, 5, SqueezerRecipeCategory.SQUEEZER_UID);
		registration.addRecipeClickArea(GuiBottler.class, 74, 34, 28, 5, BottleRecipeCategory.BOTTLER_UID);
		registration.addRecipeClickArea(GuiFluidInfuser.class, 102, 36, 17, 5, FluidInfuserRecipeCategory.FLUID_INFUSER_UID);
		registration.addRecipeClickArea(GuiPoweredGrinder.class, 79, 39, 18, 17, CentrifugeRecipeCategory.CENTRIFUGE_UID);

//		registration.addRecipeClickArea(GuiSolidGenerator.class, 37, 32, 22, 16, SolidGeneratorRecipeCategory.SOLID_GENERATOR_UID);
//		registration.addRecipeClickArea(GuiPoweredFurnace.class, 75, 30, 22, 16, PoweredFurnaceRecipeCategory.POWERED_FURNACE_UID);
//		registration.addRecipeClickArea(GuiLumberMill.class, 59, 32, 22, 16, LumberMillRecipeCategory.LUMBER_MILL_UID);
//		registration.addRecipeClickArea(GuiFormer.class, 82, 34, 22, 16, FormerRecipeCategory.FORMER_UID);
//		registration.addRecipeClickArea(GuiAutoSolderingTable.class, 99, 38, 22, 16, SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
//		registration.addRecipeClickArea(GuiAutoCraftingTable.class, 99, 38, 22, 16, VanillaRecipeCategoryUid.CRAFTING);
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
		poweredFurnaceCategory = new PoweredFurnaceRecipeCategory(guiHelper);
		registration.addRecipeCategories(poweredFurnaceCategory);

		// Powered Grinder
		poweredGrinderCategory = new PoweredGrinderRecipeCategory(guiHelper);
		registration.addRecipeCategories(poweredGrinderCategory);

		// Fermenter
		fermenterCategory = new FermenterRecipeCategory(guiHelper);
		registration.addRecipeCategories(fermenterCategory);

		// Squeezer
		squeezerCategory = new SqueezerRecipeCategory(guiHelper);
		registration.addRecipeCategories(squeezerCategory);

		// Bottler
		bottlerCategory = new BottleRecipeCategory(guiHelper);
		registration.addRecipeCategories(bottlerCategory);

		// Solid Generator
		solidGeneratorCategory = new SolidGeneratorRecipeCategory(guiHelper);
		registration.addRecipeCategories(solidGeneratorCategory);

		// Soldering Iron
		solderingTableCategory = new SolderingTableRecipeCategory(guiHelper);
		registration.addRecipeCategories(solderingTableCategory);

		// Fluid Infuser
		fluidInfuserCategory = new FluidInfuserRecipeCategory(guiHelper);
		registration.addRecipeCategories(fluidInfuserCategory);
		
		// Centrifuge
		centrifugeCategory = new CentrifugeRecipeCategory(guiHelper);
		registration.addRecipeCategories(centrifugeCategory);
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
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(SqueezerRecipe.RECIPE_TYPE), SqueezerRecipeCategory.SQUEEZER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(BottleRecipe.RECIPE_TYPE), BottleRecipeCategory.BOTTLER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(SolidFuelRecipe.RECIPE_TYPE), SolidGeneratorRecipeCategory.SOLID_GENERATOR_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(SolderingRecipe.RECIPE_TYPE), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FluidInfusionRecipe.RECIPE_TYPE), FluidInfuserRecipeCategory.FLUID_INFUSER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CentrifugeRecipe.RECIPE_TYPE), CentrifugeRecipeCategory.CENTRIFUGE_UID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LumberMill), LumberMillRecipeCategory.LUMBER_MILL_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Former), FormerRecipeCategory.FORMER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.PoweredFurnace), PoweredFurnaceRecipeCategory.POWERED_FURNACE_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.PoweredGrinder), PoweredGrinderRecipeCategory.GRINDER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Fermenter), FermenterRecipeCategory.FERMENTER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Squeezer), SqueezerRecipeCategory.SQUEEZER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Bottler), BottleRecipeCategory.BOTTLER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SolidGenerator), SolidGeneratorRecipeCategory.SOLID_GENERATOR_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SolderingTable), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoSolderingTable), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoCraftingTable), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FluidInfuser), FluidInfuserRecipeCategory.FLUID_INFUSER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Centrifuge), CentrifugeRecipeCategory.CENTRIFUGE_UID);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addRecipeManagerPlugin(new CoverRecipeCategory(ModItems.CableCover));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerDigistoreCraftingTerminal.class, 9), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerAutoCraftingTable.class, 9), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerSolderingTable.class, 9), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerAutoSolderingTable.class, 9), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Reference.MOD_ID, "plguin_jei");
	}
}
