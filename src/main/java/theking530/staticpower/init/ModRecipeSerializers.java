package theking530.staticpower.init;

import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.data.crafting.wrappers.bottler.BottlerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.fermenter.FermenterRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.grinder.GrinderRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.lumbermill.LumberMillRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.squeezer.SqueezerRecipeSerializer;

public class ModRecipeSerializers {

	public static void init() {
		StaticPowerRegistry.preRegisterRecipeSerializer(GrinderRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(LumberMillRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FermenterRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FormerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(SqueezerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(BottlerRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(SolderingRecipeSerializer.INSTANCE);
	}
}
