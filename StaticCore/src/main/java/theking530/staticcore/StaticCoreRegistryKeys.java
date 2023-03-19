package theking530.staticcore;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.rendering.ItemAttributeType;
import theking530.api.attributes.type.AttributeType;
import theking530.api.attributes.values.AttributeValueType;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.productivity.product.ProductType;

public class StaticCoreRegistryKeys {
	public static final ResourceKey<Registry<UpgradeType<?>>> UPGRADE_TYPES = key("recipe_type");

	public static final ResourceKey<Registry<CableDestination>> CABLE_DESTINATION_REGISTRY = key("cable_destinations");
	public static final ResourceKey<Registry<CableNetworkModuleType>> CABLE_MODULE_REGISTRY = key("cable_modules");
	public static final ResourceKey<Registry<ServerCableCapabilityType<?>>> CABLE_CAPABILITY_REGISTRY = key("cable_capabilities");

	public static final ResourceKey<Registry<ProductType<?>>> PRODUCT_REGISTRY = key("productivity_tracker");

	public static final ResourceKey<Registry<AttributeType<?>>> ATTRIBUTE_REGISTRY = key("attribute");
	public static final ResourceKey<Registry<AttributeModifierType<?>>> ATTRIBUTE_MODIFIER_REGISTRY = key("attribute_modifier");
	public static final ResourceKey<Registry<AttributeValueType<?>>> ATTRIBUTE_VALUE_REGISTRY = key("attribute_value");
	public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_REGISTRY = key("item_attribute_registry");

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(new ResourceLocation(StaticCore.MOD_ID, name));
	}
}
