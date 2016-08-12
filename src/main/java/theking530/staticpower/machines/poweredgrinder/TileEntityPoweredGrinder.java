package theking530.staticpower.machines.poweredgrinder;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.handlers.crafting.wrappers.GrinderOutputWrapper;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityPoweredGrinder extends BaseMachine {
	
	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_side = new int[] {1,2,3};		

	
	public TileEntityPoweredGrinder() {
		initializeBasicMachine(2, 100000, 80, 100, 8, new int[]{0}, new int[]{1, 2, 3}, new int[]{4, 5, 6});
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
						if(canSlotAcceptItemstack(tempWrapper.getOutputItems().get(i).getOutput(), slots[1]) && slot1 == false) {
							slot1 = true;
						}else if(canSlotAcceptItemstack(tempWrapper.getOutputItems().get(i).getOutput(), slots[2]) && slot2 == false) {
							slot2 = true;
						}else if(canSlotAcceptItemstack(tempWrapper.getOutputItems().get(i).getOutput(), slots[3]) && slot3 == false) {
							slot3 = true;
						}else{
							flag = false;
						}
					}
				}
				if(STORAGE.getEnergyStored() >= PROCESSING_ENERGY_MULT*1000 && flag == true) {
					return true;
				}
			}
		}
		return false;
	}
	public void process() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if(!isProcessing() && !isMoving() && canProcess(slots[0])) {
				MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && canProcess(slots[0])) {
			if(MOVE_TIMER < MOVE_SPEED) {
				MOVE_TIMER++;
			}else{
				moveItem(0,7);
				MOVE_TIMER=0;
				PROCESSING_TIMER = 1;
			}
		}
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER <= PROCESSING_TIME) {
				useEnergy((1000*PROCESSING_ENERGY_MULT) / PROCESSING_TIME);
				PROCESSING_TIMER++;
			}else{
				if(getGrindingResult(slots[7]) != null) {
					for(int j=0; j<getGrindingResult(slots[7]).getOutputItemCount(); j++) {
						for(int i=1; i<4; i++) {
							if(diceRoll(getGrindingResult(slots[7]).getOutputItems().get(j).getPercentage())) {
								if(placeStackInSlot(getGrindingResult(slots[7]).getOutputItems().get(j).getOutput(), i) == true) {
									break;
								}	
							}
						}
					}
				}
				slots[7] = null;
				PROCESSING_TIMER=0;
				MOVE_TIMER = 0;
			}
		}
	}
 
	public boolean diceRoll(float percentage) {
		Random rand = new Random();
		float randFloat = rand.nextFloat();
		
		return percentage > randFloat ? true : false;
	}
}


	
