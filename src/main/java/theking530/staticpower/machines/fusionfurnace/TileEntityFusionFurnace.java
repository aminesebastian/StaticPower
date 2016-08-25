package theking530.staticpower.machines.fusionfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FusionFurnaceRecipeWrapper;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityFusionFurnace extends BaseMachine {
	

	public TileEntityFusionFurnace() {
		initializeBasicMachine(2, 1000, 100000, 80, 100, 4, 4, 1);
	}	
	//IInventory				
	@Override
	public String getName() {
		return "Fusion Furnace";		
	}
	
	//Process 
	public FusionFurnaceRecipeWrapper getFusionResult(ItemStack... inputs) {
		if(inputs != null && inputs[0] != null) {
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
				if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, tempWrapper.getOutputItem())) {
					if(STORAGE.getEnergyStored() >= getProcessingCost()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public void process() {
		if(!isProcessing() && !isMoving() && canProcess(getInputStack(0), getInputStack(1), getInputStack(2), getInputStack(3), getInputStack(4))) {
				MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && canProcess(getInputStack(0), getInputStack(1), getInputStack(2), getInputStack(3), getInputStack(4))) {
			if(MOVE_TIMER < MOVE_SPEED) {
				MOVE_TIMER++;
			}else{
				if(getInputStack(0) != null) {
					moveItem(SLOTS_INPUT, 0, SLOTS_INTERNAL, 0);
				}
				if(getInputStack(1)!= null) {
					moveItem(SLOTS_INPUT, 1, SLOTS_INTERNAL, 1);
				}
				if(getInputStack(2) != null) {
					moveItem(SLOTS_INPUT, 2, SLOTS_INTERNAL, 2);
				}
				if(getInputStack(3) != null) {
					moveItem(SLOTS_INPUT, 3, SLOTS_INTERNAL, 3);
				}
				if(getInputStack(4) != null) {
					moveItem(SLOTS_INPUT, 4, SLOTS_INTERNAL, 4);
				}
				MOVE_TIMER=0;
				PROCESSING_TIMER = 1;
			}
		}
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER <= PROCESSING_TIME) {
				useEnergy(getProcessingCost() / PROCESSING_TIME);
				PROCESSING_TIMER++;
			}else{
				if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, getFusionResult(getInternalStack(0), getInternalStack(1), getInternalStack(2), getInternalStack(3), getInternalStack(4)).getOutputItem())) {
					SLOTS_OUTPUT.insertItem(0, getFusionResult(getInternalStack(0), getInternalStack(1), getInternalStack(2), getInternalStack(3), getInternalStack(4)).getOutputItem().copy(), false);
					setInternalStack(0, null);
					setInternalStack(1, null);
					setInternalStack(2, null);
					setInternalStack(3, null);
					setInternalStack(4, null);
					PROCESSING_TIMER=0;
				}
			}
		}
	}
 
}


	
