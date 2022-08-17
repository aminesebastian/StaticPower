package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.patternencoder.ContainerDigistorePatternEncoder;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.data.crafting.wrappers.packager.PackagerRecipe;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.categories.bottler.BottleRecipeCategory;
import theking530.staticpower.integration.JEI.categories.caster.CasterRecipeCategory;
import theking530.staticpower.integration.JEI.categories.cauldron.CauldronRecipeCategory;
import theking530.staticpower.integration.JEI.categories.centrifuge.CentrifugeRecipeCategory;
import theking530.staticpower.integration.JEI.categories.condenser.CondenserRecipeCategory;
import theking530.staticpower.integration.JEI.categories.covers.CoverRecipeCategory;
import theking530.staticpower.integration.JEI.categories.crucible.CrucibleRecipeCategory;
import theking530.staticpower.integration.JEI.categories.evaporator.EvaporatorRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fermenter.FermenterRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fertilization.FertilizerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fluidgenerator.FluidGeneratorRecipeCateogry;
import theking530.staticpower.integration.JEI.categories.fluidinfuser.FluidInfuserRecipeCategory;
import theking530.staticpower.integration.JEI.categories.former.FormerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.fusionfurnace.FusionFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.hammer.HammerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.lathe.LatheRecipeCategory;
import theking530.staticpower.integration.JEI.categories.lumbermill.LumberMillRecipeCategory;
import theking530.staticpower.integration.JEI.categories.mixer.MixerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.packager.PackagerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.poweredfurnace.PoweredFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.poweredgrinder.PoweredGrinderRecipeCategory;
import theking530.staticpower.integration.JEI.categories.refinery.RefineryRecipeCategory;
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
	private ThermalConductivityRecipeCategory thermalConductivityRecipeCategory;
	@Nullable
	private PackagerRecipeCategory packagerRecipeCategory;
	@Nullable
	private HammerRecipeCategory hammerRecipeCategory;
	@Nullable
	private CauldronRecipeCategory cauldronRecipeCategory;
	@Nullable
	private FertilizerRecipeCategory fertilizerRecipeCategory;
	@Nullable
	private RefineryRecipeCategory refineryRecipeCategory;

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

		// Packager
		packagerRecipeCategory = new PackagerRecipeCategory(guiHelper);
		registration.addRecipeCategories(packagerRecipeCategory);

		// Hammer
		hammerRecipeCategory = new HammerRecipeCategory(guiHelper);
		registration.addRecipeCategories(hammerRecipeCategory);

		// Cauldron
		cauldronRecipeCategory = new CauldronRecipeCategory(guiHelper);
		registration.addRecipeCategories(cauldronRecipeCategory);

		// Fertilization
		fertilizerRecipeCategory = new FertilizerRecipeCategory(guiHelper);
		registration.addRecipeCategories(fertilizerRecipeCategory);

		// Refinery
		refineryRecipeCategory = new RefineryRecipeCategory(guiHelper);
		registration.addRecipeCategories(refineryRecipeCategory);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		RUNTIME = jeiRuntime;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(ModItems.CableCover.get());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.BasicPortableBattery.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AdvancedPortableBattery.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.StaticPortableBattery.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EnergizedPortableBattery.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.LumumPortableBattery.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.CreativePortableBattery.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.BasicBatteryPack.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AdvancedBatteryPack.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.StaticBatteryPack.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EnergizedBatteryPack.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.LumumBatteryPack.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.CreativeBatteryPack.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.ElectringSolderingIron.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.BasicMiningDrill.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AdvancedMiningDrill.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.StaticMiningDrill.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EnergizedMiningDrill.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.LumumMiningDrill.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.BasicChainsaw.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AdvancedChainsaw.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.StaticChainsaw.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EnergizedChainsaw.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.LumumChainsaw.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.BasicMagnet.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AdvancedMagnet.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.StaticMagnet.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EnergizedMagnet.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.LumumMagnet.get(), new StaticPowerEnergyStoringItem.EnergyItemJEIInterpreter());

		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.IronFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.BasicFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AdvancedFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.StaticFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.EnergizedFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.LumumFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
		subtypeRegistry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.CreativeFluidCapsule.get(), new FluidCapsule.FluidCapsuleItemJEIInterpreter());
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(LumberMillRecipe.RECIPE_TYPE), LumberMillRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FormerRecipe.RECIPE_TYPE), FormerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.FURNACE_RECIPES.values(), PoweredFurnaceRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(GrinderRecipe.RECIPE_TYPE), PoweredGrinderRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FermenterRecipe.RECIPE_TYPE), FermenterRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(SqueezerRecipe.RECIPE_TYPE), SqueezerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(BottleRecipe.RECIPE_TYPE), BottleRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(SolidFuelRecipe.RECIPE_TYPE), SolidGeneratorRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(SolderingRecipe.RECIPE_TYPE), SolderingTableRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FluidInfusionRecipe.RECIPE_TYPE), FluidInfuserRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CentrifugeRecipe.RECIPE_TYPE), CentrifugeRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FusionFurnaceRecipe.RECIPE_TYPE), FusionFurnaceRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(EvaporatorRecipe.RECIPE_TYPE), EvaporatorRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FluidGeneratorRecipe.RECIPE_TYPE), FluidGeneratorRecipeCateogry.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CondensationRecipe.RECIPE_TYPE), CondenserRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(VulcanizerRecipe.RECIPE_TYPE), VulcanizerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(LatheRecipe.RECIPE_TYPE), LatheRecipeCategory.UID);
		registration.addRecipes(SmithingRecipeProvider.getRecipes(), SmithingRecipeCategory.UID);
		registration.addRecipes(ThermalConductivityRecipeProvider.getRecipes(), ThermalConductivityRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(MixerRecipe.RECIPE_TYPE), MixerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CrucibleRecipe.RECIPE_TYPE), CrucibleRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CastingRecipe.RECIPE_TYPE), CasterRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(TumblerRecipe.RECIPE_TYPE), TumblerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(PackagerRecipe.RECIPE_TYPE), PackagerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(HammerRecipe.RECIPE_TYPE), HammerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(CauldronRecipe.RECIPE_TYPE), CauldronRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(FertalizerRecipe.RECIPE_TYPE), FertilizerRecipeCategory.UID);
		registration.addRecipes(StaticPowerRecipeRegistry.getRecipesOfType(RefineryRecipe.RECIPE_TYPE), RefineryRecipeCategory.UID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LumberMill.get()), LumberMillRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Former.get()), FormerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.PoweredFurnace.get()), PoweredFurnaceRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.PoweredGrinder.get()), PoweredGrinderRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Fermenter.get()), FermenterRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Squeezer.get()), SqueezerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Bottler.get()), BottleRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SolidGenerator.get()), SolidGeneratorRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SolderingTable.get()), SolderingTableRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoSolderingTable.get()), SolderingTableRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoCraftingTable.get()), VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FluidInfuser.get()), FluidInfuserRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Centrifuge.get()), CentrifugeRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FusionFurnace.get()), FusionFurnaceRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Evaporator.get()), EvaporatorRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FluidGenerator.get()), FluidGeneratorRecipeCateogry.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Condenser.get()), CondenserRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Vulcanizer.get()), VulcanizerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoSmith.get()), SmithingRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Lathe.get()), LatheRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Mixer.get()), MixerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Crucible.get()), CrucibleRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Caster.get()), CasterRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Tumbler.get()), TumblerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Packager.get()), PackagerRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AluminumHeatCable.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AluminumHeatSink.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CopperHeatCable.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CopperHeatSink.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.GoldHeatCable.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.GoldHeatSink.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SilverHeatCable.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SilverHeatSink.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.TinHeatCable.get()), ThermalConductivityRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.TinHeatSink.get()), ThermalConductivityRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModItems.BronzeMetalHammer.get()), HammerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.CreativeMetalHammer.get()), HammerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.IronMetalHammer.get()), HammerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.CopperMetalHammer.get()), HammerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.TinMetalHammer.get()), HammerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.TungstenMetalHammer.get()), HammerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.ZincMetalHammer.get()), HammerRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.RustyCauldron.get()), CauldronRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CleanCauldron.get()), CauldronRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.BasicFarmer.get()), FertilizerRecipeCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(ModItems.SprinklerAttachment.get()), FertilizerRecipeCategory.UID);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Refinery.get()), RefineryRecipeCategory.UID);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addRecipeManagerPlugin(new CoverRecipeCategory(ModItems.CableCover.get()));
		registration.addRecipeManagerPlugin(new SmithingRecipeProvider());
		registration.addRecipeManagerPlugin(new ThermalConductivityRecipeProvider());
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerDigistoreCraftingTerminal, CraftingRecipe>(ContainerDigistoreCraftingTerminal.class, CraftingRecipe.class, 9),
				VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerAutoCraftingTable, CraftingRecipe>(ContainerAutoCraftingTable.class, CraftingRecipe.class, 9),
				VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerDigistorePatternEncoder, CraftingRecipe>(ContainerDigistorePatternEncoder.class, CraftingRecipe.class, 9),
				VanillaRecipeCategoryUid.CRAFTING);
		registration.addRecipeTransferHandler(new CraftingRecipeTransferHandler<ContainerSolderingTable, SolderingRecipe>(ContainerSolderingTable.class, SolderingRecipe.class, 9),
				SolderingTableRecipeCategory.UID);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerAutoSolderingTable, SolderingRecipe>(ContainerAutoSolderingTable.class, SolderingRecipe.class, 9),
				SolderingTableRecipeCategory.UID);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(StaticPower.MOD_ID, "plugin_jei");
	}
}
