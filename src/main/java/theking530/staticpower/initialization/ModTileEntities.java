package theking530.staticpower.initialization;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
import theking530.staticpower.conduits.fluidconduit.TileEntityFluidConduit;
import theking530.staticpower.conduits.itemconduit.TileEntityItemConduit;
import theking530.staticpower.conduits.staticconduit.TileEntityStaticConduit;
import theking530.staticpower.logicgates.adder.TileEntityAdder;
import theking530.staticpower.logicgates.and.TileEntityAndGate;
import theking530.staticpower.logicgates.led.TileEntityLED;
import theking530.staticpower.logicgates.notgate.TileEntityNotGate;
import theking530.staticpower.logicgates.or.TileEntityOrGate;
import theking530.staticpower.logicgates.powercell.TileEntityPowerCell;
import theking530.staticpower.logicgates.subtractor.TileEntitySubtractorGate;
import theking530.staticpower.logicgates.timer.TileEntityTimer;
import theking530.staticpower.logicgates.transducer.TileEntitySignalMultiplier;
import theking530.staticpower.machines.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBasicBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityEnergizedBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityLumumBattery;
import theking530.staticpower.machines.batteries.tileentities.TileEntityStaticBattery;
import theking530.staticpower.machines.centrifuge.TileEntityCentrifuge;
import theking530.staticpower.machines.chargingstation.TileEntityChargingStation;
import theking530.staticpower.machines.condenser.TileEntityCondenser;
import theking530.staticpower.machines.cropsqueezer.TileEntityCropSqueezer;
import theking530.staticpower.machines.distillery.TileEntityDistillery;
import theking530.staticpower.machines.esotericenchanter.TileEsotericEnchanter;
import theking530.staticpower.machines.fermenter.TileEntityFermenter;
import theking530.staticpower.machines.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.machines.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.machines.former.TileEntityFormer;
import theking530.staticpower.machines.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.machines.heatingelement.TileEntityHeatingElement;
import theking530.staticpower.machines.lumbermill.TileLumberMill;
import theking530.staticpower.machines.mechanicalsqueezer.TileEntityMechanicalSqueezer;
import theking530.staticpower.machines.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.quarry.TileEntityQuarry;
import theking530.staticpower.machines.refinery.controller.TileEntityFluidRefineryController;
import theking530.staticpower.machines.refinery.fluidinterface.TileEntityRefineryFluidInterface;
import theking530.staticpower.machines.refinery.mixer.TileEntityRefineryMixer;
import theking530.staticpower.machines.refinery.reactor.TileEntityRefineryReactor;
import theking530.staticpower.machines.refinery.vent.TileEntityRefineryVent;
import theking530.staticpower.machines.treefarmer.TileEntityTreeFarm;
import theking530.staticpower.tileentity.astralquary.brain.TileEntityAstralQuarryBrain;
import theking530.staticpower.tileentity.chest.energizedchest.TileEntityEnergizedChest;
import theking530.staticpower.tileentity.chest.lumumchest.TileEntityLumumChest;
import theking530.staticpower.tileentity.chest.staticchest.TileEntityStaticChest;
import theking530.staticpower.tileentity.chunkloader.TileEntityChunkLoader;
import theking530.staticpower.tileentity.solarpanels.TileEntityBasicSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityCreativeSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityEnergizedSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityLumumSolarPanel;
import theking530.staticpower.tileentity.solarpanels.TileEntityStaticSolarPanel;
import theking530.staticpower.tileentity.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.tileentity.vacuumchest.TileEntityVacuumChest;

public class ModTileEntities {
	public static void init() {
		registerTileEntity(TileEntityChunkLoader.class, "BaseChunkLoader");
		
		registerTileEntity(TileEntitySolderingTable.class, "SolderingTable");	
		registerTileEntity(TileEntityFluidInfuser.class, "FluidInfuser");
		registerTileEntity(TileEntityFluidGenerator.class, "FluidGenerator");
		registerTileEntity(TileEntityCropSqueezer.class, "CropSqueezer");
		registerTileEntity(TileEntityPoweredGrinder.class, "PoweredGrinder");
		registerTileEntity(TileEntityPoweredFurnace.class, "PoweredSmelter");
		registerTileEntity(TileEntityStaticConduit.class, "StaticConduit");
		registerTileEntity(TileEntityFluidConduit.class, "FluidConduit");
		registerTileEntity(TileEntityItemConduit.class, "ItemConduit");
		registerTileEntity(TileEntityFusionFurnace.class, "FusionFurnace");
		registerTileEntity(TileEntityQuarry.class, "Quarry");
		registerTileEntity(TileEntityBasicFarmer.class, "BasicFarmer");
		registerTileEntity(TileEntityChargingStation.class, "ChargingStation");
		registerTileEntity(TileEntityMechanicalSqueezer.class, "MechanicalSqueezer");
		registerTileEntity(TileEntityFermenter.class, "Fermenter");
		registerTileEntity(TileEntityHeatingElement.class, "HeatingElement");
		registerTileEntity(TileEntityDistillery.class, "Distillery");
		registerTileEntity(TileEntityCondenser.class, "Condenser");
		registerTileEntity(TileEntityFormer.class, "Former");
		registerTileEntity(TileEntityTreeFarm.class, "TreeFarmer");
		registerTileEntity(TileLumberMill.class, "LumberMill");
		
		registerTileEntity(TileEntityBasicSolarPanel.class, "BaseSolarPanel");
		registerTileEntity(TileEntityStaticSolarPanel.class, "StaticSolarPanel");
		registerTileEntity(TileEntityEnergizedSolarPanel.class, "EnergizedSolarPanel");
		registerTileEntity(TileEntityLumumSolarPanel.class, "LumumSolarPanel");
		registerTileEntity(TileEntityCreativeSolarPanel.class, "CreativeSolarPanel");	
		
		registerTileEntity(TileEntityBasicBattery.class, "BasicBattery");
		registerTileEntity(TileEntityStaticBattery.class, "StaticBattery");
		registerTileEntity(TileEntityEnergizedBattery.class, "EnergizedBattery");
		registerTileEntity(TileEntityLumumBattery.class, "LumumBattery");

		registerTileEntity(TileEntitySignalMultiplier.class, "SignalMultiplier");
		registerTileEntity(TileEntityNotGate.class, "NotGate");
		registerTileEntity(TileEntityPowerCell.class, "PowerCell");
		registerTileEntity(TileEntityTimer.class, "Timer");
		registerTileEntity(TileEntityAdder.class, "Adder");
		registerTileEntity(TileEntityAndGate.class, "And");
		registerTileEntity(TileEntityOrGate.class, "Or");
		registerTileEntity(TileEntitySubtractorGate.class, "Subtractor");
		registerTileEntity(TileEntityLED.class, "LED");
		
		registerTileEntity(TileEntityStaticChest.class, "StaticChest");
		registerTileEntity(TileEntityEnergizedChest.class, "EnergizedChest");
		registerTileEntity(TileEntityLumumChest.class, "LumumChest");
		registerTileEntity(TileEntityVacuumChest.class, "VacuumChest");
		
		registerTileEntity(TileEsotericEnchanter.class, "Esoteric Enchanter");
		registerTileEntity(TileEntityCentrifuge.class, "Centrifuge");
		
		registerTileEntity(TileEntityAstralQuarryBrain.class, "Astral Quarry");
		
		registerTileEntity(TileEntityFluidRefineryController.class, "Fluid Refinery Controller");
		registerTileEntity(TileEntityRefineryFluidInterface.class, "Fluid Refinery Interface");
		
		registerTileEntity(TileEntityRefineryReactor.class, "Fluid Refinery Reactor");
		registerTileEntity(TileEntityRefineryMixer.class, "Fluid Refinery Mixer");
		registerTileEntity(TileEntityRefineryVent.class, "Fluid Refinery Vent");
	}
	private static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String key) {
		// As return is ignored for compatibility, always check namespace
		ResourceLocation teResource = new ResourceLocation(key);
		GameData.checkPrefix(teResource.toString(), true);
		GameRegistry.registerTileEntity(tileEntityClass, teResource);
	}
}
