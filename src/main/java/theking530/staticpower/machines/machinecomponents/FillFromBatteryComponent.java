package theking530.staticpower.machines.machinecomponents;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.power.StaticEnergyStorage;
import theking530.staticpower.tileentity.BaseTileEntity;

public class FillFromBatteryComponent implements IMachineComponentInterface{

	private String NAME;
	
	private ItemStackHandler BATTERY_SLOT_HANDLER;
	private int BATTERY_SLOT;
	private BaseTileEntity TE;
	private StaticEnergyStorage ENERGY_STORAGE;
	private EnumFacing DIRECTION;
	
	public FillFromBatteryComponent(String componentName, ItemStackHandler batterySlotHandler, int batterySlot,
			BaseTileEntity tileEntity, StaticEnergyStorage energyHandler) {	
		NAME = componentName;
		BATTERY_SLOT_HANDLER = batterySlotHandler;
		BATTERY_SLOT = batterySlot;
		TE = tileEntity;
		ENERGY_STORAGE = energyHandler;
	}

	@Override
	public void update() {
		if(BATTERY_SLOT < BATTERY_SLOT_HANDLER.getSlots()) {
			if(ENERGY_STORAGE.getEnergyStored() < ENERGY_STORAGE.getMaxEnergyStored()) {
				if(BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT) != null && BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT).getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem batteryItem = (IEnergyContainerItem) BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT).getItem();
					if(batteryItem.getEnergyStored(BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT)) > 0) {
						int recieved = ENERGY_STORAGE.receiveEnergy(Math.min(batteryItem.getEnergyStored(BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT)), ENERGY_STORAGE.getMaxReceive()), false);
						batteryItem.extractEnergy(BATTERY_SLOT_HANDLER.getStackInSlot(BATTERY_SLOT), recieved, false);					
						TE.updateBlock();
					}
				}
			}
		}
	}

	@Override
	public String getComponentName() {
		return NAME;
	}

}