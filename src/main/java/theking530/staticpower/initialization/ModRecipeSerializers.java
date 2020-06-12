package theking530.staticpower.initialization;

import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.crafting.wrappers.grinder.GrinderRecipeSerializer;

public class ModRecipeSerializers {

	public static void init() {
		StaticPowerRegistry.preRegisterRecipeSerializer(GrinderRecipeSerializer.INSTANCE);
	}
}
