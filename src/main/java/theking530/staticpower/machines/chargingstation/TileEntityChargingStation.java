package theking530.staticpower.machines.chargingstation;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityChargingStation extends BaseMachine {
	
	public TileEntityChargingStation() {
		initializeBasicMachine(2, 0, 100000, 500, 2, 13, new int[]{0,1,2,3}, new int[]{4,5,6,7}, new int[]{8,9,10});
		setBatterySlot(11);
	}
	@Override
	public void process(){
		if(STORAGE.getEnergyStored() > 0) {
			for(int i=0; i<4; i++) {
				if(slots[i] != null && slots[i].getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem tempChargingItem = (IEnergyContainerItem)slots[i].getItem();
					extractEnergy(EnumFacing.UP, tempChargingItem.receiveEnergy(slots[i], STORAGE.getMaxExtract(), false), false);
					if(tempChargingItem.getEnergyStored(slots[i]) >= tempChargingItem.getMaxEnergyStored(slots[i])) {
						outputItem(i);
					}
				}
			}	
		}
	}
	public void outputItem(int fromSlot){
		if(slots[4] == null) {
			slots[4] = slots[fromSlot].copy();
			slots[fromSlot].stackSize--;
			if(slots[fromSlot].stackSize <= 0) {
				slots[fromSlot] = null;
			}
			return;
		}
		if(slots[5] == null) {
			slots[5] = slots[fromSlot].copy();
			slots[fromSlot].stackSize--;
			if(slots[fromSlot].stackSize <= 0) {
				slots[fromSlot] = null;
			}
			return;
		}
		if(slots[6] == null) {
			slots[6] = slots[fromSlot].copy();
			slots[fromSlot].stackSize--;
			if(slots[fromSlot].stackSize <= 0) {
				slots[fromSlot] = null;
			}
			return;
		}
		if(slots[7] == null) {
			slots[7] = slots[fromSlot].copy();
			slots[fromSlot].stackSize--;
			if(slots[fromSlot].stackSize <= 0) {
				slots[fromSlot] = null;
			}
			return;
		}
	}

	@Override
	public String getName() {
		return "Charging Station";		
	}			
}
