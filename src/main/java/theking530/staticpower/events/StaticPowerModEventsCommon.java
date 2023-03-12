package theking530.staticpower.events;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import theking530.api.Events;
import theking530.api.attributes.capability.CapabilityAttributable;
import theking530.api.attributes.modifiers.AttributeModifierType;
import theking530.api.attributes.rendering.ItemAttributeType;
import theking530.api.attributes.type.AttributeType;
import theking530.api.attributes.values.AttributeValueType;
import theking530.api.digistore.CapabilityDigistoreInventory;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.fluid.CapabilityStaticFluid;
import theking530.api.heat.CapabilityHeatable;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.blockentities.machines.cropfarmer.BlockEntityBasicFarmer;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.CactusCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.GenericCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.NetherWartCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.StemCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.SugarCaneCropHarvester;
import theking530.staticpower.entities.player.datacapability.CapabilityStaticPowerPlayerData;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.teams.TeamManager;

@Mod.EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerModEventsCommon {

	public static final Logger LOGGER = LogManager.getLogger(StaticPowerModEventsCommon.class);

	@SubscribeEvent
	public static void commonSetupEvent(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			// Register farming harvesters.
			BlockEntityBasicFarmer.registerHarvester(new GenericCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new SugarCaneCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new CactusCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new NetherWartCropHarvester());
			BlockEntityBasicFarmer.registerHarvester(new StemCropHarvester());

			// Register composter recipes.
			event.enqueueWork(() -> {
				ComposterBlock.COMPOSTABLES.put(ModBlocks.RubberTreeLeaves.get().asItem(), 0.6f);
			});

			// Register data classes.
			StaticPowerGameDataManager.registerDataFactory(TeamManager.ID, (isClientSide) -> {
				return new TeamManager(isClientSide);
			});

			ModEntities.registerPlacements(event);
			Events.commonSetupEvent(event);

			LOGGER.info("Static Power Common Setup Completed!");
		});
	}

	@SubscribeEvent
	public static void registerCustomRegistries(@Nonnull NewRegistryEvent event) {
		event.create(new RegistryBuilder<CableDestination>().setName(StaticPowerRegistries.CABLE_DESTINATION_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<CableNetworkModuleType>().setName(StaticPowerRegistries.CABLE_MODULE_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ServerCableCapabilityType<?>>().setName(StaticPowerRegistries.CABLE_CAPABILITY_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ProductType<?>>().setName(StaticPowerRegistries.PRODUCT_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));

		event.create(new RegistryBuilder<AttributeType<?>>().setName(StaticPowerRegistries.ATTRIBUTE_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<AttributeModifierType<?>>().setName(StaticPowerRegistries.ATTRIBUTE_MODIFIER_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<AttributeValueType<?>>().setName(StaticPowerRegistries.ATTRIBUTE_VALUE_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
		event.create(new RegistryBuilder<ItemAttributeType>().setName(StaticPowerRegistries.ITEM_ATTRIBUTE_REGISTRY).setIDRange(0, Integer.MAX_VALUE - 1));
	}

	@SubscribeEvent
	public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
		// Register capabilities.
		CapabilityDigistoreInventory.register(event);
		CapabilityHeatable.register(event);
		CapabilityAttributable.register(event);
		CapabilityStaticPowerPlayerData.register(event);
		CapabilityStaticPower.register(event);
		CapabilityStaticFluid.register(event);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		ModEntities.registerEntityAttributes(event);
	}
}
