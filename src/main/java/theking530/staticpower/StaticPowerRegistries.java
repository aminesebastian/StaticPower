package theking530.staticpower;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;

public class StaticPowerRegistries {

	public static final ResourceLocation CABLE_DESTINATION_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_destinations");
	public static final ResourceLocation CABLE_MODULE_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_modules");

	public static final Supplier<IForgeRegistry<CableDestination>> CABLE_DESTINATIONS = () -> RegistryManager.ACTIVE.getRegistry(CABLE_DESTINATION_REGISTRY);
	public static final Supplier<IForgeRegistry<CableNetworkModuleType>> CABLE_MODULES = () -> RegistryManager.ACTIVE.getRegistry(CABLE_MODULE_REGISTRY);

	public static final ForgeRegistry<CableDestination> CableDestinationRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY);
	}

	public static final ForgeRegistry<CableNetworkModuleType> CableModuleRegsitry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_MODULE_REGISTRY);
	}
}
