package theking530.staticcore.cablenetwork.destinations;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticcore.cablenetwork.destinations.types.CableDestinationCapability;
import theking530.staticcore.cablenetwork.destinations.types.CableDestinationDigistore;
import theking530.staticcore.cablenetwork.destinations.types.CableDestinationRedstone;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;

public class ModCableDestinations {
	private static final DeferredRegister<CableDestination> DESTINATIONS = DeferredRegister.create(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY, StaticPower.MOD_ID);

	public static final RegistryObject<CableDestinationCapability> Power = DESTINATIONS.register("power",
			() -> new CableDestinationCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY));
	public static final RegistryObject<CableDestinationCapability> Item = DESTINATIONS.register("item",
			() -> new CableDestinationCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY));
	public static final RegistryObject<CableDestinationCapability> Fluid = DESTINATIONS.register("fluid",
			() -> new CableDestinationCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY));
	public static final RegistryObject<CableDestinationCapability> Heat = DESTINATIONS.register("heat",
			() -> new CableDestinationCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY));
	public static final RegistryObject<CableDestinationRedstone> Redstone = DESTINATIONS.register("redstone", () -> new CableDestinationRedstone());
	public static final RegistryObject<CableDestinationDigistore> Digistore = DESTINATIONS.register("digistore", () -> new CableDestinationDigistore());

	public static void init(IEventBus eventBus) {
		DESTINATIONS.register(eventBus);
	}
}