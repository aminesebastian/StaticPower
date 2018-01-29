package theking530.staticpower.machines.fusionfurnace;

import net.minecraft.item.ItemStack;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FusionFurnaceRecipeWrapper;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityFusionFurnace extends BaseMachine {
	
	public TileEntityFusionFurnace() {
		initializeBasicMachine(2, 1000, 100000, 80, 100, 5, 5, 1);
	}	
	//IInventory				
	@Override
	public String getName() {
		return "Fusion Furnace";		
	}
	
	//Process 
	public FusionFurnaceRecipeWrapper getFusionResult(ItemStack... inputs) {
		if(inputs != null && inputs[0] != ItemStack.EMPTY) {
			return FusionRecipeRegistry.Fusing().getFusionResult(inputs);
		}else{
			return null;
		}		
	}
	public boolean hasResult(ItemStack... inputs) {
		if(inputs != null && getFusionResult(inputs) != null) {
			return true;
		}
		return false;
	}
	public boolean canProcess(ItemStack... inputs) {
		if(hasResult(inputs) == true) {
			if(getFusionResult(inputs).getOutputItem() != null) {
				FusionFurnaceRecipeWrapper tempWrapper = getFusionResult(inputs);
				if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, tempWrapper.getOutputItem())) {
					if(energyStorage.getEnergyStored() >= getProcessingCost()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public void process() {
		if(!isProcessing() && !isMoving() && canProcess(getInputStack(0), getInputStack(1), getInputStack(2), getInputStack(3), getInputStack(4))) {
				moveTimer = 1;
		}
		if(!isProcessing() && isMoving() && canProcess(getInputStack(0), getInputStack(1), getInputStack(2), getInputStack(3), getInputStack(4))) {
			if(moveTimer < moveSpeed) {
				moveTimer++;
			}else{
				if(getInputStack(0) != ItemStack.EMPTY) {
					moveItem(slotsInput, 0, slotsInternal, 0);
				}
				if(getInputStack(1)!= ItemStack.EMPTY) {
					moveItem(slotsInput, 1, slotsInternal, 1);
				}
				if(getInputStack(2) != ItemStack.EMPTY) {
					moveItem(slotsInput, 2, slotsInternal, 2);
				}
				if(getInputStack(3) != ItemStack.EMPTY) {
					moveItem(slotsInput, 3, slotsInternal, 3);
				}
				if(getInputStack(4) != ItemStack.EMPTY) {
					moveItem(slotsInput, 4, slotsInternal, 4);
				}
				moveTimer=0;
				processingTimer = 1;
			}
		}
		if(isProcessing() && !isMoving()) {
			if(processingTimer <= processingTime) {
				useEnergy(getProcessingCost() / processingTime);
				processingTimer++;
			}else{
				if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getFusionResult(getInternalStack(0), getInternalStack(1), getInternalStack(2), getInternalStack(3), getInternalStack(4)).getOutputItem())) {
					slotsOutput.insertItem(0, getFusionResult(getInternalStack(0), getInternalStack(1), getInternalStack(2), getInternalStack(3), getInternalStack(4)).getOutputItem().copy(), false);
					setInternalStack(0, ItemStack.EMPTY);
					setInternalStack(1, ItemStack.EMPTY);
					setInternalStack(2, ItemStack.EMPTY);
					setInternalStack(3, ItemStack.EMPTY);
					setInternalStack(4, ItemStack.EMPTY);
					processingTimer=0;
				}
			}
		}
	}
 
}


	
