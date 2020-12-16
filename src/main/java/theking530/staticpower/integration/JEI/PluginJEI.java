package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.patternencoder.ContainerDigistorePatternEncoder;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.categories.bottler.BottleRecipeCategory;
import theking530.staticpower.integration.JEI.categories.caster.CasterRecipeCategory;
import theking530.staticpower.integration.JEI.categories.centrifuge.CentrifugeRecipeCategory;
import theking530.staticpower.integration.JEI.categories.condenser.CondenserRecipeCategory;
import theking530.staticpower.integration.JEI.categories.covers.CoverRecipeCategory;
import theking530.staticpower.integration.JEI.categories.crucible.CrucibleRecipeCategory;
import theking530.staticpower.integration.JEI.categories.evaporator.EvaporatorRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fermenter.FermenterRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fluidgenerator.FluidGeneratorRecipeCateogry;
import theking530.staticpower.integration.JEI.categories.fluidinfuser.FluidInfuserRecipeCategory;
import theking530.staticpower.integration.JEI.categories.former.FormerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fusionfurnace.FusionFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.lathe.LatheRecipeCategory;
import theking530.staticpower.integration.JEI.categories.lumbermill.LumberMillRecipeCategory;
import theking530.staticpower.integration.JEI.categories.mixer.MixerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.poweredfurnace.PoweredFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.poweredgrinder.PoweredGrinderRecipeCategory;
import theking530.staticpower.integration.JEI.categories.smithing.SmithingRecipeCategory;
import theking530.staticpower.integration.JEI.categories.smithing.SmithingRecipeProvider;
import theking530.staticpower.integration.JEI.categories.solderingtable.SolderingTableRecipeCategory;
import theking530.staticpower.integration.JEI.categories.solidgenerator.SolidGeneratorRecipeCategory;
import theking530.staticpower.integration.JEI.categories.squeezer.SqueezerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityRecipeCategory;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityRecipeProvider;
import theking530.staticpower.integration.JEI.categories.tumbler.TumblerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.vulcanizer.VulcanizerRecipeCategory;
import theking530.staticpower.integration.JEI.ingredients.ProbabilityItemStackHelper;
import theking530.staticpower.integration.JEI.ingredients.ProbabilityItemStackRenderer;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;
import theking530.staticpower.items.fluidcapsule.FluidCapsule;
import theking530.staticpower.tileentities.nonpowered.solderingtable.ContainerSolderingTable;
import theking530.staticpower.tileentities.powered.autocrafter.ContainerAutoCraftingTable;
import theking530.staticpower.tileentities.powered.autosolderingtable.ContainerAutoSolderingTable;

@JeiPlugin
public class PluginJEI implements IModPlugin {
	public static final IIngredientType<ProbabilityItemStackOutput> PROBABILITY_ITEM_STACK = () -> ProbabilityItemStackOutput.class;
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
	@Nullable
	private CondenserRecipeCategory condensationCategory;
	@Nullable
	private FluidGeneratorRecipeCateogry fuidGeneratorRecipeCateogry;
	@Nullable
	private VulcanizerRecipeCategory vulcanizerRecipeCategory;
	@Nullable
	private SmithingRecipeCategory smithingRecipeCategory;
	@Nullable
	private LatheRecipeCategory latheRecipeCategory;
	@Nullable
	private MixerRecipeCategory mixerRecipeCategory;
	@Nullable
	private CrucibleRecipeCategory crucibleRecipeCategory;
	@Nullable
	private CasterRecipeCategory casterRecipeCategory;
	@Nullable
	private TumblerRecipeCategory tumblerRecipeCategory;
	@Nullable
	ThermalConductivityRecipeCategory thermalConductivityRecipeCategory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(StaticPowerContainerGui.class, new JEIContainerHandler());
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {
		List<ProbabilityItemStackOutput> probabilityStacks = new ArrayList<ProbabilityItemStackOutput>();

		ProbabilityItemStackHelper itemStackHelper = new ProbabilityItemStackHelper(registration);
		ProbabilityItemStackRenderer itemStackRenderer = new ProbabilityItemStackRenderer();
		registration.register(PROBABILITY_ITEM_STACK, probabilityStacks, itemStackHelper, itemStackRenderer);
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

		// Condenser
		condensationCategory = new CondenserRecipeCategory(guiHelper);
		registration.addRecipeCategories(condensationCategory);

		// Fluid Generator
		fuidGeneratorRecipeCateogry = new FluidGeneratorRecipeCateogry(guiHelper);
		registration.addRecipeCategories(fuidGeneratorRecipeCateogry);

		// Vulcanizer
		vulcanizerRecipeCategory = new VulcanizerRecipeCategory(guiHelper);
		registration.addRecipeCategories(vulcanizerRecipeCategory);

		// Smithing
		smithingRecipeCategory = new SmithingRecipeCategory(guiHelper);
		registration.addRecipeCategories(smithingRecipeCategory);

		// Lathe
		latheRecipeCategory = new LatheRecipeCategory(guiHelper);
		registration.addRecipeCategories(latheRecipeCategory);

		// Mixer
		mixerRecipeCategory = new MixerRecipeCategory(guiHelper);
		registration.addRecipeCategories(mixerRecipeCategory);

		// Crucible
		crucibleRecipeCategory = new CrucibleRecipeCategory(guiHelper);
		registration.addRecipeCategories(crucibleRecipeCategory);

		// Caster
		casterRecipeCategory = new CasterRecipeCategory(guiHelper);
		registration.addRecipeCategories(casterRecipeCategory);

		// Tumbler
		tumblerRecipeCategory = new TumblerRecipeCategory(guiHelper);
		registration.addRecipeCategories(tumblerRecipeCategory);

		// Thermal Conductivity
		thermalConductivityRecipeCategory = new ThermalConductivityRecipeCategory(guiHelper);
		registration.addRecipeCategories(thermalConductivityRecipeCategory);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		RUNTIME = jeiRuntime;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(ModItems.CableCover);
		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.AdvancedPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumPortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.CreativePortableBattery, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicBatteryPack, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.AdvancedBatteryPack, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticBatteryPack, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedBatteryPack, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumBatteryPack, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.CreativeBatteryPack, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(ModItems.ElectringSolderingIron, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicMiningDrill, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.AdvancedMiningDrill, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticMiningDrill, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedMiningDrill, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumMiningDrill, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicChainsaw, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.AdvancedChainsaw, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticChainsaw, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedChainsaw, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumChainsaw, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicMagnet, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.AdvancedMagnet, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticMagnet, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedMagnet, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumMagnet, new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(ModItems.IronFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.BasicFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.AdvancedFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.StaticFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.EnergizedFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.LumumFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(ModItems.CreativeFluidCapsule, new FluidCapsule.FluidCapsuleItemJEIInterpreter());
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
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FluidGeneratorRecipe.RECIPE_TYPE), FluidGeneratorRecipeCateogry.FLUID_GENERATOR_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CondensationRecipe.RECIPE_TYPE), CondenserRecipeCategory.CONDENSER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(VulcanizerRecipe.RECIPE_TYPE), VulcanizerRecipeCategory.VULCANIZER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(LatheRecipe.RECIPE_TYPE), LatheRecipeCategory.LATHE_UID);
		registration.addRecipes(SmithingRecipeProvider.getRecipes(), SmithingRecipeCategory.AUTO_SMITHING_UID);
		registration.addRecipes(ThermalConductivityRecipeProvider.getRecipes(), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(MixerRecipe.RECIPE_TYPE), MixerRecipeCategory.MIXER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CrucibleRecipe.RECIPE_TYPE), CrucibleRecipeCategory.CRUCIBLE_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CastingRecipe.RECIPE_TYPE), CasterRecipeCategory.CASTER_UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(TumblerRecipe.RECIPE_TYPE), TumblerRecipeCategory.TUMBLER_UID);
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
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FluidGenerator), FluidGeneratorRecipeCateogry.FLUID_GENERATOR_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Condenser), CondenserRecipeCategory.CONDENSER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Vulcanizer), VulcanizerRecipeCategory.VULCANIZER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoSmith), SmithingRecipeCategory.AUTO_SMITHING_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Lathe), LatheRecipeCategory.LATHE_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Mixer), MixerRecipeCategory.MIXER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Crucible), CrucibleRecipeCategory.CRUCIBLE_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Caster), CasterRecipeCategory.CASTER_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Tumbler), TumblerRecipeCategory.TUMBLER_UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AluminiumHeatCable), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AluminiumHeatSink), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CopperHeatCable), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CopperHeatSink), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.GoldHeatCable), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.GoldHeatSink), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SilverHeatCable), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SilverHeatSink), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.TinHeatCable), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.TinHeatSink), ThermalConductivityRecipeCategory.THERMAL_CONDUCTIVITY_UID);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addRecipeManagerPlugin(new CoverRecipeCategory(ModItems.CableCover));
		registration.addRecipeManagerPlugin(new SmithingRecipeProvider());
		registration.addRecipeManagerPlugin(new ThermalConductivityRecipeProvider());
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerDigistoreCraftingTerminal.class, 9), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerAutoCraftingTable.class, 9), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerDigistorePatternEncoder.class, 9), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerSolderingTable.class, 9), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<>(ContainerAutoSolderingTable.class, 9), SolderingTableRecipeCategory.SOLDERING_TABLE_UID);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(StaticPower.MOD_ID, "plugin_jei");
	}
}
