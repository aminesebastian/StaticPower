package theking530.staticpower.initialization;

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
import theking530.staticpower.tileentities.nonpowered.tank.TileEntityTank;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.TileEntityVacuumChest;
import theking530.staticpower.tileentities.powered.basicfarmer.TileEntityBasicFarmer;
import theking530.staticpower.tileentities.powered.battery.TileEntityBattery;
import theking530.staticpower.tileentities.powered.chargingstation.TileEntityChargingStation;
import theking530.staticpower.tileentities.powered.fermenter.TileEntityFermenter;
import theking530.staticpower.tileentities.powered.former.TileEntityFormer;
import theking530.staticpower.tileentities.powered.lumbermill.TileEntityLumberMill;
import theking530.staticpower.tileentities.powered.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.tileentities.powered.pump.TileEntityPump;
import theking530.staticpower.tileentities.powered.solarpanels.TileEntitySolarPanel;
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

	public static TileEntityType<TileEntityPowerCable> POWER_CABLE;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE;
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
	public static TileEntityType<TileEntityBattery> BATTERY;
	
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
		BATTERY = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityBattery(), ModBlocks.BasicBattery);
		
		TANK = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityTank(), ModBlocks.BasicTank);
		PUMP = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPump(), ModBlocks.Pump);

		POWER_CABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityPowerCable(), ModBlocks.PowerCable);
		ITEM_CABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityItemCable(), ModBlocks.ItemCable);
		FLUID_CABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFluidCable(type, 2.0f / 16.0f, 100), ModBlocks.FluidCable);
		INDUSTRIAL_FLUID_CABLE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityFluidCable(type, 3.5f / 16.0f, 1000), ModBlocks.IndustrialFluidCable);

		DIGISTORE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistore(), ModBlocks.Digistore);
		DIGISTORE_MANAGER = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreManager(), ModBlocks.DigistoreManager);
		DIGISTORE_IO_PORT = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreIOPort(), ModBlocks.DigistoreIOPort);
		DIGISTORE_WIRE = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreWire(), ModBlocks.DigistoreWire);
		DIGISTORE_SERVER_RACK = StaticPowerRegistry.preRegisterTileEntity((type) -> new TileEntityDigistoreServerRack(), ModBlocks.DigistoreServerRack);
	}
}
