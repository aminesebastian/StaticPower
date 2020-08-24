package theking530.staticpower.integration.JEI;

import javax.annotation.Nullable;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
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
import theking530.staticpower.integration.JEI.evaporator.EvaporatorRecipeCategory;
import theking530.staticpower.integration.JEI.fermenter.FermenterRecipeCategory;
import theking530.staticpower.integration.JEI.fluidinfuser.FluidInfuserRecipeCategory;
import theking530.staticpower.integration.JEI.former.FormerRecipeCategory;
import theking530.staticpower.integration.JEI.fusionfurnace.FusionFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.lumbermill.LumberMillRecipeCategory;
import theking530.staticpower.integration.JEI.poweredfurnace.PoweredFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.poweredgrinder.PoweredGrinderRecipeCategory;
import theking530.staticpower.integration.JEI.solderingtable.SolderingTableRecipeCategory;
import theking530.staticpower.integration.JEI.solidgenerator.SolidGeneratorRecipeCategory;
import theking530.staticpower.integration.JEI.squeezer.SqueezerRecipeCategory;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;
import theking530.staticpower.items.cableattachments.digistorecraftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.tileentities.nonpowered.solderingtable.ContainerSolderingTable;
import theking530.staticpower.tileentities.powered.autocrafter.ContainerAutoCraftingTable;
import theking530.staticpower.tileentities.powered.autosolderingtable.ContainerAutoSolderingTable;

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
	@Nullable
	private FusionFurnaceRecipeCategory fusionFurnaceCateogry;
	@Nullable
	private EvaporatorRecipeCategory evaporationCategory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(StaticPowerContainerGui.class, new JEITabSlotAdjuster());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

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

		// Fusion Furnace
		fusionFurnaceCateogry = new FusionFurnaceRecipeCategory(guiHelper);
		registration.addRecipeCategories(fusionFurnaceCateogry);

		// Evaporator
		evaporationCategory = new EvaporatorRecipeCategory(guiHelper);
		registration.addRecipeCategories(evaporationCategory);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		RUNTIME = jeiRuntime;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(ModItems.CableCover);
		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.ElectringSolderingIron, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
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
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FusionFurnaceRecipe.RECIPE_TYPE), FusionFurnaceRecipeCategory.FUSION_FURNACE_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(EvaporatorRecipe.RECIPE_TYPE), EvaporatorRecipeCategory.EVAPORATOR_UID);
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
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FusionFurnace), FusionFurnaceRecipeCategory.FUSION_FURNACE_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Evaporator), EvaporatorRecipeCategory.EVAPORATOR_UID);
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
		return new ResourceLocation(StaticPower.MOD_ID, "plguin_jei");
	}
}
