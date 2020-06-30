package theking530.staticpower.initialization;

import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.crafting.wrappers.fermenter.FermenterRecipeSerializer;
import theking530.staticpower.crafting.wrappers.former.FormerRecipeSerializer;
import theking530.staticpower.crafting.wrappers.grinder.GrinderRecipeSerializer;
import theking530.staticpower.crafting.wrappers.lumbermill.LumberMillRecipeSerializer;

public class ModRecipeSerializers {

	public static void init() {
		StaticPowerRegistry.preRegisterRecipeSerializer(GrinderRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(LumberMillRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FermenterRecipeSerializer.INSTANCE);
		StaticPowerRegistry.preRegisterRecipeSerializer(FormerRecipeSerializer.INSTANCE);
	}
}
