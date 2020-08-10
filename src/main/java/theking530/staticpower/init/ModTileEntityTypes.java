package theking530.staticpower.init;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.cables.digistore.TileEntityDigistoreWire;
import theking530.staticpower.cables.fluid.TileEntityFluidCable;
import theking530.staticpower.cables.item.TileEntityItemCable;
import theking530.staticpower.cables.power.TileEntityPowerCable;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport.TileEntityDigistoreIOPort;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack.TileEntityDigistoreServerRack;
import theking530.staticpower.tileentities.nonpowered.miner.TileEntityMiner;
import theking530.staticpower.tileentities.nonpowered.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.tileentities.nonpowered.tank.TileEntityTank;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.TileEntityVacuumChest;
import theking530.staticpower.tileentities.powered.autocrafter.TileEntityAutoCraftingTable;
import theking530.staticpower.tileentities.powered.autosolderingtable.TileEntityAutoSolderingTable;
import theking530.staticpower.tileentities.powered.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.tileentities.powered.battery.TileEntityBattery;
import theking530.staticpower.tileentities.powered.bottler.TileEntityBottler;
import theking530.staticpower.tileentities.powered.centrifuge.TileEntityCentrifuge;
import theking530.staticpower.tileentities.powered.chargingstation.TileEntityChargingStation;
import theking530.staticpower.tileentities.powered.crucible.TileEntityCrucible;
import theking530.staticpower.tileentities.powered.electricminer.TileEntityElectricMiner;
import theking530.staticpower.tileentities.powered.fermenter.TileEntityFermenter;
import theking530.staticpower.tileentities.powered.fluidgenerator.TileEntityFluidGenerator;
import theking530.staticpower.tileentities.powered.fluidinfuser.TileEntityFluidInfuser;
import theking530.staticpower.tileentities.powered.former.TileEntityFormer;
import theking530.staticpower.tileentities.powered.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.tileentities.powered.lumbermill.TileEntityLumberMill;
import theking530.staticpower.tileentities.powered.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.tileentities.powered.pump.TileEntityPump;
import theking530.staticpower.tileentities.powered.solarpanels.TileEntitySolarPanel;
import theking530.staticpower.tileentities.powered.solidgenerator.TileEntitySolidGenerator;
import theking530.staticpower.tileentities.powered.squeezer.TileEntitySqueezer;
import theking530.staticpower.tileentities.powered.treefarmer.TileEntityTreeFarm;

public class ModTileEntityTypes {
	public static TileEntityType<TileEntityVacuumChest> VACCUM_CHEST;
	public static TileEntityType<TileEntityChargingStation> CHARGING_STATION;
	public static TileEntityType<TileEntitySolarPanel> SOLAR_PANEL_BASIC;
	public static TileEntityType<TileEntitySolarPanel> SOLAR_PANEL_STATIC;
	public static TileEntityType<TileEntitySolarPanel> SOLAR_PANEL_ENERGIZED;
	public static TileEntityType<TileEntitySolarPanel> SOLAR_PANEL_LUMUM;
	public static TileEntityType<TileEntitySolarPanel> SOLAR_PANEL_CREATIVE;

	public static TileEntityType<TileEntityPoweredFurnace> POWERED_FURNACE;
	public static TileEntityType<TileEntityPoweredGrinder> POWERED_GRINDER;
	public static TileEntityType<TileEntityTank> TANK;
	public static TileEntityType<TileEntityPump> PUMP;

	public static TileEntityType<TileEntityPowerCable> POWER_CABLE_BASIC;
	public static TileEntityType<TileEntityPowerCable> POWER_CABLE_ADVANCED;
	public static TileEntityType<TileEntityPowerCable> POWER_CABLE_STATIC;
	public static TileEntityType<TileEntityPowerCable> POWER_CABLE_ENERGIZED;
	public static TileEntityType<TileEntityPowerCable> POWER_CABLE_LUMUM;
	public static TileEntityType<TileEntityPowerCable> POWER_CABLE_CREATIVE;

	public static TileEntityType<TileEntityItemCable> ITEM_CABLE_BASIC;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE_ADVANCED;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE_STATIC;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE_ENERGIZED;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE_LUMUM;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE_CREATIVE;

	public static TileEntityType<TileEntityFluidCable> FLUID_CABLE;
	public static TileEntityType<TileEntityFluidCable> INDUSTRIAL_FLUID_CABLE;

	public static TileEntityType<TileEntityDigistore> DIGISTORE;
	public static TileEntityType<TileEntityDigistoreManager> DIGISTORE_MANAGER;
	public static TileEntityType<TileEntityDigistoreServerRack> DIGISTORE_SERVER_RACK;
	public static TileEntityType<TileEntityDigistoreIOPort> DIGISTORE_IO_PORT;
	public static TileEntityType<TileEntityDigistoreWire> DIGISTORE_WIRE;
	public static TileEntityType<TileEntityLumberMill> LUMBER_MILL;
	public static TileEntityType<TileEntityBasicFarmer> BASIC_FARMER;
	public static TileEntityType<TileEntityTreeFarm> TREE_FARM;
	public static TileEntityType<TileEntityFermenter> FERMENTER;
	public static TileEntityType<TileEntityFormer> FORMER;
	public static TileEntityType<TileEntitySolidGenerator> SOLID_GENERATOR;
	public static TileEntityType<TileEntityFluidGenerator> FLUID_GENERATOR;
	public static TileEntityType<TileEntityCrucible> CRUCIBLE;
	public static TileEntityType<TileEntitySqueezer> SQUEEZER;
	public static TileEntityType<TileEntityBottler> BOTTLER;
	public static TileEntityType<TileEntitySolderingTable> SOLDERING_TABLE;
	public static TileEntityType<TileEntityAutoSolderingTable> AUTO_SOLDERING_TABLE;
	public static TileEntityType<TileEntityAutoCraftingTable> AUTO_CRAFTING_TABLE;
	public static TileEntityType<TileEntityFluidInfuser> FLUID_INFUSER;
	public static TileEntityType<TileEntityCentrifuge> CENTRIFUGE;
	public static TileEntityType<TileEntityFusionFurnace> FUSION_FURNACE;
	public static TileEntityType<TileEntityMiner> MINER;
	public static TileEntityType<TileEntityElectricMiner> ELECTRIC_MINER;
	
	public static TileEntityType<TileEntityBattery> BATTERY_BASIC;
	public static TileEntityType<TileEntityBattery> BATTERY_ADVANCED;
	public static TileEntityType<TileEntityBattery> BATTERY_STATIC;
	public static TileEntityType<TileEntityBattery> BATTERY_ENERGIZED;
	public static TileEntityType<TileEntityBattery> BATTERY_LUMUM;
	public static TileEntityType<TileEntityBattery> BATTERY_CREATIVE;

	public static void init() {
		VACCUM_CHEST = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityVacuumChest(), ModBlocks.VacuumChest);
		CHARGING_STATION = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityChargingStation(), ModBlocks.ChargingStation);

		SOLAR_PANEL_BASIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolarPanel(StaticPowerTiers.BASIC, type), ModBlocks.SolarPanelBasic);
		SOLAR_PANEL_STATIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolarPanel(StaticPowerTiers.STATIC, type), ModBlocks.SolarPanelStatic);
		SOLAR_PANEL_ENERGIZED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolarPanel(StaticPowerTiers.ENERGIZED, type), ModBlocks.SolarPanelEnergized);
		SOLAR_PANEL_LUMUM = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolarPanel(StaticPowerTiers.LUMUM, type), ModBlocks.SolarPanelLumum);
		SOLAR_PANEL_CREATIVE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolarPanel(StaticPowerTiers.CREATIVE, type), ModBlocks.SolarPanelCreative);

		POWERED_FURNACE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPoweredFurnace(), ModBlocks.PoweredFurnace);
		POWERED_GRINDER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPoweredGrinder(), ModBlocks.PoweredGrinder);
		LUMBER_MILL = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityLumberMill(), ModBlocks.LumberMill);
		BASIC_FARMER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBasicFarmer(), ModBlocks.BasicFarmer);
		TREE_FARM = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityTreeFarm(), ModBlocks.TreeFarmer);
		FERMENTER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFermenter(), ModBlocks.Fermenter);
		FORMER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFormer(), ModBlocks.Former);
		SOLID_GENERATOR = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolidGenerator(), ModBlocks.SolidGenerator);
		FLUID_GENERATOR = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFluidGenerator(), ModBlocks.FluidGenerator);
		CRUCIBLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityCrucible(), ModBlocks.Crucible);
		SQUEEZER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySqueezer(), ModBlocks.Squeezer);
		BOTTLER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBottler(), ModBlocks.Bottler);
		SOLDERING_TABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntitySolderingTable(type), ModBlocks.SolderingTable);
		AUTO_SOLDERING_TABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityAutoSolderingTable(), ModBlocks.AutoSolderingTable);
		AUTO_CRAFTING_TABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityAutoCraftingTable(), ModBlocks.AutoCraftingTable);
		FLUID_INFUSER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFluidInfuser(), ModBlocks.FluidInfuser);
		CENTRIFUGE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityCentrifuge(), ModBlocks.Centrifuge);
		FUSION_FURNACE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFusionFurnace(), ModBlocks.FusionFurnace);
		MINER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityMiner(), ModBlocks.Miner);
		ELECTRIC_MINER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityElectricMiner(), ModBlocks.ElectricMiner);

		BATTERY_BASIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(type, StaticPowerTiers.BASIC), ModBlocks.BatteryBasic);
		BATTERY_ADVANCED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(type, StaticPowerTiers.ADVANCED), ModBlocks.BatteryAdvanced);
		BATTERY_STATIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(type, StaticPowerTiers.STATIC), ModBlocks.BatteryStatic);
		BATTERY_ENERGIZED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(type, StaticPowerTiers.ENERGIZED), ModBlocks.BatteryEnergized);
		BATTERY_LUMUM = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(type, StaticPowerTiers.LUMUM), ModBlocks.BatteryLumum);
		BATTERY_CREATIVE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(type, StaticPowerTiers.CREATIVE), ModBlocks.BatteryCreative);

		TANK = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityTank(), ModBlocks.BasicTank);
		PUMP = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPump(), ModBlocks.Pump);

		POWER_CABLE_BASIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(type, StaticPowerTiers.BASIC), ModBlocks.PowerCableBasic);
		POWER_CABLE_ADVANCED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(type, StaticPowerTiers.ADVANCED), ModBlocks.PowerCableAdvanced);
		POWER_CABLE_STATIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(type, StaticPowerTiers.STATIC), ModBlocks.PowerCableStatic);
		POWER_CABLE_ENERGIZED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(type, StaticPowerTiers.ENERGIZED), ModBlocks.PowerCableEnergized);
		POWER_CABLE_LUMUM = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(type, StaticPowerTiers.LUMUM), ModBlocks.PowerCableLumum);
		POWER_CABLE_CREATIVE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(type, StaticPowerTiers.CREATIVE), ModBlocks.PowerCableCreative);

		ITEM_CABLE_BASIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(type, StaticPowerTiers.BASIC), ModBlocks.ItemCableBasic);
		ITEM_CABLE_ADVANCED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(type, StaticPowerTiers.ADVANCED), ModBlocks.ItemCableAdvanced);
		ITEM_CABLE_STATIC = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(type, StaticPowerTiers.STATIC), ModBlocks.ItemCableStatic);
		ITEM_CABLE_ENERGIZED = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(type, StaticPowerTiers.ENERGIZED), ModBlocks.ItemCableEnergized);
		ITEM_CABLE_LUMUM = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(type, StaticPowerTiers.LUMUM), ModBlocks.ItemCableLumum);
		ITEM_CABLE_CREATIVE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(type, StaticPowerTiers.CREATIVE), ModBlocks.ItemCableCreative);

		FLUID_CABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFluidCable(type, 2.0f / 16.0f, 100), ModBlocks.FluidCable);
		INDUSTRIAL_FLUID_CABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFluidCable(type, 3.5f / 16.0f, 1000), ModBlocks.IndustrialFluidCable);

		DIGISTORE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistore(), ModBlocks.Digistore);
		DIGISTORE_MANAGER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreManager(), ModBlocks.DigistoreManager);
		DIGISTORE_IO_PORT = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreIOPort(), ModBlocks.DigistoreIOPort);
		DIGISTORE_WIRE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreWire(), ModBlocks.DigistoreWire);
		DIGISTORE_SERVER_RACK = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreServerRack(), ModBlocks.DigistoreServerRack);
	}
}
