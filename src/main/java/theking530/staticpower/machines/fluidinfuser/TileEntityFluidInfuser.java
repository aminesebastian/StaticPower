package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;

public class TileEntityFluidInfuser extends BaseMachineWithTank {
	
	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_side = new int[] {1};		
	
	public TileEntityFluidInfuser() {
		initializeBaseMachineWithTank(2, 100, 50000, 80, 100, 6, new int[]{0}, new int[]{1}, new int[]{2,3,4}, 10000);
	}
					
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        STORAGE.readFromNBT(nbt);
        TANK.readFromNBT(nbt);
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
        TANK.writeToNBT(nbt);
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
		return "Fluid Infuser";		
	}
	
	//Functionality
	@Override
 	public ItemStack getResult(ItemStack itemStack) {
		if(itemStack != null) {
			return InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemStack, TANK.getFluid());
		}else{
			return null;
		}
	}
	@Override
	public boolean hasResult(ItemStack itemStack) {
		if(itemStack != null) {
			if(getResult(itemStack) != null && TANK.getFluidAmount() >= InfuserRecipeRegistry.Infusing().getInfusingFluidCost(slots[0], TANK.getFluid()) &&
					STORAGE.getEnergyStored() >= getProcessingEnergy(itemStack) && canSlotAcceptItemstack(getResult(itemStack), slots[1])) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int getProcessingEnergy(ItemStack itemStack) {
		if(getResult(itemStack) != null) {
			return InfuserRecipeRegistry.Infusing().getInfusingFluidCost(itemStack, TANK.getFluid())*5*PROCESSING_ENERGY_MULT;
		}
		return 0;
	}
	@Override
	public void process() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if(slots[5] == null){
			PROCESSING_TIMER = 0;
		}
		if(!isProcessing() && !isMoving() && !isTankEmpty() && hasPower() && hasResult(slots[0])) {
			MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && !isTankEmpty() && hasPower() && hasResult(slots[0])) {
			MOVE_TIMER++;
			if(MOVE_TIMER >= MOVE_SPEED) {
				MOVE_TIMER = 0;
				TANK.drain(InfuserRecipeRegistry.Infusing().getInfusingFluidCost(slots[0], TANK.getFluid()), true);
				useEnergy(getProcessingEnergy(slots[0]));
				moveItem(0, 5);
				PROCESSING_TIMER = 1;
				
			}
		}else{
			MOVE_TIMER = 0;
		}
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER < PROCESSING_TIME) {
				PROCESSING_TIMER++;
			}else{
				if(canSlotAcceptItemstack(getResult(slots[5]), slots[1])) {
					placeItemStackInSlot(getResult(slots[5]), 1);
					slots[5] = null;
					PROCESSING_TIMER = 0;
				}
			}
		}	
	}
}


	
