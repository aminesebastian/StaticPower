package theking530.staticpower.init.cables;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.cables.fluid.FluidCableCapability.FluidCableCapabilityType;

public class ModCableCapabilities {
	private static final DeferredRegister<ServerCableCapabilityType<?>> CAPABILITIES = DeferredRegister.create(StaticCoreRegistries.CABLE_CAPABILITY_REGISTRY_KEY, StaticPower.MOD_ID);

	public static final RegistryObject<FluidCableCapabilityType> Fluid = CAPABILITIES.register("fluid", () -> new FluidCableCapabilityType());

	public static void init(IEventBus eventBus) {
		CAPABILITIES.register(eventBus);
	}
}
