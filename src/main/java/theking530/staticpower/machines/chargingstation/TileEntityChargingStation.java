package theking530.staticpower.machines.chargingstation;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityChargingStation extends BaseMachine {
	
	public TileEntityChargingStation() {
		initializeBasicMachine(2, 0, 100000, 500, 2, 0, 6, 4);
		setBatterySlot(4);
		STORAGE.setMaxExtract(512);
	}
	@Override
	public void process(){
		if(STORAGE.getEnergyStored() > 0) {
			for(int i=0; i<4; i++) {
				if(SLOTS_INPUT.getStackInSlot(i) != null && SLOTS_INPUT.getStackInSlot(i).getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem tempChargingItem = (IEnergyContainerItem)SLOTS_INPUT.getStackInSlot(i).getItem();
					extractEnergy(EnumFacing.UP, tempChargingItem.receiveEnergy(SLOTS_INPUT.getStackInSlot(i), STORAGE.getMaxExtract(), false), false);
					if(tempChargingItem.getEnergyStored(SLOTS_INPUT.getStackInSlot(i)) >= tempChargingItem.getMaxEnergyStored(SLOTS_INPUT.getStackInSlot(i))) {
						outputItem(i);
					}
				}
			}	
		}
	}
	public void outputItem(int fromSlot){
		for(int i=0; i<4; i++) {
			if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, i, SLOTS_INPUT.getStackInSlot(fromSlot))) {
				SLOTS_OUTPUT.insertItem(i, SLOTS_INPUT.getStackInSlot(fromSlot).copy(), false);
				SLOTS_INPUT.extractItem(fromSlot, 1, false);
				return;
			}
		}
	}

	@Override
	public String getName() {
		return "Charging Station";		
	}			
}
