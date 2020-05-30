package theking530.staticpower.initialization;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DeferredWorkQueue;
import theking530.staticpower.Registry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.items.itemfilter.ContainerItemFilter;
import theking530.staticpower.items.itemfilter.GuiItemFilter;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.ContainerVacuumChest;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.GuiVacuumChest;
import theking530.staticpower.tileentities.powered.chargingstation.ContainerChargingStation;
import theking530.staticpower.tileentities.powered.chargingstation.GuiChargingStation;
import theking530.staticpower.tileentities.powered.poweredfurnace.ContainerPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredfurnace.GuiPoweredFurnace;

@SuppressWarnings("deprecation")
public class ModContainerTypes {
	public static ContainerType<ContainerVacuumChest> VACUUM_CHEST_CONTAINER;
	public static ContainerType<ContainerChargingStation> CHARGING_STATION_CONTAINER;
	public static ContainerType<ContainerItemFilter> ITEM_FILTER_CONTAINER;
	public static ContainerType<ContainerPoweredFurnace> MACHINE_POWERED_FURNACE_CONTAINER;

	public static void init() {
		VACUUM_CHEST_CONTAINER = Registry.preRegisterContainer("chest_vacuum", ContainerVacuumChest::new);
		CHARGING_STATION_CONTAINER = Registry.preRegisterContainer("machine_charging_station", ContainerChargingStation::new);
		ITEM_FILTER_CONTAINER = Registry.preRegisterContainer("filter_item", ContainerItemFilter::new);
		MACHINE_POWERED_FURNACE_CONTAINER = Registry.preRegisterContainer("machine_powered_furnace", ContainerPoweredFurnace::new);
	}

	@OnlyIn(Dist.CLIENT)
	public static void initializeGui() {
		DeferredWorkQueue.runLater(() -> {
			ScreenManager.registerFactory(VACUUM_CHEST_CONTAINER, GuiVacuumChest::new);
			ScreenManager.registerFactory(CHARGING_STATION_CONTAINER, GuiChargingStation::new);
			ScreenManager.registerFactory(ITEM_FILTER_CONTAINER, GuiItemFilter::new);
			ScreenManager.registerFactory(MACHINE_POWERED_FURNACE_CONTAINER, GuiPoweredFurnace::new);
			StaticPower.LOGGER.debug("Registered all Static Power container types.");
		});
	}
}
