package theking530.staticpower.initialization;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.Registry;
import theking530.staticpower.machines.chargingstation.TileEntityChargingStation;
import theking530.staticpower.tileentity.solarpanels.TileEntityBasicSolarPanel;
import theking530.staticpower.tileentity.vacuumchest.TileEntityVacuumChest;

public class ModTileEntityTypes {
	public static TileEntityType<TileEntityVacuumChest> VACCUM_CHEST;
	public static TileEntityType<TileEntityChargingStation> CHARGING_STATION;
	public static TileEntityType<TileEntityBasicSolarPanel> SOLAR_PANEL_BASIC;
	
	public static void init() {
		VACCUM_CHEST = Registry.preRegisterTileEntity(TileEntityVacuumChest::new, ModBlocks.VacuumChest);
		CHARGING_STATION = Registry.preRegisterTileEntity(TileEntityChargingStation::new, ModBlocks.ChargingStation);
		SOLAR_PANEL_BASIC = Registry.preRegisterTileEntity(TileEntityBasicSolarPanel::new, ModBlocks.SolarPanelBasic);
	}
}
