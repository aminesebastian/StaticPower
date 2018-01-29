package theking530.staticpower.machines.chargingstation;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityChargingStation extends BaseMachine {
	
	public TileEntityChargingStation() {
		initializeBasicMachine(2, 0, 100000, 500, 2, 0, 6, 4);
		setBatterySlot(4);
		energyStorage.setMaxExtract(512);
	}
	@Override
	public void process(){
		if(energyStorage.getEnergyStored() > 0) {
			for(int i=0; i<4; i++) {
				if(slotsInput.getStackInSlot(i) != ItemStack.EMPTY && slotsInput.getStackInSlot(i).getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem tempChargingItem = (IEnergyContainerItem)slotsInput.getStackInSlot(i).getItem();
					extractEnergy(EnumFacing.UP, tempChargingItem.receiveEnergy(slotsInput.getStackInSlot(i), energyStorage.getMaxExtract(), false), false);
					if(tempChargingItem.getEnergyStored(slotsInput.getStackInSlot(i)) >= tempChargingItem.getMaxEnergyStored(slotsInput.getStackInSlot(i))) {
						outputItem(i);
					}
				}
			}	
		}
	}
	public void outputItem(int fromSlot){
		for(int i=0; i<4; i++) {
			if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, i, slotsInput.getStackInSlot(fromSlot))) {
				slotsOutput.insertItem(i, slotsInput.getStackInSlot(fromSlot).copy(), false);
				slotsInput.extractItem(fromSlot, 1, false);
				return;
			}
		}
	}

	@Override
	public String getName() {
		return "Charging Station";		
	}			
}
