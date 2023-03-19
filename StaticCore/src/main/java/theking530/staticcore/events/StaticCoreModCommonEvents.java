package theking530.staticcore.events;

import javax.annotation.Nonnull;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.rendering.ItemAttributeType;
import theking530.api.attributes.type.AttributeType;
import theking530.api.attributes.values.AttributeValueType;
import theking530.api.digistore.IDigistoreInventory;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistryKeys;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.productivity.product.ProductType;

@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticCoreModCommonEvents {
	@SubscribeEvent
	public static void registerCustomRegistries(@Nonnull NewRegistryEvent event) {
		event.create(new RegistryBuilder<CableDestination>().setName(StaticCoreRegistryKeys.UPGRADE_TYPES.location()).setIDRange(0, Integer.MAX_VALUE - 1));

		event.create(new RegistryBuilder<CableDestination>().setName(StaticCoreRegistryKeys.CABLE_DESTINATION_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<CableNetworkModuleType>().setName(StaticCoreRegistryKeys.CABLE_MODULE_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ServerCableCapabilityType<?>>().setName(StaticCoreRegistryKeys.CABLE_CAPABILITY_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ProductType<?>>().setName(StaticCoreRegistryKeys.PRODUCT_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));

		event.create(new RegistryBuilder<AttributeType<?>>().setName(StaticCoreRegistryKeys.ATTRIBUTE_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<AttributeModifierType<?>>().setName(StaticCoreRegistryKeys.ATTRIBUTE_MODIFIER_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<AttributeValueType<?>>().setName(StaticCoreRegistryKeys.ATTRIBUTE_VALUE_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ItemAttributeType>().setName(StaticCoreRegistryKeys.ITEM_ATTRIBUTE_REGISTRY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
	}

	@SubscribeEvent
	public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
		event.register(IDigistoreInventory.class);
		event.register(IHeatStorage.class);
		event.register(IAttributable.class);
		event.register(IStaticPowerStorage.class);
		event.register(IStaticPowerFluidHandler.class);
	}
}
