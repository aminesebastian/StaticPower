package theking530.staticpower.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.data.crafting.wrappers.covers.CoverRecipe;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.data.crafting.wrappers.enchanter.EnchanterRecipe;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipe;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.data.research.Research;

public class ModRecipeTypes {
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, StaticPower.MOD_ID);
	public static final RegistryObject<RecipeType<AutoSmithRecipe>> AUTO_SMITH_RECIPE_TYPE = TYPES.register(AutoSmithRecipe.ID, () -> AutoSmithRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<BottleRecipe>> BOTTLER_RECIPE_TYPE = TYPES.register(BottleRecipe.ID, () -> new StaticPowerRecipeType<BottleRecipe>());
	public static final RegistryObject<RecipeType<CastingRecipe>> CASTING_RECIPE_TYPE = TYPES.register(CastingRecipe.ID, () -> CastingRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<CauldronRecipe>> CAULDRON_RECIPE_TYPE = TYPES.register(CauldronRecipe.ID, () -> CauldronRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = TYPES.register(CentrifugeRecipe.ID, () -> CentrifugeRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<CondensationRecipe>> CONDENSATION_RECIPE_TYPE = TYPES.register(CondensationRecipe.ID, () -> CondensationRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<CoverRecipe>> COVER_RECIPE_TYPE = TYPES.register(CoverRecipe.ID, () -> CoverRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<CrucibleRecipe>> CRUCIBLE_RECIPE_TYPE = TYPES.register(CrucibleRecipe.ID, () -> CrucibleRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<EnchanterRecipe>> ENCHANTER_RECIPE_TYPE = TYPES.register(EnchanterRecipe.ID, () -> EnchanterRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<EvaporatorRecipe>> EVAPORATOR_RECIPE_TYPE = TYPES.register(EvaporatorRecipe.ID, () -> EvaporatorRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<FermenterRecipe>> FERMENTER_RECIPE_TYPE = TYPES.register(FermenterRecipe.ID, () -> FermenterRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<FertalizerRecipe>> FERTALIZER_RECIPE_TYPE = TYPES.register(FertalizerRecipe.ID, () -> FertalizerRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<FluidGeneratorRecipe>> FLUID_GENERATOR_RECIPE_TYPE = TYPES.register(FluidGeneratorRecipe.ID,
			() -> FluidGeneratorRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<FluidInfusionRecipe>> FLUID_INFUSION_RECIPE_TYPE = TYPES.register(FluidInfusionRecipe.ID, () -> FluidInfusionRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<FormerRecipe>> FORMER_RECIPE_TYPE = TYPES.register(FormerRecipe.ID, () -> FormerRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<FusionFurnaceRecipe>> FUSION_FURNACE_RECIPE_TYPE = TYPES.register(FusionFurnaceRecipe.ID, () -> FusionFurnaceRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<GrinderRecipe>> GRINDER_RECIPE_TYPE = TYPES.register(GrinderRecipe.ID, () -> new StaticPowerRecipeType<GrinderRecipe>());
	public static final RegistryObject<RecipeType<HammerRecipe>> HAMMER_RECIPE_TYPE = TYPES.register(HammerRecipe.ID, () -> new StaticPowerRecipeType<HammerRecipe>());
	public static final RegistryObject<RecipeType<LatheRecipe>> LATHE_RECIPE_TYPE = TYPES.register(LatheRecipe.ID, () -> LatheRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<LumberMillRecipe>> LUMBER_MILL_RECIPE_TYPE = TYPES.register(LumberMillRecipe.ID, () -> LumberMillRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<MixerRecipe>> MIXER_RECIPE_TYPE = TYPES.register(MixerRecipe.ID, () -> MixerRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<RefineryRecipe>> REFINERY_RECIPE_TYPE = TYPES.register(RefineryRecipe.ID, () -> RefineryRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<Research>> RESEARCH_RECIPE_TYPE = TYPES.register(Research.ID, () -> new StaticPowerRecipeType<Research>());
	public static final RegistryObject<RecipeType<SolderingRecipe>> SOLDERING_RECIPE_TYPE = TYPES.register(SolderingRecipe.ID, () -> SolderingRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<SqueezerRecipe>> SQUEEZER_RECIPE_TYPE = TYPES.register(SqueezerRecipe.ID, () -> SqueezerRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<ThermalConductivityRecipe>> THERMAL_CONDUCTIVITY_RECIPE_TYPE = TYPES.register(ThermalConductivityRecipe.ID,
			() -> ThermalConductivityRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<TumblerRecipe>> TUMBLER_RECIPE_TYPE = TYPES.register(TumblerRecipe.ID, () -> TumblerRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<TurbineRecipe>> TURBINE_RECIPE_TYPE = TYPES.register(TurbineRecipe.ID, () -> TurbineRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<VulcanizerRecipe>> VULCANIZER_RECIPE_TYPE = TYPES.register(VulcanizerRecipe.ID, () -> VulcanizerRecipe.RECIPE_TYPE);
	public static final RegistryObject<RecipeType<AlloyFurnaceRecipe>> ALLOY_FURNACE_RECIPE_TYPE = TYPES.register(AlloyFurnaceRecipe.ID,
			() -> new StaticPowerRecipeType<AlloyFurnaceRecipe>());
	public static final RegistryObject<RecipeType<HydroponicFarmingRecipe>> HYDROPONIC_FARMING_RECIPE_TYPE = TYPES.register(HydroponicFarmingRecipe.ID,
			() -> HydroponicFarmingRecipe.RECIPE_TYPE);

	public static void init(IEventBus eventBus) {
		TYPES.register(eventBus);
	}
}
