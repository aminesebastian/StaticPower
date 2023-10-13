package theking530.staticpower.init;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.crafting.StaticPowerRecipeType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.data.crafting.wrappers.blastfurnace.BlastFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.carpenter.CarpenterRecipe;
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
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;

public class ModRecipeTypes {
	public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,
			StaticPower.MOD_ID);
	public static final RegistryObject<RecipeType<AutoSmithRecipe>> AUTO_SMITH_RECIPE_TYPE = TYPES
			.register(AutoSmithRecipe.ID, () -> new StaticPowerRecipeType<AutoSmithRecipe>());
	public static final RegistryObject<RecipeType<BottleRecipe>> BOTTLER_RECIPE_TYPE = TYPES.register(BottleRecipe.ID,
			() -> new StaticPowerRecipeType<BottleRecipe>());
	public static final RegistryObject<RecipeType<CastingRecipe>> CASTING_RECIPE_TYPE = TYPES.register(CastingRecipe.ID,
			() -> new StaticPowerRecipeType<CastingRecipe>());
	public static final RegistryObject<RecipeType<CauldronRecipe>> CAULDRON_RECIPE_TYPE = TYPES
			.register(CauldronRecipe.ID, () -> new StaticPowerRecipeType<CauldronRecipe>());
	public static final RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE_TYPE = TYPES
			.register(CentrifugeRecipe.ID, () -> new StaticPowerRecipeType<CentrifugeRecipe>());
	public static final RegistryObject<RecipeType<CondensationRecipe>> CONDENSATION_RECIPE_TYPE = TYPES
			.register(CondensationRecipe.ID, () -> new StaticPowerRecipeType<CondensationRecipe>());
	public static final RegistryObject<RecipeType<CoverRecipe>> COVER_RECIPE_TYPE = TYPES.register(CoverRecipe.ID,
			() -> new StaticPowerRecipeType<CoverRecipe>());
	public static final RegistryObject<RecipeType<CrucibleRecipe>> CRUCIBLE_RECIPE_TYPE = TYPES
			.register(CrucibleRecipe.ID, () -> new StaticPowerRecipeType<CrucibleRecipe>());
	public static final RegistryObject<RecipeType<EnchanterRecipe>> ENCHANTER_RECIPE_TYPE = TYPES
			.register(EnchanterRecipe.ID, () -> new StaticPowerRecipeType<EnchanterRecipe>());
	public static final RegistryObject<RecipeType<EvaporatorRecipe>> EVAPORATOR_RECIPE_TYPE = TYPES
			.register(EvaporatorRecipe.ID, () -> new StaticPowerRecipeType<EvaporatorRecipe>());
	public static final RegistryObject<RecipeType<FermenterRecipe>> FERMENTER_RECIPE_TYPE = TYPES
			.register(FermenterRecipe.ID, () -> new StaticPowerRecipeType<FermenterRecipe>());
	public static final RegistryObject<RecipeType<FertalizerRecipe>> FERTALIZER_RECIPE_TYPE = TYPES
			.register(FertalizerRecipe.ID, () -> new StaticPowerRecipeType<FertalizerRecipe>());
	public static final RegistryObject<RecipeType<FluidGeneratorRecipe>> FLUID_GENERATOR_RECIPE_TYPE = TYPES
			.register(FluidGeneratorRecipe.ID, () -> new StaticPowerRecipeType<FluidGeneratorRecipe>());
	public static final RegistryObject<RecipeType<FluidInfusionRecipe>> FLUID_INFUSION_RECIPE_TYPE = TYPES
			.register(FluidInfusionRecipe.ID, () -> new StaticPowerRecipeType<FluidInfusionRecipe>());
	public static final RegistryObject<RecipeType<FormerRecipe>> FORMER_RECIPE_TYPE = TYPES.register(FormerRecipe.ID,
			() -> new StaticPowerRecipeType<FormerRecipe>());
	public static final RegistryObject<RecipeType<FusionFurnaceRecipe>> FUSION_FURNACE_RECIPE_TYPE = TYPES
			.register(FusionFurnaceRecipe.ID, () -> new StaticPowerRecipeType<FusionFurnaceRecipe>());
	public static final RegistryObject<RecipeType<GrinderRecipe>> GRINDER_RECIPE_TYPE = TYPES.register(GrinderRecipe.ID,
			() -> new StaticPowerRecipeType<GrinderRecipe>());
	public static final RegistryObject<RecipeType<HammerRecipe>> HAMMER_RECIPE_TYPE = TYPES.register(HammerRecipe.ID,
			() -> new StaticPowerRecipeType<HammerRecipe>());
	public static final RegistryObject<RecipeType<CarpenterRecipe>> LATHE_RECIPE_TYPE = TYPES
			.register(CarpenterRecipe.ID, () -> new StaticPowerRecipeType<CarpenterRecipe>());
	public static final RegistryObject<RecipeType<LumberMillRecipe>> LUMBER_MILL_RECIPE_TYPE = TYPES
			.register(LumberMillRecipe.ID, () -> new StaticPowerRecipeType<LumberMillRecipe>());
	public static final RegistryObject<RecipeType<MixerRecipe>> MIXER_RECIPE_TYPE = TYPES.register(MixerRecipe.ID,
			() -> new StaticPowerRecipeType<MixerRecipe>());
	public static final RegistryObject<RecipeType<RefineryRecipe>> REFINERY_RECIPE_TYPE = TYPES
			.register(RefineryRecipe.ID, () -> new StaticPowerRecipeType<RefineryRecipe>());

	public static final RegistryObject<RecipeType<SolderingRecipe>> SOLDERING_RECIPE_TYPE = TYPES
			.register(SolderingRecipe.ID, () -> new StaticPowerRecipeType<SolderingRecipe>());
	public static final RegistryObject<RecipeType<SqueezerRecipe>> SQUEEZER_RECIPE_TYPE = TYPES
			.register(SqueezerRecipe.ID, () -> new StaticPowerRecipeType<SqueezerRecipe>());
	public static final RegistryObject<RecipeType<TumblerRecipe>> TUMBLER_RECIPE_TYPE = TYPES.register(TumblerRecipe.ID,
			() -> new StaticPowerRecipeType<TumblerRecipe>());
	public static final RegistryObject<RecipeType<TurbineRecipe>> TURBINE_RECIPE_TYPE = TYPES.register(TurbineRecipe.ID,
			() -> new StaticPowerRecipeType<TurbineRecipe>());
	public static final RegistryObject<RecipeType<VulcanizerRecipe>> VULCANIZER_RECIPE_TYPE = TYPES
			.register(VulcanizerRecipe.ID, () -> new StaticPowerRecipeType<VulcanizerRecipe>());
	public static final RegistryObject<RecipeType<AlloyFurnaceRecipe>> ALLOY_FURNACE_RECIPE_TYPE = TYPES
			.register(AlloyFurnaceRecipe.ID, () -> new StaticPowerRecipeType<AlloyFurnaceRecipe>());
	public static final RegistryObject<RecipeType<HydroponicFarmingRecipe>> HYDROPONIC_FARMING_RECIPE_TYPE = TYPES
			.register(HydroponicFarmingRecipe.ID, () -> new StaticPowerRecipeType<HydroponicFarmingRecipe>());
	public static final RegistryObject<RecipeType<BlastFurnaceRecipe>> BLAST_FURNACE_RECIPE_TYPE = TYPES
			.register(BlastFurnaceRecipe.ID, () -> new StaticPowerRecipeType<BlastFurnaceRecipe>());

	public static void init(IEventBus eventBus) {
		TYPES.register(eventBus);
	}
}
