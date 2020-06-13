package theking530.staticpower.initialization;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.cables.item.TileEntityItemCable;
import theking530.staticpower.cables.power.TileEntityPowerCable;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.TileEntityDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.ioport.TileEntityDigistoreIOPort;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager.TileEntityDigistoreManager;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.networkwire.TileEntityDigistoreWire;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.TileEntityVacuumChest;
import theking530.staticpower.tileentities.powered.chargingstation.TileEntityChargingStation;
import theking530.staticpower.tileentities.powered.poweredfurnace.TileEntityPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.tileentities.powered.solarpanels.TileEntityBasicSolarPanel;

public class ModTileEntityTypes {
	public static TileEntityType<TileEntityVacuumChest> VACCUM_CHEST;
	public static TileEntityType<TileEntityChargingStation> CHARGING_STATION;
	public static TileEntityType<TileEntityBasicSolarPanel> SOLAR_PANEL_BASIC;
	public static TileEntityType<TileEntityPoweredFurnace> POWERED_FURNACE;
	public static TileEntityType<TileEntityPoweredGrinder> POWERED_GRINDER;

	public static TileEntityType<TileEntityPowerCable> POWER_CABLE;
	public static TileEntityType<TileEntityItemCable> ITEM_CABLE;

	public static TileEntityType<TileEntityDigistore> DIGISTORE;
	public static TileEntityType<TileEntityDigistoreManager> DIGISTORE_MANAGER;
	public static TileEntityType<TileEntityDigistoreIOPort> DIGISTORE_IO_PORT;
	public static TileEntityType<TileEntityDigistoreWire> DIGISTORE_WIRE;

	public static void init() {
		VACCUM_CHEST = StaticPowerRegistry.preRegisterTileEntity(TileEntityVacuumChest::new, ModBlocks.VacuumChest);
		CHARGING_STATION = StaticPowerRegistry.preRegisterTileEntity(TileEntityChargingStation::new, ModBlocks.ChargingStation);
		SOLAR_PANEL_BASIC = StaticPowerRegistry.preRegisterTileEntity(TileEntityBasicSolarPanel::new, ModBlocks.SolarPanelBasic);
		POWERED_FURNACE = StaticPowerRegistry.preRegisterTileEntity(TileEntityPoweredFurnace::new, ModBlocks.PoweredFurnace);
		POWERED_GRINDER = StaticPowerRegistry.preRegisterTileEntity(TileEntityPoweredGrinder::new, ModBlocks.PoweredGrinder);

		POWER_CABLE = StaticPowerRegistry.preRegisterTileEntity(TileEntityPowerCable::new, ModBlocks.PowerCable);
		ITEM_CABLE = StaticPowerRegistry.preRegisterTileEntity(TileEntityItemCable::new, ModBlocks.ItemCable);

		DIGISTORE = StaticPowerRegistry.preRegisterTileEntity(TileEntityDigistore::new, ModBlocks.Digistore);
		DIGISTORE_MANAGER = StaticPowerRegistry.preRegisterTileEntity(TileEntityDigistoreManager::new, ModBlocks.DigistoreManager);
		DIGISTORE_IO_PORT = StaticPowerRegistry.preRegisterTileEntity(TileEntityDigistoreIOPort::new, ModBlocks.DigistoreIOPort);
		DIGISTORE_WIRE = StaticPowerRegistry.preRegisterTileEntity(TileEntityDigistoreWire::new, ModBlocks.DigistoreWire);
	}
}
