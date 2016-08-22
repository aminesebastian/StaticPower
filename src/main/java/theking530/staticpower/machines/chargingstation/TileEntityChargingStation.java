package theking530.staticpower.machines.chargingstation;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityChargingStation extends BaseMachine {
	
	public TileEntityChargingStation() {
		initializeBasicMachine(2, 100000, 100, 2, 11, new int[]{0,1,2,3,4}, new int[]{5,6,7}, new int[]{8,9,10});
	}
	@Override
	public void process(){
		if(STORAGE.hasEnoughPowerToExtract()) {
			for(int i=0; i<5; i++) {
				if(slots[i].getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem tempChargingItem = (IEnergyContainerItem)slots[i].getItem();
					extractEnergy(EnumFacing.UP, tempChargingItem.extractEnergy(slots[i], STORAGE.getMaxExtract(), false), false);
				}
			}	
		}
	}
}
