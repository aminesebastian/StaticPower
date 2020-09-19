package theking530.staticpower.init;

import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottlerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.condensation.CondensationRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.covers.CoverRecipe;
import theking530.staticpower.data.crafting.wrappers.evaporation.EvaporatorRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.farmer.FarmingFertalizerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fluidgenerator.FluidGeneratorRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fluidinfusion.FluidInfusionRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fusionfurnace.FusionFurnaceRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.vulcanizer.VulcanizerRecipeSerializer;

public class ModRecipeSerializers {

	public static void init() {
		StaticPowerRegistry.preRegisterRecipeSerializer(GrinderRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(LumberMillRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FermenterRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FormerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(SqueezerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(BottlerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(SolderingRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FluidInfusionRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(CoverRecipe.SERIALIZER_INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FarmingFertalizerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(CentrifugeRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FusionFurnaceRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FluidGeneratorRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(ThermalConductivityRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(EvaporatorRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(CondensationRecipeSerializer.INSTANCE);		
		StaticPowerRegistry.preRegisterRecipeSerializer(VulcanizerRecipeSerializer.INSTANCE);		
	}
}
