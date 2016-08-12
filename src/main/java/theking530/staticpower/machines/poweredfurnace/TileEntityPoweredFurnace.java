package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.machines.BaseMachine;

public class TileEntityPoweredFurnace extends BaseMachine {

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_side = new int[] {1};		
		
	public TileEntityPoweredFurnace() {
		initializeBasicMachine(2, 100000, 80, 100, 6, new int[]{0}, new int[]{1}, new int[]{2, 3, 4});
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
	@Override
	public String getName() {
		return "Powered Furnace";
		
	}			

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i != 1) {
			return true;
		}
		return false;			
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
			if(canSlotAcceptItemstack(getResult(itemStack), slots[1]) && STORAGE.getEnergyStored() >= 1000*PROCESSING_ENERGY_MULT) {
				return true;
			}
		}
		return false;
	}
	public void process() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if(!isProcessing() && !isMoving() && canProcess(slots[0])) {
			MOVE_TIMER++;
		}
		if(!isProcessing() && isMoving() && canProcess(slots[0])) {
			if(MOVE_TIMER < MOVE_SPEED) {
				MOVE_TIMER++;
			}else{
				moveItem(0,5);
				PROCESSING_TIMER = 1;
				MOVE_TIMER = 0;
			}
		}
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER < PROCESSING_TIME) {
				PROCESSING_TIMER++;
			}else{
				if(canSlotAcceptItemstack(getResult(slots[5]), slots[1])) {
					placeStackInSlot(getResult(slots[5]), 1);
					useEnergy(1000*PROCESSING_ENERGY_MULT);
					slots[5] = null;
					//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
					PROCESSING_TIMER=0;
					MOVE_TIMER = 0;
				}
			}
		}
		
	}
}


	
