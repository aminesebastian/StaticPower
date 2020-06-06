package theking530.staticpower.initialization;

import net.minecraft.inventory.container.ContainerType;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.items.itemfilter.ContainerItemFilter;
import theking530.staticpower.items.itemfilter.GuiItemFilter;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.ContainerDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.GuiDigistore;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.ContainerVacuumChest;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.GuiVacuumChest;
import theking530.staticpower.tileentities.powered.chargingstation.ContainerChargingStation;
import theking530.staticpower.tileentities.powered.chargingstation.GuiChargingStation;
import theking530.staticpower.tileentities.powered.poweredfurnace.ContainerPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredfurnace.GuiPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.ContainerPoweredGrinder;
import theking530.staticpower.tileentities.powered.poweredgrinder.GuiPoweredGrinder;

public class ModContainerTypes {
	public static ContainerType<ContainerVacuumChest> VACUUM_CHEST_CONTAINER;
	public static ContainerType<ContainerChargingStation> CHARGING_STATION_CONTAINER;
	public static ContainerType<ContainerItemFilter> ITEM_FILTER_CONTAINER;
	public static ContainerType<ContainerPoweredFurnace> POWERED_FURNACE_CONTAINER;
	public static ContainerType<ContainerPoweredGrinder> POWERED_GRINDER_CONTAINER;
	public static ContainerType<ContainerDigistore> DIGISTORE_CONTAINER;

	public static void init() {
		VACUUM_CHEST_CONTAINER = StaticPowerRegistry.preRegisterContainer("chest_vacuum", ContainerVacuumChest::new, GuiVacuumChest::new);
		CHARGING_STATION_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_charging_station", ContainerChargingStation::new, GuiChargingStation::new);
		ITEM_FILTER_CONTAINER = StaticPowerRegistry.preRegisterContainer("filter_item", ContainerItemFilter::new, GuiItemFilter::new);
		POWERED_FURNACE_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_powered_furnace", ContainerPoweredFurnace::new, GuiPoweredFurnace::new);
		POWERED_GRINDER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_powered_grinder", ContainerPoweredGrinder::new, GuiPoweredGrinder::new);
		DIGISTORE_CONTAINER = StaticPowerRegistry.preRegisterContainer("digistore", ContainerDigistore::new, GuiDigistore::new);
	}
}
