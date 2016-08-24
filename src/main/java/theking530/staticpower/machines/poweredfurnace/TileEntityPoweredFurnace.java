package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.machines.BaseMachine;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityPoweredFurnace extends BaseMachine {

	public TileEntityPoweredFurnace() {
		initializeBasicMachine(2, 100, 100000, 80, 100, 1, 1, 1);
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
			if(canSlotAcceptItemstack(getResult(itemStack), getOutputStack(0)) && STORAGE.getEnergyStored() >= 1000*PROCESSING_ENERGY_MULT) {
				return true;
			}
		}
		return false;
	}
	public void process() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
				PROCESSING_TIMER++;
			}else{
				if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, getResult(getInternalStack(0)))) {
					SLOTS_OUTPUT.insertItem(0, getResult(getInternalStack(0)).copy(), false);
					useEnergy(1000*PROCESSING_ENERGY_MULT);
					setInternalStack(0, null);
					PROCESSING_TIMER=0;
					MOVE_TIMER = 0;
				}
			}
		}
		
	}
}


	
