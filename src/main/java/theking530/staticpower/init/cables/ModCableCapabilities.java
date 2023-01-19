package theking530.staticpower.init.cables;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.cables.fluid.FluidCableCapability.FluidCableCapabilityType;

public class ModCableCapabilities {
	private static final DeferredRegister<FluidCableCapabilityType> CAPABILITIES = DeferredRegister.create(StaticPowerRegistries.CABLE_CAPABILITY_REGISTRY, StaticPower.MOD_ID);

	public static final RegistryObject<FluidCableCapabilityType> Fluid = CAPABILITIES.register("fluid", () -> new FluidCableCapabilityType());

	public static void init(IEventBus eventBus) {
		CAPABILITIES.register(eventBus);
	}
}