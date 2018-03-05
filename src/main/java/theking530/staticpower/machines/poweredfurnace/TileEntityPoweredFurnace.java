package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;

public class TileEntityPoweredFurnace extends BaseMachine {

	public TileEntityPoweredFurnace() {
		initializeSlots(2, 1, 1);
		initializeBasicMachine(2, 1000, 100000, 80, 180);
		registerComponent(new TileEntityItemOutputServo(this, 1, slotsOutput, 0));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0));
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 1, energyStorage));
	}
	@Override
	public String getName() {
		return "container.PoweredFurnace";		
	}			
	
	//Functionality
	@Override
   	public ItemStack getResult(ItemStack itemStack) {
		if(itemStack!= ItemStack.EMPTY) {
			return FurnaceRecipes.instance().getSmeltingResult(itemStack);
		}else{
			return ItemStack.EMPTY;
		}
	}
	public boolean hasResult(ItemStack itemStack) {
		if(itemStack != null) {
			if(!FurnaceRecipes.instance().getSmeltingResult(itemStack).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean canProcess(ItemStack itemStack) {
		if(hasResult(itemStack)) {
			if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemStack)) && energyStorage.getEnergyStored() >= getProcessingCost()) {
				return true;
			}
		}
		return false;
	}
	public void process() {
		if(!getWorld().isRemote) {
			if(!isProcessing() && !isMoving() && canProcess(getInputStack(0))) {
				moveTimer++;
			}
			if(!isProcessing() && isMoving() && canProcess(getInputStack(0))) {
				if(moveTimer < moveSpeed) {
					moveTimer++;
				}else{
					transferItemInternally(slotsInput, 0, slotsInternal, 0);
					processingTimer = 1;
					moveTimer = 0;
					updateBlock();
				}
			}
			if(isProcessing() && !isMoving()) {
				if(processingTimer < processingTime) {
					useEnergy(getProcessingCost() / processingTime);
					processingTimer++;
				}else{
					processingTimer=0;
					updateBlock();
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(getInternalStack(0)))) {
						InventoryUtilities.insertItemIntoInventory(slotsOutput, getResult(getInternalStack(0)), 0, 0);
						setInternalStack(0, ItemStack.EMPTY);
						moveTimer = 0;
					}
				}
			}
		}	
	}
}


	
