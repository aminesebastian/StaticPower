package theking530.staticcore;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
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
	public static final ResourceKey<Registry<UpgradeType<?>>> UPGRADE_TYPES_REGISTRY_KEY = key("recipe_type");

	public static final ResourceKey<Registry<CableDestination>> CABLE_DESTINATION_REGISTRY_KEY = key("cable_destinations");
	public static final ResourceKey<Registry<CableNetworkModuleType>> CABLE_MODULE_REGISTRY_KEY = key("cable_modules");
	public static final ResourceKey<Registry<ServerCableCapabilityType<?>>> CABLE_CAPABILITY_REGISTRY_KEY = key("cable_capabilities");

	public static final ResourceKey<Registry<ProductType<?>>> PRODUCT_TYPE_REGISTRY_KEY = key("productivity_tracker");

	public static final ResourceKey<Registry<AttributeType<?>>> ATTRIBUTE_REGISTRY_KEY = key("attribute");
	public static final ResourceKey<Registry<AttributeModifierType<?>>> ATTRIBUTE_MODIFIER_REGISTRY_KEY = key("attribute_modifier");
	public static final ResourceKey<Registry<AttributeValueType<?>>> ATTRIBUTE_VALUE_REGISTRY_KEY = key("attribute_value");
	public static final ResourceKey<Registry<ItemAttributeType>> ITEM_ATTRIBUTE_REGISTRY_KEY = key("item_attribute_registry");

	public static final ForgeRegistry<UpgradeType<?>> UpgradeTypes() {
		return RegistryManager.ACTIVE.getRegistry(UPGRADE_TYPES_REGISTRY_KEY);
	}

	public static final ForgeRegistry<CableDestination> CableDestinationRegistry() {
		return RegistryManager.ACTIVE.getRegistry(CABLE_DESTINATION_REGISTRY_KEY);
	}

	public static final ForgeRegistry<CableNetworkModuleType> CableModuleRegsitry() {
		return RegistryManager.ACTIVE.getRegistry(CABLE_MODULE_REGISTRY_KEY);
	}

	public static final ForgeRegistry<ServerCableCapabilityType<?>> CableCapabilityRegistry() {
		return RegistryManager.ACTIVE.getRegistry(CABLE_CAPABILITY_REGISTRY_KEY);
	}

	public static final ForgeRegistry<ProductType<?>> ProductRegistry() {
		return RegistryManager.ACTIVE.getRegistry(PRODUCT_TYPE_REGISTRY_KEY);
	}

	public static final ForgeRegistry<AttributeType<?>> Attribute() {
		return RegistryManager.ACTIVE.getRegistry(ATTRIBUTE_REGISTRY_KEY);
	}

	public static final ForgeRegistry<AttributeModifierType<?>> AttributeModifier() {
		return RegistryManager.ACTIVE.getRegistry(ATTRIBUTE_MODIFIER_REGISTRY_KEY);
	}

	public static final ForgeRegistry<AttributeValueType<?>> AttributeValue() {
		return RegistryManager.ACTIVE.getRegistry(ATTRIBUTE_VALUE_REGISTRY_KEY);
	}

	public static final ForgeRegistry<ItemAttributeType> ItemAttribute() {
		return RegistryManager.ACTIVE.getRegistry(ITEM_ATTRIBUTE_REGISTRY_KEY);
	}

	private static <T> ResourceKey<Registry<T>> key(String name) {
		return ResourceKey.createRegistryKey(new ResourceLocation(StaticCore.MOD_ID, name));
	}
}
