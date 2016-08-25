package theking530.staticpower.machines.poweredgrinder;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityPoweredGrinder extends BaseMachine {
	
	public TileEntityPoweredGrinder() {
		initializeBasicMachine(2, 1000, 100000, 80, 100, 1, 1, 3);
	}
		
	//IInventory				
	@Override
	public String getName() {
		return "Powered Grinder";		
	}
	
	//Process 
	public GrinderOutputWrapper getGrindingResult(ItemStack stack) {
		if(stack != null) {
			return GrinderRecipeRegistry.Grinding().getgrindingResult(stack);
		}else{
			return null;
		}
		
	}
	public boolean hasResult(ItemStack stack) {
		if(stack != null && getGrindingResult(stack) != null) {
			return true;
		}
		return false;
	}
	@Override
	public boolean canProcess(ItemStack stack) {
		if(hasResult(stack) == true) {
			if(getGrindingResult(stack).getOutputItemCount() > 0) {
				boolean flag = true;
				boolean slot1 = false;
				boolean slot2 = false;
				boolean slot3 = false;
				GrinderOutputWrapper tempWrapper = getGrindingResult(stack);
				for(int i=0; i<tempWrapper.getOutputItemCount(); i++) {
					if(tempWrapper.getOutputItems().get(i).isValid()) {
						if(canSlotAcceptItemstack(tempWrapper.getOutputItems().get(i).getOutput(), getOutputStack(0)) && slot1 == false) {
							slot1 = true;
						}else if(canSlotAcceptItemstack(tempWrapper.getOutputItems().get(i).getOutput(), getOutputStack(1)) && slot2 == false) {
							slot2 = true;
						}else if(canSlotAcceptItemstack(tempWrapper.getOutputItems().get(i).getOutput(), getOutputStack(2)) && slot3 == false) {
							slot3 = true;
						}else{
							flag = false;
						}
					}
				}
				if(STORAGE.getEnergyStored() >= getProcessingCost() && flag == true) {
					return true;
				}
			}
		}
		return false;
	}
	public void process() {
		if(!isProcessing() && !isMoving() && canProcess(getInputStack(0))) {
			MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && canProcess(getInputStack(0))) {
			if(MOVE_TIMER < MOVE_SPEED) {
				MOVE_TIMER++;
			}else{
				moveItem(SLOTS_INPUT, 0, SLOTS_INTERNAL, 0);
				MOVE_TIMER=0;
				PROCESSING_TIMER = 1;
			}
		}
		if(isProcessing() && !isMoving()) {
			if(!worldObj.isRemote) {
				if(PROCESSING_TIMER <= PROCESSING_TIME) {
					useEnergy(getProcessingCost() / PROCESSING_TIME);
					PROCESSING_TIMER++;
				}else{
					if(getGrindingResult(getInternalStack(0)) != null) {
						for(int j=0; j<getGrindingResult(getInternalStack(0)).getOutputItemCount(); j++) {
							for(int i=0; i<3; i++) {
								if(diceRoll(getGrindingResult(getInternalStack(0)).getOutputItems().get(j).getPercentage())) {
									if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, i, getGrindingResult(getInternalStack(0)).getOutputItems().get(j).getOutput())) {
										SLOTS_OUTPUT.insertItem(i, getGrindingResult(getInternalStack(0)).getOutputItems().get(j).getOutput().copy(), false);
										break;
									}	
								}
							}
						}
					}
					setInternalStack(0, null);
					markForUpdate();
					PROCESSING_TIMER=0;
					MOVE_TIMER = 0;
				}
			}	
		}
	}
 
	public boolean diceRoll(float percentage) {
		if(percentage >= 1) {
			return true;
		}
		float randFloat = RANDOM.nextFloat();
		
		return percentage > randFloat ? true : false;
	}
}


	
