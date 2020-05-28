package theking530.staticpower.machines.chargingstation;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import theking530.staticpower.machines.TileEntityMachine;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.TileItemEntityInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileItemEntityOutputServo;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityChargingStation extends TileEntityMachine {
	
	public TileEntityChargingStation() {
		initializeSlots(1, 4, 4);
		initializeBasicMachine(2, 0, 100000, 500, 2);
		energyStorage.setMaxExtract(512);
		
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 0, energyStorage));
		registerComponent(new TileItemEntityOutputServo(this, 1, slotsOutput, 0, 1, 2, 3));
		registerComponent(new TileItemEntityInputServo(this, 2, slotsInput, 0, 1, 2, 3));
		
	}
	@Override
	public void process(){
		if(energyStorage.getEnergyStored() > 0) {
			for(int i=0; i<4; i++) {
				if(slotsInput.getStackInSlot(i) != ItemStack.EMPTY && slotsInput.getStackInSlot(i).getItem() instanceof IEnergyContainerItem) {
					IEnergyContainerItem tempChargingItem = (IEnergyContainerItem)slotsInput.getStackInSlot(i).getItem();
					extractEnergy(Direction.UP, tempChargingItem.receiveEnergy(slotsInput.getStackInSlot(i), energyStorage.getMaxExtract(), false), false);
					if(tempChargingItem.getEnergyStored(slotsInput.getStackInSlot(i)) >= tempChargingItem.getMaxEnergyStored(slotsInput.getStackInSlot(i))) {
						outputItem(i);
					}
				}
			}	
		}
	}
	public void outputItem(int fromSlot){
		for(int i=0; i<4; i++) {
			if(InventoryUtilities.canFullyInsertStackIntoSlot(slotsOutput, i, slotsInput.getStackInSlot(fromSlot))) {
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
