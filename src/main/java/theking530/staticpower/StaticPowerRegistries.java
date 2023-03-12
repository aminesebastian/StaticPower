package theking530.staticpower;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.type.AttributeType;
import theking530.api.attributes.values.AttributeValueType;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.productivity.product.ProductType;

public class StaticPowerRegistries {

	public static final ResourceLocation CABLE_DESTINATION_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_destinations");
	public static final ResourceLocation CABLE_MODULE_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_modules");
	public static final ResourceLocation CABLE_CAPABILITY_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "cable_capabilities");
	public static final ResourceLocation PRODUCT_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "productivity_tracker");

	public static final ResourceLocation ATTRIBUTE_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "attribute");
	public static final ResourceLocation ATTRIBUTE_MODIFIER_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "attribute_modifier");
	public static final ResourceLocation ATTRIBUTE_VALUE_REGISTRY = new ResourceLocation(StaticPower.MOD_ID, "attribute_value");

	public static final ForgeRegistry<CableDestination> CableDestinationRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY);
	}

	public static final ForgeRegistry<CableNetworkModuleType> CableModuleRegsitry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_MODULE_REGISTRY);
	}

	public static final ForgeRegistry<ServerCableCapabilityType<?>> CableCapabilityRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.CABLE_CAPABILITY_REGISTRY);
	}

	public static final ForgeRegistry<ProductType<?>> ProductRegistry() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.PRODUCT_REGISTRY);
	}

	public static final ForgeRegistry<AttributeType<?>> Attribute() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.ATTRIBUTE_REGISTRY);
	}

	public static final ForgeRegistry<AttributeModifierType<?>> AttributeModifier() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.ATTRIBUTE_MODIFIER_REGISTRY);
	}

	public static final ForgeRegistry<AttributeValueType<?>> AttributeValue() {
		return RegistryManager.ACTIVE.getRegistry(StaticPowerRegistries.ATTRIBUTE_VALUE_REGISTRY);
	}
}
