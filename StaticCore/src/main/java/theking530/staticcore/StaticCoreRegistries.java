package theking530.staticcore;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.rendering.ItemAttributeType;
import theking530.api.attributes.type.AttributeType;
import theking530.api.attributes.values.AttributeValueType;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.productivity.product.ProductType;

public class StaticCoreRegistries {

	public static final IForgeRegistry<UpgradeType<?>> UPGRADE_TYPES = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.UPGRADE_TYPES);

	public static final IForgeRegistry<CableDestination> CABLE_DESTINATION_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.CABLE_DESTINATION_REGISTRY);
	public static final IForgeRegistry<CableNetworkModuleType> CABLE_MODULE_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.CABLE_MODULE_REGISTRY);
	public static final IForgeRegistry<ServerCableCapabilityType<?>> CABLE_CAPABILITY_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.CABLE_CAPABILITY_REGISTRY);

	public static final IForgeRegistry<ProductType<?>> PRODUCT_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.PRODUCT_REGISTRY);

	public static final IForgeRegistry<AttributeType<?>> ATTRIBUTE_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.ATTRIBUTE_REGISTRY);
	public static final IForgeRegistry<AttributeModifierType<?>> ATTRIBUTE_MODIFIER_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.ATTRIBUTE_MODIFIER_REGISTRY);
	public static final IForgeRegistry<AttributeValueType<?>> ATTRIBUTE_VALUE_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.ATTRIBUTE_VALUE_REGISTRY);
	public static final IForgeRegistry<ItemAttributeType> ITEM_ATTRIBUTE_TYPE = RegistryManager.ACTIVE.getRegistry(StaticCoreRegistryKeys.ITEM_ATTRIBUTE_REGISTRY);

}
