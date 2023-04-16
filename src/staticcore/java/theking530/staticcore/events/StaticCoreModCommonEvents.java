package theking530.staticcore.events;

import javax.annotation.Nonnull;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import theking530.api.attributes.ItemAttributeRegistry.ItemAttributeRegisterEvent;
import theking530.api.attributes.capability.IAttributable;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.rendering.ItemAttributeType;
import theking530.api.attributes.type.AttributeType;
import theking530.api.attributes.values.AttributeValueType;
import theking530.api.digistore.IDigistoreInventory;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.api.heat.IHeatStorage;
import theking530.api.item.compound.capability.ICompoundItem;
import theking530.api.item.compound.slot.CompoundItemSlot;
import theking530.api.upgrades.UpgradeType;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.data.StaticCoreGameDataManager;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.teams.TeamManager;

@Mod.EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticCoreModCommonEvents {

	@SubscribeEvent
	public static void commonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			StaticCoreGameDataManager.registerDataFactory(TeamManager.ID, (isClientSide) -> {
				return new TeamManager(isClientSide);
			});

			ItemAttributeRegisterEvent itemAttributeEvent = new ItemAttributeRegisterEvent();
			ModLoader.get().postEvent(itemAttributeEvent);

			StaticCore.LOGGER.info("Static Core Common Setup Completed!");
		});
	}

	@SubscribeEvent
	public static void registerCustomRegistries(@Nonnull NewRegistryEvent event) {
		event.create(new RegistryBuilder<UpgradeType<?>>().setName(StaticCoreRegistries.UPGRADE_TYPES_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));

		event.create(new RegistryBuilder<CableDestination>().setName(StaticCoreRegistries.CABLE_DESTINATION_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<CableNetworkModuleType>().setName(StaticCoreRegistries.CABLE_MODULE_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ServerCableCapabilityType<?>>().setName(StaticCoreRegistries.CABLE_CAPABILITY_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ProductType<?>>().setName(StaticCoreRegistries.PRODUCT_TYPE_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));

		event.create(new RegistryBuilder<AttributeType<?>>().setName(StaticCoreRegistries.ATTRIBUTE_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<AttributeModifierType<?>>().setName(StaticCoreRegistries.ATTRIBUTE_MODIFIER_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<AttributeValueType<?>>().setName(StaticCoreRegistries.ATTRIBUTE_VALUE_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ItemAttributeType>().setName(StaticCoreRegistries.ITEM_ATTRIBUTE_REGISTRY_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
		
		event.create(new RegistryBuilder<CompoundItemSlot>().setName(StaticCoreRegistries.ITEM_SLOT_KEY.location()).setIDRange(0, Integer.MAX_VALUE - 1));
	}

	@SubscribeEvent
	public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
		event.register(IDigistoreInventory.class);
		event.register(IHeatStorage.class);
		event.register(IAttributable.class);
		event.register(IStaticPowerStorage.class);
		event.register(IStaticPowerFluidHandler.class);
		event.register(ICompoundItem.class);
	}
}
