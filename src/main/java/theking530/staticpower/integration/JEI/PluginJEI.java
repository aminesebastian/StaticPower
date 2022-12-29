package theking530.staticpower.integration.JEI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.machines.autocrafter.ContainerAutoCraftingTable;
import theking530.staticpower.blockentities.machines.autosolderingtable.ContainerAutoSolderingTable;
import theking530.staticpower.blockentities.nonpowered.solderingtable.ContainerSolderingTable;
import theking530.staticpower.cables.attachments.digistore.craftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.cables.attachments.digistore.patternencoder.ContainerDigistorePatternEncoder;
import theking530.staticpower.client.gui.StaticPowerContainerGui;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
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
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.solidfuel.SolidFuelRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.integration.JEI.categories.AlloyFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.BottleRecipeCategory;
import theking530.staticpower.integration.JEI.categories.CasterRecipeCategory;
import theking530.staticpower.integration.JEI.categories.CauldronRecipeCategory;
import theking530.staticpower.integration.JEI.categories.CentrifugeRecipeCategory;
import theking530.staticpower.integration.JEI.categories.CondenserRecipeCategory;
import theking530.staticpower.integration.JEI.categories.CoverRecipeCategory;
import theking530.staticpower.integration.JEI.categories.CrucibleRecipeCategory;
import theking530.staticpower.integration.JEI.categories.EvaporatorRecipeCategory;
import theking530.staticpower.integration.JEI.categories.FermenterRecipeCategory;
import theking530.staticpower.integration.JEI.categories.FertilizerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.FluidGeneratorRecipeCateogry;
import theking530.staticpower.integration.JEI.categories.FluidInfuserRecipeCategory;
import theking530.staticpower.integration.JEI.categories.FormerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.FusionFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.HammerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.LatheRecipeCategory;
import theking530.staticpower.integration.JEI.categories.LumberMillRecipeCategory;
import theking530.staticpower.integration.JEI.categories.MixerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.PackagerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.PoweredFurnaceRecipeCategory;
import theking530.staticpower.integration.JEI.categories.PoweredGrinderRecipeCategory;
import theking530.staticpower.integration.JEI.categories.RefineryRecipeCategory;
import theking530.staticpower.integration.JEI.categories.SolderingTableRecipeCategory;
import theking530.staticpower.integration.JEI.categories.SolidGeneratorRecipeCategory;
import theking530.staticpower.integration.JEI.categories.SqueezerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.TumblerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.VulcanizerRecipeCategory;
import theking530.staticpower.integration.JEI.categories.packager.PackagerRecipeProvider;
import theking530.staticpower.integration.JEI.categories.smithing.SmithingRecipeCategory;
import theking530.staticpower.integration.JEI.categories.smithing.SmithingRecipeProvider;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityRecipeCategory;
import theking530.staticpower.integration.JEI.categories.thermalconductivity.ThermalConductivityRecipeProvider;
import theking530.staticpower.integration.JEI.ingredients.probabilitystack.ProbabilityItemStackHelper;
import theking530.staticpower.integration.JEI.ingredients.probabilitystack.ProbabilityItemStackRenderer;
import theking530.staticpower.items.StaticPowerEnergyStoringItem;
import theking530.staticpower.items.fluidcapsule.FluidCapsule;

@JeiPlugin
public class PluginJEI implements IModPlugin {
	public static final IIngredientType<ProbabilityItemStackOutput> PROBABILITY_ITEM_STACK = () -> ProbabilityItemStackOutput.class;
	public static IJeiRuntime RUNTIME;

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

		registerCategory(registration, new LumberMillRecipeCategory(guiHelper));
		registerCategory(registration, new FormerRecipeCategory(guiHelper));
		registerCategory(registration, new PoweredFurnaceRecipeCategory(guiHelper));
		registerCategory(registration, new PoweredGrinderRecipeCategory(guiHelper));
		registerCategory(registration, new FermenterRecipeCategory(guiHelper));
		registerCategory(registration, new SqueezerRecipeCategory(guiHelper));
		registerCategory(registration, new BottleRecipeCategory(guiHelper));
		registerCategory(registration, new SolidGeneratorRecipeCategory(guiHelper));
		registerCategory(registration, new SolderingTableRecipeCategory(guiHelper));
		registerCategory(registration, new FluidInfuserRecipeCategory(guiHelper));
		registerCategory(registration, new CentrifugeRecipeCategory(guiHelper));
		registerCategory(registration, new FusionFurnaceRecipeCategory(guiHelper));
		registerCategory(registration, new EvaporatorRecipeCategory(guiHelper));
		registerCategory(registration, new CondenserRecipeCategory(guiHelper));
		registerCategory(registration, new FluidGeneratorRecipeCateogry(guiHelper));
		registerCategory(registration, new VulcanizerRecipeCategory(guiHelper));
		registerCategory(registration, new SmithingRecipeCategory(guiHelper));
		registerCategory(registration, new LatheRecipeCategory(guiHelper));
		registerCategory(registration, new MixerRecipeCategory(guiHelper));
		registerCategory(registration, new CrucibleRecipeCategory(guiHelper));
		registerCategory(registration, new CasterRecipeCategory(guiHelper));
		registerCategory(registration, new TumblerRecipeCategory(guiHelper));
		registerCategory(registration, new ThermalConductivityRecipeCategory(guiHelper));
		registerCategory(registration, new PackagerRecipeCategory(guiHelper));
		registerCategory(registration, new HammerRecipeCategory(guiHelper));
		registerCategory(registration, new CauldronRecipeCategory(guiHelper));
		registerCategory(registration, new FertilizerRecipeCategory(guiHelper));
		registerCategory(registration, new RefineryRecipeCategory(guiHelper));
		registerCategory(registration, new AlloyFurnaceRecipeCategory(guiHelper));
	}

	protected void registerCategory(IRecipeCategoryRegistration registration, IRecipeCategory<?> category) {
		registration.addRecipeCategories(category);
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
		@SuppressWarnings("resource")
		RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

		registration.addRecipes(LumberMillRecipeCategory.TYPE, recipeManager.getAllRecipesFor(LumberMillRecipe.RECIPE_TYPE));
		registration.addRecipes(PoweredFurnaceRecipeCategory.TYPE, recipeManager.getAllRecipesFor(RecipeType.SMELTING));
		registration.addRecipes(FormerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(FormerRecipe.RECIPE_TYPE));
		registration.addRecipes(SolderingTableRecipeCategory.TYPE, recipeManager.getAllRecipesFor(SolderingRecipe.RECIPE_TYPE));
		registration.addRecipes(ThermalConductivityRecipeCategory.TYPE, ThermalConductivityRecipeProvider.getRecipes());
		registration.addRecipes(HammerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(HammerRecipe.RECIPE_TYPE));
		registration.addRecipes(CauldronRecipeCategory.TYPE, recipeManager.getAllRecipesFor(CauldronRecipe.RECIPE_TYPE));
		registration.addRecipes(FertilizerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(FertalizerRecipe.RECIPE_TYPE));
		registration.addRecipes(AlloyFurnaceRecipeCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipeTypes.ALLOY_FURNACE_RECIPE_TYPE.get()));
		registration.addRecipes(PoweredGrinderRecipeCategory.TYPE, recipeManager.getAllRecipesFor(GrinderRecipe.RECIPE_TYPE));
		registration.addRecipes(CentrifugeRecipeCategory.TYPE, recipeManager.getAllRecipesFor(CentrifugeRecipe.RECIPE_TYPE));
		registration.addRecipes(SolidGeneratorRecipeCategory.TYPE, recipeManager.getAllRecipesFor(SolidFuelRecipe.RECIPE_TYPE));
		registration.addRecipes(LatheRecipeCategory.TYPE, recipeManager.getAllRecipesFor(LatheRecipe.RECIPE_TYPE));
		registration.addRecipes(TumblerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(TumblerRecipe.RECIPE_TYPE));
		registration.addRecipes(SmithingRecipeCategory.TYPE, SmithingRecipeProvider.getRecipes());
		registration.addRecipes(PackagerRecipeCategory.TYPE, PackagerRecipeProvider.getRecipes());
		registration.addRecipes(FermenterRecipeCategory.TYPE, recipeManager.getAllRecipesFor(FermenterRecipe.RECIPE_TYPE));
		registration.addRecipes(SqueezerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(SqueezerRecipe.RECIPE_TYPE));
		registration.addRecipes(BottleRecipeCategory.TYPE, recipeManager.getAllRecipesFor(BottleRecipe.RECIPE_TYPE));
		registration.addRecipes(FluidInfuserRecipeCategory.TYPE, recipeManager.getAllRecipesFor(FluidInfusionRecipe.RECIPE_TYPE));
		registration.addRecipes(FluidGeneratorRecipeCateogry.TYPE, recipeManager.getAllRecipesFor(FluidGeneratorRecipe.RECIPE_TYPE));
		registration.addRecipes(CasterRecipeCategory.TYPE, recipeManager.getAllRecipesFor(CastingRecipe.RECIPE_TYPE));
		registration.addRecipes(CasterRecipeCategory.TYPE, recipeManager.getAllRecipesFor(CastingRecipe.RECIPE_TYPE));
		registration.addRecipes(FusionFurnaceRecipeCategory.TYPE, recipeManager.getAllRecipesFor(FusionFurnaceRecipe.RECIPE_TYPE));
		registration.addRecipes(EvaporatorRecipeCategory.TYPE, recipeManager.getAllRecipesFor(EvaporatorRecipe.RECIPE_TYPE));
		registration.addRecipes(CondenserRecipeCategory.TYPE, recipeManager.getAllRecipesFor(CondensationRecipe.RECIPE_TYPE));
		registration.addRecipes(VulcanizerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(VulcanizerRecipe.RECIPE_TYPE));
		registration.addRecipes(MixerRecipeCategory.TYPE, recipeManager.getAllRecipesFor(MixerRecipe.RECIPE_TYPE));
		registration.addRecipes(CrucibleRecipeCategory.TYPE, recipeManager.getAllRecipesFor(CrucibleRecipe.RECIPE_TYPE));
		registration.addRecipes(RefineryRecipeCategory.TYPE, recipeManager.getAllRecipesFor(RefineryRecipe.RECIPE_TYPE));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.LumberMill.get()), LumberMillRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.PoweredFurnace.get()), PoweredFurnaceRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Former.get()), FormerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SolderingTable.get()), SolderingTableRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoSolderingTable.get()), SolderingTableRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AlloyFurnace.get()), AlloyFurnaceRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.PoweredGrinder.get()), PoweredGrinderRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Centrifuge.get()), CentrifugeRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.SolidGenerator.get()), SolidGeneratorRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoSmith.get()), SmithingRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Lathe.get()), LatheRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Tumbler.get()), TumblerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Packager.get()), PackagerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Fermenter.get()), FermenterRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Squeezer.get()), SqueezerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Bottler.get()), BottleRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FluidInfuser.get()), FluidInfuserRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FluidGenerator.get()), FluidGeneratorRecipeCateogry.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Caster.get()), CasterRecipeCategory.TYPE);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AutoCraftingTable.get()), RecipeTypes.CRAFTING);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.FusionFurnace.get()), FusionFurnaceRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Evaporator.get()), EvaporatorRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Condenser.get()), CondenserRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Vulcanizer.get()), VulcanizerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Mixer.get()), MixerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.Crucible.get()), CrucibleRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.RefineryController.get()), RefineryRecipeCategory.TYPE);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AluminumHeatCable.get()), ThermalConductivityRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.AluminumHeatSink.get()), ThermalConductivityRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CopperHeatCable.get()), ThermalConductivityRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CopperHeatSink.get()), ThermalConductivityRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.GoldHeatCable.get()), ThermalConductivityRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.GoldHeatSink.get()), ThermalConductivityRecipeCategory.TYPE);

		registration.addRecipeCatalyst(new ItemStack(ModItems.BronzeMetalHammer.get()), HammerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.CreativeMetalHammer.get()), HammerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.IronMetalHammer.get()), HammerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.CopperMetalHammer.get()), HammerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.TinMetalHammer.get()), HammerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.TungstenMetalHammer.get()), HammerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.ZincMetalHammer.get()), HammerRecipeCategory.TYPE);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.RustyCauldron.get()), CauldronRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModBlocks.CleanCauldron.get()), CauldronRecipeCategory.TYPE);

		registration.addRecipeCatalyst(new ItemStack(ModBlocks.BasicFarmer.get()), FertilizerRecipeCategory.TYPE);
		registration.addRecipeCatalyst(new ItemStack(ModItems.SprinklerAttachment.get()), FertilizerRecipeCategory.TYPE);
	}

	@Override
	public void registerAdvanced(IAdvancedRegistration registration) {
		registration.addRecipeManagerPlugin(new CoverRecipeCategory(ModItems.CableCover.get()));
		registration.addRecipeManagerPlugin(new SmithingRecipeProvider());
		registration.addRecipeManagerPlugin(new ThermalConductivityRecipeProvider());
		registration.addRecipeManagerPlugin(new PackagerRecipeProvider());
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerDigistoreCraftingTerminal, CraftingRecipe>(ContainerDigistoreCraftingTerminal.class, RecipeTypes.CRAFTING, 9),
				RecipeTypes.CRAFTING);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerAutoCraftingTable, CraftingRecipe>(ContainerAutoCraftingTable.class, RecipeTypes.CRAFTING, 9), RecipeTypes.CRAFTING);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerDigistorePatternEncoder, CraftingRecipe>(ContainerDigistorePatternEncoder.class, RecipeTypes.CRAFTING, 9),
				RecipeTypes.CRAFTING);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerSolderingTable, SolderingRecipe>(ContainerSolderingTable.class, SolderingTableRecipeCategory.TYPE, 9),
				SolderingTableRecipeCategory.TYPE);
		registration.addRecipeTransferHandler(
				new CraftingRecipeTransferHandler<ContainerAutoSolderingTable, SolderingRecipe>(ContainerAutoSolderingTable.class, SolderingTableRecipeCategory.TYPE, 9),
				SolderingTableRecipeCategory.TYPE);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(StaticPower.MOD_ID, "plugin_jei");
	}
}
