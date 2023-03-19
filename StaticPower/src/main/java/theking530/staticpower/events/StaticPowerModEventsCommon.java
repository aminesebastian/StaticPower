package theking530.staticpower.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import theking530.api.attributes.ItemAttributeRegistry.ItemAttributeRegisterEvent;
import theking530.api.attributes.rendering.AttributeRenderLayer;
import theking530.staticcore.attribiutes.Attributes;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.machines.cropfarmer.BlockEntityBasicFarmer;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.CactusCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.GenericCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.NetherWartCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.StemCropHarvester;
import theking530.staticpower.blockentities.machines.cropfarmer.harvesters.SugarCaneCropHarvester;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.entities.player.datacapability.IStaticPowerPlayerData;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModEntities;
import theking530.staticpower.init.ModItems;

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

			ModEntities.registerPlacements(event);

			LOGGER.info("Static Power Common Setup Completed!");
		});
	}

	@SubscribeEvent
	public static void registerItemAttributes(ItemAttributeRegisterEvent event) {
		event.attach(ModItems.AdvancedDrillBit.get(), Attributes.DiamondHardened.get(), false, new AttributeRenderLayer(StaticPowerAdditionalModels.DRILL_BIT_HARDENED_DIAMOND, 2));
	}

	@SubscribeEvent
	public static void capabilityRegisterEvent(RegisterCapabilitiesEvent event) {
		event.register(IStaticPowerPlayerData.class);
	}

	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		ModEntities.registerEntityAttributes(event);
	}
}
