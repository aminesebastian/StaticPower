package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityPoweredFurnace extends BaseMachine {

	public TileEntityPoweredFurnace() {
		initializeBasicMachine(2, 1000, 100000, 80, 100, 1, 1, 1);
	}
	@Override
	public String getName() {
		return "Powered Furnace";
		
	}			
	
	//Functionality
	@Override
   	public ItemStack getResult(ItemStack itemStack) {
		if(itemStack!= null) {
			return FurnaceRecipes.instance().getSmeltingResult(itemStack);
		}else{
			return null;
		}
	}
	public boolean hasResult(ItemStack itemStack) {
		if(itemStack != null) {
			if(FurnaceRecipes.instance().getSmeltingResult(itemStack) != null) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean canProcess(ItemStack itemStack) {
		if(hasResult(itemStack)) {
			if(canSlotAcceptItemstack(getResult(itemStack), getOutputStack(0)) && STORAGE.getEnergyStored() >= getProcessingCost()) {
				return true;
			}
		}
		return false;
	}
	public void process() {
		if(!worldObj.isRemote) {
			if(!isProcessing() && !isMoving() && canProcess(getInputStack(0))) {
				MOVE_TIMER++;
			}
			if(!isProcessing() && isMoving() && canProcess(getInputStack(0))) {
				if(MOVE_TIMER < MOVE_SPEED) {
					MOVE_TIMER++;
				}else{
					moveItem(SLOTS_INPUT, 0, SLOTS_INTERNAL, 0);
					PROCESSING_TIMER = 1;
					MOVE_TIMER = 0;
				}
			}
			if(isProcessing() && !isMoving()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					useEnergy(getProcessingCost() / PROCESSING_TIME);
					PROCESSING_TIMER++;
				}else{
					PROCESSING_TIMER=0;
					updateBlock();
					if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, getResult(getInternalStack(0)))) {
						//System.out.println(getResult(getInternalStack(0)));
						InventoryUtilities.insertItemIntoInventory(SLOTS_OUTPUT, getResult(getInternalStack(0)), 0, 0);
						//SLOTS_OUTPUT.insertItem(0, getResult(SLOTS_INTERNAL.getStackInSlot(0).copy()), false);
						setInternalStack(0, null);
						MOVE_TIMER = 0;
					}
				}
			}
		}
		
	}
}


	
