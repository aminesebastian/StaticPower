package theking530.staticpower.machines.fusionfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.FusionFurnaceRecipeWrapper;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityFusionFurnace extends BaseMachine {
	

	public TileEntityFusionFurnace() {
		initializeBasicMachine(2, 100000, 80, 100, 14, new int[]{0, 1, 2, 3, 4}, new int[]{5}, new int[]{6, 7, 8});
	}

    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        STORAGE.readFromNBT(nbt);
        
        if(slots != null) {
            NBTTagList list = nbt.getTagList("Items", 10);
    		slots = new ItemStack[getSizeInventory()];
            for (int i =0; i < list.tagCount(); i++) {
    			NBTTagCompound nbt1 = (NBTTagCompound)list.getCompoundTagAt(i);
    			byte b0 = nbt1.getByte("Slot");
    			
    			if (b0 >= 0 && b0 < slots.length) {
    				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
    			}
    		}	
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        STORAGE.writeToNBT(nbt);	
    	if(slots != null) {
        	NBTTagList list = new NBTTagList();
    		for (int i = 0; i < slots.length; i++) {
    			if (slots[i] != null) {
    				NBTTagCompound nbt1 = new NBTTagCompound();
    				nbt1.setByte("Slot", (byte)i);
    				slots[i].writeToNBT(nbt1);
    				list.appendTag(nbt1);
    			}
    			
    		}
    		nbt.setTag("Items", list);
    	}	
    	return nbt;
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
				if(canSlotAcceptItemstack(tempWrapper.getOutputItem(), slots[5])) {
					if(STORAGE.getEnergyStored() >= PROCESSING_ENERGY_MULT*1000) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public void process() {
		if(!isProcessing() && !isMoving() && canProcess(slots[0], slots[1], slots[2], slots[3], slots[4])) {
				MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && canProcess(slots[0], slots[1], slots[2], slots[3], slots[4])) {
			if(MOVE_TIMER < MOVE_SPEED) {
				MOVE_TIMER++;
			}else{
				if(slots[0] != null) {
					moveItem(0,9);
				}
				if(slots[1] != null) {
					moveItem(1,10);
				}
				if(slots[2] != null) {
					moveItem(2,11);
				}
				if(slots[3] != null) {
					moveItem(3,12);
				}
				if(slots[4] != null) {
					moveItem(4,13);
				}
				MOVE_TIMER=0;
				PROCESSING_TIMER = 1;
			}
		}
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER <= PROCESSING_TIME) {
				useEnergy((1000*PROCESSING_ENERGY_MULT) / PROCESSING_TIME);
				PROCESSING_TIMER++;
			}else{
				if(canSlotAcceptItemstack(getFusionResult(slots[9], slots[10], slots[11], slots[12], slots[13]).getOutputItem(), slots[5])) {
					placeStackInSlot(getFusionResult(slots[9], slots[10], slots[11], slots[12], slots[13]).getOutputItem(), 5);
					slots[9] = null;
					slots[10] = null;
					slots[11] = null;
					slots[12] = null;
					slots[13] = null;
					PROCESSING_TIMER=0;
				}
			}
		}
	}
 
}


	
