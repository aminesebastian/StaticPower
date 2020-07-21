package theking530.staticpower.initialization;

import net.minecraft.inventory.container.ContainerType;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.items.cableattachments.digistorecraftingterminal.ContainerDigistoreCraftingTerminal;
import theking530.staticpower.items.cableattachments.digistorecraftingterminal.GuiDigistoreCraftingTerminal;
import theking530.staticpower.items.cableattachments.digistoreterminal.ContainerDigistoreTerminal;
import theking530.staticpower.items.cableattachments.digistoreterminal.GuiDigistoreTerminal;
import theking530.staticpower.items.cableattachments.extractor.ContainerExtractor;
import theking530.staticpower.items.cableattachments.extractor.GuiExtractor;
import theking530.staticpower.items.cableattachments.filter.ContainerFilter;
import theking530.staticpower.items.cableattachments.filter.GuiFilter;
import theking530.staticpower.items.cableattachments.retirever.ContainerRetriever;
import theking530.staticpower.items.cableattachments.retirever.GuiRetriever;
import theking530.staticpower.items.itemfilter.ContainerItemFilter;
import theking530.staticpower.items.itemfilter.GuiItemFilter;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.ContainerDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore.GuiDigistore;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack.ContainerDigistoreServerRack;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.severrack.GuiDigistoreServerRack;
import theking530.staticpower.tileentities.nonpowered.tank.ContainerTank;
import theking530.staticpower.tileentities.nonpowered.tank.GuiTank;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.ContainerVacuumChest;
import theking530.staticpower.tileentities.nonpowered.vacuumchest.GuiVacuumChest;
import theking530.staticpower.tileentities.powered.basicfarmer.ContainerBasicFarmer;
import theking530.staticpower.tileentities.powered.basicfarmer.GuiBasicFarmer;
import theking530.staticpower.tileentities.powered.battery.ContainerBattery;
import theking530.staticpower.tileentities.powered.battery.GuiBattery;
import theking530.staticpower.tileentities.powered.chargingstation.ContainerChargingStation;
import theking530.staticpower.tileentities.powered.chargingstation.GuiChargingStation;
import theking530.staticpower.tileentities.powered.crucible.ContainerCrucible;
import theking530.staticpower.tileentities.powered.crucible.GuiCrucible;
import theking530.staticpower.tileentities.powered.fermenter.ContainerFermenter;
import theking530.staticpower.tileentities.powered.fermenter.GuiFermenter;
import theking530.staticpower.tileentities.powered.former.ContainerFormer;
import theking530.staticpower.tileentities.powered.former.GuiFormer;
import theking530.staticpower.tileentities.powered.lumbermill.ContainerLumberMill;
import theking530.staticpower.tileentities.powered.lumbermill.GuiLumberMill;
import theking530.staticpower.tileentities.powered.poweredfurnace.ContainerPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredfurnace.GuiPoweredFurnace;
import theking530.staticpower.tileentities.powered.poweredgrinder.ContainerPoweredGrinder;
import theking530.staticpower.tileentities.powered.poweredgrinder.GuiPoweredGrinder;
import theking530.staticpower.tileentities.powered.pump.ContainerPump;
import theking530.staticpower.tileentities.powered.pump.GuiPump;
import theking530.staticpower.tileentities.powered.solidgenerator.ContainerSolidGenerator;
import theking530.staticpower.tileentities.powered.solidgenerator.GuiSolidGenerator;
import theking530.staticpower.tileentities.powered.squeezer.ContainerSqueezer;
import theking530.staticpower.tileentities.powered.squeezer.GuiSqueezer;
import theking530.staticpower.tileentities.powered.treefarmer.ContainerTreeFarmer;
import theking530.staticpower.tileentities.powered.treefarmer.GuiTreeFarmer;

public class ModContainerTypes {
	public static ContainerType<ContainerVacuumChest> VACUUM_CHEST_CONTAINER;
	public static ContainerType<ContainerChargingStation> CHARGING_STATION_CONTAINER;
	public static ContainerType<ContainerItemFilter> ITEM_FILTER_CONTAINER;
	public static ContainerType<ContainerPoweredFurnace> POWERED_FURNACE_CONTAINER;
	public static ContainerType<ContainerPoweredGrinder> POWERED_GRINDER_CONTAINER;
	public static ContainerType<ContainerFermenter> FERMENTER_CONTAINER;
	public static ContainerType<ContainerLumberMill> LUMBER_MILL_CONTAINER;
	public static ContainerType<ContainerBasicFarmer> BASIC_FARMER_CONTAINER;
	public static ContainerType<ContainerTreeFarmer> TREE_FARMER_CONTAINER;
	public static ContainerType<ContainerFormer> FORMER_CONTAINER;
	public static ContainerType<ContainerBattery> BATTERY_CONTAINER;
	public static ContainerType<ContainerSolidGenerator> SOLID_GENERATOR_CONTAINER;
	public static ContainerType<ContainerCrucible> CRUCIBLE_CONTAINER;
	public static ContainerType<ContainerSqueezer> SQUEEZER_CONTAINER;
	
	public static ContainerType<ContainerTank> TANK_CONTAINER;
	public static ContainerType<ContainerPump> PUMP_CONTAINER;

	public static ContainerType<ContainerDigistore> DIGISTORE_CONTAINER;
	public static ContainerType<ContainerDigistoreTerminal> DIGISTORE_TERMINAL;
	public static ContainerType<ContainerDigistoreCraftingTerminal> DIGISTORE_CRAFTING_TERMINAL;
	public static ContainerType<ContainerDigistoreServerRack> DIGISTORE_SERVER_RACK;

	public static ContainerType<ContainerExtractor> EXTRACTOR_CONTAINER;
	public static ContainerType<ContainerFilter> FILTER_CONTAINER;
	public static ContainerType<ContainerRetriever> RETRIEVER_CONTAINER;

	public static void init() {
		VACUUM_CHEST_CONTAINER = StaticPowerRegistry.preRegisterContainer("chest_vacuum", ContainerVacuumChest::new, GuiVacuumChest::new);
		CHARGING_STATION_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_charging_station", ContainerChargingStation::new, GuiChargingStation::new);
		ITEM_FILTER_CONTAINER = StaticPowerRegistry.preRegisterContainer("filter_item", ContainerItemFilter::new, GuiItemFilter::new);
		POWERED_FURNACE_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_powered_furnace", ContainerPoweredFurnace::new, GuiPoweredFurnace::new);
		POWERED_GRINDER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_powered_grinder", ContainerPoweredGrinder::new, GuiPoweredGrinder::new);
		LUMBER_MILL_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_lumber_mill", ContainerLumberMill::new, GuiLumberMill::new);
		BASIC_FARMER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_basic_farmer", ContainerBasicFarmer::new, GuiBasicFarmer::new);
		TREE_FARMER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_tree_farmer", ContainerTreeFarmer::new, GuiTreeFarmer::new);
		FERMENTER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_fermenter", ContainerFermenter::new, GuiFermenter::new);
		FORMER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_former", ContainerFormer::new, GuiFormer::new);
		BATTERY_CONTAINER = StaticPowerRegistry.preRegisterContainer("battery", ContainerBattery::new, GuiBattery::new);
		SOLID_GENERATOR_CONTAINER = StaticPowerRegistry.preRegisterContainer("solid_generator", ContainerSolidGenerator::new, GuiSolidGenerator::new);
		CRUCIBLE_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_crucible", ContainerCrucible::new, GuiCrucible::new);
		SQUEEZER_CONTAINER = StaticPowerRegistry.preRegisterContainer("machine_squeezer", ContainerSqueezer::new, GuiSqueezer::new);
		
		TANK_CONTAINER = StaticPowerRegistry.preRegisterContainer("tank", ContainerTank::new, GuiTank::new);
		PUMP_CONTAINER = StaticPowerRegistry.preRegisterContainer("pump", ContainerPump::new, GuiPump::new);

		DIGISTORE_CONTAINER = StaticPowerRegistry.preRegisterContainer("digistore", ContainerDigistore::new, GuiDigistore::new);
		DIGISTORE_TERMINAL = StaticPowerRegistry.preRegisterContainer("digistore_terminal", ContainerDigistoreTerminal::new, GuiDigistoreTerminal::new);
		DIGISTORE_CRAFTING_TERMINAL = StaticPowerRegistry.preRegisterContainer("digistore_crafting_terminal", ContainerDigistoreCraftingTerminal::new, GuiDigistoreCraftingTerminal::new);
		DIGISTORE_SERVER_RACK = StaticPowerRegistry.preRegisterContainer("digistore_server_rack", ContainerDigistoreServerRack::new, GuiDigistoreServerRack::new);
		EXTRACTOR_CONTAINER = StaticPowerRegistry.preRegisterContainer("cable_attachment_extractor", ContainerExtractor::new, GuiExtractor::new);
		FILTER_CONTAINER = StaticPowerRegistry.preRegisterContainer("cable_attachment_filter", ContainerFilter::new, GuiFilter::new);
		RETRIEVER_CONTAINER = StaticPowerRegistry.preRegisterContainer("cable_attachment_retriever", ContainerRetriever::new, GuiRetriever::new);
	}
}
