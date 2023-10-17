package theking530.staticpower.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.blastfurnace.BlastFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.blastfurnace.BlastFurnaceRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.bottler.BottlerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.carpenter.CarpenterRecipe;
import theking530.staticpower.data.crafting.wrappers.carpenter.CarpenterRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.cokeoven.CokeOvenRecipe;
import theking530.staticpower.data.crafting.wrappers.cokeoven.CokeOvenRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipe;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.covers.CoverRecipe;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipe;
import theking530.staticpower.data.crafting.wrappers.crucible.CrucibleRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.enchanter.EnchanterRecipe;
import theking530.staticpower.data.crafting.wrappers.enchanter.EnchanterRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipe;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipe;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipe;
import theking530.staticpower.data.crafting.wrappers.fertilization.FertalizerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipe;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipe;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipe;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipe;
import theking530.staticpower.data.crafting.wrappers.mixer.MixerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipe;
import theking530.staticpower.data.crafting.wrappers.refinery.RefineryRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipe;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipeSerializer;

public class ModRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, StaticPower.MOD_ID);

	public static final RegistryObject<AutoSmithRecipeSerializer> AUTO_SMITH_SERIALIZER = SERIALIZERS
			.register(AutoSmithRecipe.ID, () -> new AutoSmithRecipeSerializer());
	public static final RegistryObject<BottlerRecipeSerializer> BOTTLER_SERIALIZER = SERIALIZERS
			.register(BottleRecipe.ID, () -> new BottlerRecipeSerializer());
	public static final RegistryObject<CastingRecipeSerializer> CASTING_SERIALIZER = SERIALIZERS
			.register(CastingRecipe.ID, () -> new CastingRecipeSerializer());
	public static final RegistryObject<CauldronRecipeSerializer> CAULDRON_SERIALIZER = SERIALIZERS
			.register(CauldronRecipe.ID, () -> new CauldronRecipeSerializer());
	public static final RegistryObject<CentrifugeRecipeSerializer> CENTRIFUGE_SERIALIZER = SERIALIZERS
			.register(CentrifugeRecipe.ID, () -> new CentrifugeRecipeSerializer());
	public static final RegistryObject<CondensationRecipeSerializer> CONEDNSATION_SERIALIZER = SERIALIZERS
			.register(CondensationRecipe.ID, () -> new CondensationRecipeSerializer());
	public static final RegistryObject<SimpleRecipeSerializer<CoverRecipe>> COVER_SERIALIZER = SERIALIZERS
			.register(CoverRecipe.ID, () -> new SimpleRecipeSerializer<>(name -> new CoverRecipe(name)));
	public static final RegistryObject<CrucibleRecipeSerializer> CRUCIBLE_SERIALIZER = SERIALIZERS
			.register(CrucibleRecipe.ID, () -> new CrucibleRecipeSerializer());
	public static final RegistryObject<EnchanterRecipeSerializer> ENCHANTER_SERIALIZER = SERIALIZERS
			.register(EnchanterRecipe.ID, () -> new EnchanterRecipeSerializer());
	public static final RegistryObject<EvaporatorRecipeSerializer> EVAPORATOR_SERIALIZER = SERIALIZERS
			.register(EvaporatorRecipe.ID, () -> new EvaporatorRecipeSerializer());
	public static final RegistryObject<FermenterRecipeSerializer> FERMENTER_SERIALIZER = SERIALIZERS
			.register(FermenterRecipe.ID, () -> new FermenterRecipeSerializer());
	public static final RegistryObject<FertalizerRecipeSerializer> FERTILIZER_SERIALIZER = SERIALIZERS
			.register(FertalizerRecipe.ID, () -> new FertalizerRecipeSerializer());
	public static final RegistryObject<FluidGeneratorRecipeSerializer> FLUID_GENERATOR_SERIALIZER = SERIALIZERS
			.register(FluidGeneratorRecipe.ID, () -> new FluidGeneratorRecipeSerializer());
	public static final RegistryObject<FluidInfusionRecipeSerializer> FLUID_INFUSER_SERIALIZER = SERIALIZERS
			.register(FluidInfusionRecipe.ID, () -> new FluidInfusionRecipeSerializer());
	public static final RegistryObject<FormerRecipeSerializer> FORMER_SERIALIZER = SERIALIZERS.register(FormerRecipe.ID,
			() -> new FormerRecipeSerializer());
	public static final RegistryObject<FusionFurnaceRecipeSerializer> FUSION_FURANCE_SERIALIZER = SERIALIZERS
			.register(FusionFurnaceRecipe.ID, () -> new FusionFurnaceRecipeSerializer());
	public static final RegistryObject<GrinderRecipeSerializer> GRINDER_SERIALIZER = SERIALIZERS
			.register(GrinderRecipe.ID, () -> new GrinderRecipeSerializer());
	public static final RegistryObject<HammerRecipeSerializer> HAMMER_SERIALIZER = SERIALIZERS.register(HammerRecipe.ID,
			() -> new HammerRecipeSerializer());
	public static final RegistryObject<CarpenterRecipeSerializer> LATHE_SERIALIZER = SERIALIZERS
			.register(CarpenterRecipe.ID, () -> new CarpenterRecipeSerializer());
	public static final RegistryObject<LumberMillRecipeSerializer> LUMBER_MILL_SERIALIZER = SERIALIZERS
			.register(LumberMillRecipe.ID, () -> new LumberMillRecipeSerializer());
	public static final RegistryObject<MixerRecipeSerializer> MIXER_SERIALIZER = SERIALIZERS.register(MixerRecipe.ID,
			() -> new MixerRecipeSerializer());
	public static final RegistryObject<RefineryRecipeSerializer> REFINERY_SERIALIZER = SERIALIZERS
			.register(RefineryRecipe.ID, () -> new RefineryRecipeSerializer());

	public static final RegistryObject<SolderingRecipeSerializer> SOLDERING_SERIALIZER = SERIALIZERS
			.register(SolderingRecipe.ID, () -> new SolderingRecipeSerializer());
	public static final RegistryObject<SqueezerRecipeSerializer> SQUEEZER_SERIALIZER = SERIALIZERS
			.register(SqueezerRecipe.ID, () -> new SqueezerRecipeSerializer());

	public static final RegistryObject<TumblerRecipeSerializer> TUMBLER_SERIALIZER = SERIALIZERS
			.register(TumblerRecipe.ID, () -> new TumblerRecipeSerializer());
	public static final RegistryObject<TurbineRecipeSerializer> TURBINE_SERIALIZER = SERIALIZERS
			.register(TurbineRecipe.ID, () -> new TurbineRecipeSerializer());
	public static final RegistryObject<VulcanizerRecipeSerializer> VULCANIZER_SERIALIZER = SERIALIZERS
			.register(VulcanizerRecipe.ID, () -> new VulcanizerRecipeSerializer());
	public static final RegistryObject<AlloyFurnaceRecipeSerializer> ALLOY_FURNACE_SERIALIZER = SERIALIZERS
			.register(AlloyFurnaceRecipe.ID, () -> new AlloyFurnaceRecipeSerializer());
	public static final RegistryObject<HydroponicFarmingRecipeSerializer> HYDROPONIC_FARMER_SERIALIZER = SERIALIZERS
			.register(HydroponicFarmingRecipe.ID, () -> new HydroponicFarmingRecipeSerializer());
	public static final RegistryObject<BlastFurnaceRecipeSerializer> BLAST_FURNACE_SERIALIZER = SERIALIZERS
			.register(BlastFurnaceRecipe.ID, () -> new BlastFurnaceRecipeSerializer());
	public static final RegistryObject<CokeOvenRecipeSerializer> COKE_OVEN_SERIALIZER = SERIALIZERS
			.register(CokeOvenRecipe.ID, () -> new CokeOvenRecipeSerializer());

	public static void init(IEventBus eventBus) {
		SERIALIZERS.register(eventBus);
	}
}
