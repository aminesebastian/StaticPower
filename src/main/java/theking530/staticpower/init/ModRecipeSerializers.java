package theking530.staticpower.init;

import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipe;
import theking530.staticpower.data.crafting.wrappers.autosmith.AutoSmithRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.bottler.BottleRecipe;
import theking530.staticpower.data.crafting.wrappers.bottler.BottlerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipe;
import theking530.staticpower.data.crafting.wrappers.castingbasin.CastingRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipe;
import theking530.staticpower.data.crafting.wrappers.cauldron.CauldronRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipeSerializer;
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
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipe;
import theking530.staticpower.data.crafting.wrappers.lathe.LatheRecipeSerializer;
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
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipe;
import theking530.staticpower.data.crafting.wrappers.turbine.TurbineRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipe;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipeSerializer;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.ResearchSerializer;

public class ModRecipeSerializers {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, StaticPower.MOD_ID);

	public static final RegistryObject<AutoSmithRecipeSerializer> AUTO_SMITH_SERIALIZER = SERIALIZERS.register(AutoSmithRecipe.ID, () -> AutoSmithRecipeSerializer.INSTANCE);
	public static final RegistryObject<BottlerRecipeSerializer> BOTTLER_SERIALIZER = SERIALIZERS.register(BottleRecipe.ID, () -> BottlerRecipeSerializer.INSTANCE);
	public static final RegistryObject<CastingRecipeSerializer> CASTING_SERIALIZER = SERIALIZERS.register(CastingRecipe.ID, () -> CastingRecipeSerializer.INSTANCE);
	public static final RegistryObject<CauldronRecipeSerializer> CAULDRON_SERIALIZER = SERIALIZERS.register(CauldronRecipe.ID, () -> CauldronRecipeSerializer.INSTANCE);
	public static final RegistryObject<CentrifugeRecipeSerializer> CENTRIFUGE_SERIALIZER = SERIALIZERS.register(CentrifugeRecipe.ID, () -> CentrifugeRecipeSerializer.INSTANCE);
	public static final RegistryObject<CondensationRecipeSerializer> CONEDNSATION_SERIALIZER = SERIALIZERS.register(CondensationRecipe.ID,
			() -> CondensationRecipeSerializer.INSTANCE);
	public static final RegistryObject<SimpleRecipeSerializer<CoverRecipe>> COVER_SERIALIZER = SERIALIZERS.register(CoverRecipe.ID, () -> CoverRecipe.SERIALIZER_INSTANCE);
	public static final RegistryObject<CrucibleRecipeSerializer> CRUCIBLE_SERIALIZER = SERIALIZERS.register(CrucibleRecipe.ID, () -> CrucibleRecipeSerializer.INSTANCE);
	public static final RegistryObject<EnchanterRecipeSerializer> ENCHANTER_SERIALIZER = SERIALIZERS.register(EnchanterRecipe.ID, () -> EnchanterRecipeSerializer.INSTANCE);
	public static final RegistryObject<EvaporatorRecipeSerializer> EVAPORATOR_SERIALIZER = SERIALIZERS.register(EvaporatorRecipe.ID, () -> EvaporatorRecipeSerializer.INSTANCE);
	public static final RegistryObject<FermenterRecipeSerializer> FERMENTER_SERIALIZER = SERIALIZERS.register(FermenterRecipe.ID, () -> FermenterRecipeSerializer.INSTANCE);
	public static final RegistryObject<FertalizerRecipeSerializer> FERTILIZER_SERIALIZER = SERIALIZERS.register(FertalizerRecipe.ID, () -> FertalizerRecipeSerializer.INSTANCE);
	public static final RegistryObject<FluidGeneratorRecipeSerializer> FLUID_GENERATOR_SERIALIZER = SERIALIZERS.register(FluidGeneratorRecipe.ID,
			() -> FluidGeneratorRecipeSerializer.INSTANCE);
	public static final RegistryObject<FluidInfusionRecipeSerializer> FLUID_INFUSER_SERIALIZER = SERIALIZERS.register(FluidInfusionRecipe.ID,
			() -> FluidInfusionRecipeSerializer.INSTANCE);
	public static final RegistryObject<FormerRecipeSerializer> FORMER_SERIALIZER = SERIALIZERS.register(FormerRecipe.ID, () -> FormerRecipeSerializer.INSTANCE);
	public static final RegistryObject<FusionFurnaceRecipeSerializer> FUSION_FURANCE_SERIALIZER = SERIALIZERS.register(FusionFurnaceRecipe.ID,
			() -> FusionFurnaceRecipeSerializer.INSTANCE);
	public static final RegistryObject<GrinderRecipeSerializer> GRINDER_SERIALIZER = SERIALIZERS.register(GrinderRecipe.ID, () -> GrinderRecipeSerializer.INSTANCE);
	public static final RegistryObject<HammerRecipeSerializer> HAMMER_SERIALIZER = SERIALIZERS.register(HammerRecipe.ID, () -> HammerRecipeSerializer.INSTANCE);
	public static final RegistryObject<LatheRecipeSerializer> LATHE_SERIALIZER = SERIALIZERS.register(LatheRecipe.ID, () -> LatheRecipeSerializer.INSTANCE);
	public static final RegistryObject<LumberMillRecipeSerializer> LUMBER_MILL_SERIALIZER = SERIALIZERS.register(LumberMillRecipe.ID, () -> LumberMillRecipeSerializer.INSTANCE);
	public static final RegistryObject<MixerRecipeSerializer> MIXER_SERIALIZER = SERIALIZERS.register(MixerRecipe.ID, () -> MixerRecipeSerializer.INSTANCE);
	public static final RegistryObject<RefineryRecipeSerializer> REFINERY_SERIALIZER = SERIALIZERS.register(RefineryRecipe.ID, () -> RefineryRecipeSerializer.INSTANCE);
	public static final RegistryObject<ResearchSerializer> RESEARCH_SERIALIZER = SERIALIZERS.register(Research.ID, () -> ResearchSerializer.INSTANCE);
	public static final RegistryObject<SolderingRecipeSerializer> SOLDERING_SERIALIZER = SERIALIZERS.register(SolderingRecipe.ID, () -> SolderingRecipeSerializer.INSTANCE);
	public static final RegistryObject<SqueezerRecipeSerializer> SQUEEZER_SERIALIZER = SERIALIZERS.register(SqueezerRecipe.ID, () -> SqueezerRecipeSerializer.INSTANCE);
	public static final RegistryObject<ThermalConductivityRecipeSerializer> THERMAL_CONDUCTIVITY_SERIALIZER = SERIALIZERS.register(ThermalConductivityRecipe.ID,
			() -> ThermalConductivityRecipeSerializer.INSTANCE);
	public static final RegistryObject<TumblerRecipeSerializer> TUMBLER_SERIALIZER = SERIALIZERS.register(TumblerRecipe.ID, () -> TumblerRecipeSerializer.INSTANCE);
	public static final RegistryObject<TurbineRecipeSerializer> TURBINE_SERIALIZER = SERIALIZERS.register(TurbineRecipe.ID, () -> TurbineRecipeSerializer.INSTANCE);
	public static final RegistryObject<VulcanizerRecipeSerializer> VULCANIZER_SERIALIZER = SERIALIZERS.register(VulcanizerRecipe.ID, () -> VulcanizerRecipeSerializer.INSTANCE);

	public static void init(IEventBus eventBus) {
		SERIALIZERS.register(eventBus);
	}

	public static void onRegisterRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
		Registry.register(Registry.RECIPE_TYPE, AutoSmithRecipe.ID, AutoSmithRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, BottleRecipe.ID, BottleRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, CastingRecipe.ID, CastingRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, CauldronRecipe.ID, CauldronRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, CentrifugeRecipe.ID, CentrifugeRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, CondensationRecipe.ID, CondensationRecipe.RECIPE_TYPE);
//		Registry.register(Registry.RECIPE_TYPE, CoverRecipe.ID, CoverRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, CrucibleRecipe.ID, CrucibleRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, EnchanterRecipe.ID, EnchanterRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, EvaporatorRecipe.ID, EvaporatorRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, FermenterRecipe.ID, FermenterRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, FertalizerRecipe.ID, FertalizerRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, FluidGeneratorRecipe.ID, FluidGeneratorRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, FluidInfusionRecipe.ID, FluidInfusionRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, FormerRecipe.ID, FormerRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, FusionFurnaceRecipe.ID, FusionFurnaceRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, GrinderRecipe.ID, GrinderRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, HammerRecipe.ID, HammerRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, LatheRecipe.ID, LatheRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, LumberMillRecipe.ID, LumberMillRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, MixerRecipe.ID, MixerRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, RefineryRecipe.ID, RefineryRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, Research.ID, Research.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, SolderingRecipe.ID, SolderingRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, SqueezerRecipe.ID, SqueezerRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, ThermalConductivityRecipe.ID, ThermalConductivityRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, TumblerRecipe.ID, TumblerRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, TurbineRecipe.ID, TurbineRecipe.RECIPE_TYPE);
		Registry.register(Registry.RECIPE_TYPE, VulcanizerRecipe.ID, VulcanizerRecipe.RECIPE_TYPE);
	}
}
