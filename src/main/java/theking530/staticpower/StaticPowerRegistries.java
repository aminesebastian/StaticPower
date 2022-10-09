package theking530.staticpower;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.productivity.product.ProductType;

public class StaticPowerRegistries {

	public static final ResourceLocation CABLE_DESTINATION_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_destinations");
	public static final ResourceLocation CABLE_MODULE_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_modules");
	public static final ResourceLocation CABLE_CAPABILITY_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_capabilities");
	public static final ResourceLocation PRODUCT_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "productivity_tracker");

	public static final ForgeRegistry<CableDestination> CableDestinationRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY);
	}

	public static final ForgeRegistry<CableNetworkModuleType> CableModuleRegsitry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_MODULE_REGISTRY);
	}

	public static final ForgeRegistry<ServerCableCapabilityType> CableCapabilityRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_CAPABILITY_REGISTRY);
	}

	public static final ForgeRegistry<ProductType<?>> ProductRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.PRODUCT_REGISTRY);
	}
}
