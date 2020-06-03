package theking530.staticpower.initialization;

import net.minecraft.tileentity.TileEntityType;
import theking530.staticpower.StaticPowerRegistry;
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
	
	public static void init() {
		VACCUM_CHEST = StaticPowerRegistry.preRegisterTileEntity(TileEntityVacuumChest::new, ModBlocks.VacuumChest);
		CHARGING_STATION = StaticPowerRegistry.preRegisterTileEntity(TileEntityChargingStation::new, ModBlocks.ChargingStation);
		SOLAR_PANEL_BASIC = StaticPowerRegistry.preRegisterTileEntity(TileEntityBasicSolarPanel::new, ModBlocks.SolarPanelBasic);
		POWERED_FURNACE = StaticPowerRegistry.preRegisterTileEntity(TileEntityPoweredFurnace::new, ModBlocks.PoweredFurnace);
		POWERED_GRINDER = StaticPowerRegistry.preRegisterTileEntity(TileEntityPoweredGrinder::new, ModBlocks.PoweredGrinder);
	}
}
