package theking530.staticpower.machines.cropsqueezer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;

public class TileEntityCropSqueezer extends BaseMachineWithTank {

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {0};
	private static final int[] slots_side = new int[] {0};		
	
	public int INITIAL_PROCESSING_ENERGY_MULT = 10;
	public int ENERGY_CAPACTIY = 100000;
	
	private String customName;
	
	public TileEntityCropSqueezer() {
		initializeBaseMachineWithTank(2, 100000, 80, 50, 6, new int[]{0}, new int[]{1}, new int[]{2,3,4}, 5000);
	}
	@Override
	public String getName() {
		return "Crop Squeezer";		
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
	
		
	
    //Process
	@Override
	public ItemStack getResult(ItemStack itemStack) {
		return SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemStack);
	}
	@Override 
	public boolean hasResult(ItemStack itemstack) {
		if(itemstack != null && getResult(itemstack) != null) {
			return true;
		}
		return false;
	}
	public FluidStack getFluidResult(ItemStack itemStack) {
    	if(itemStack != null) {
    		return SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemStack);
    	}
    	return null;
    }
	@Override
	public boolean canProcess(ItemStack itemstack) {
		FluidStack fluidstack = SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemstack);
		if(hasResult(itemstack) && fluidstack != null && canSlotAcceptItemstack(getResult(itemstack), slots[1])) {
			if(fluidstack.amount + TANK.getFluidAmount() > TANK.getCapacity()) {
				return false;
			}
			if(STORAGE.getEnergyStored() < fluidstack.amount*PROCESSING_ENERGY_MULT) {
				return false;
			}
			if (TANK.getFluid() != null && !fluidstack.isFluidEqual(TANK.getFluid())) {
				return false;
			}
			if(TANK.getFluid() == null) {
				return true;
			}
			if(TANK.getFluidAmount() + fluidstack.amount <= TANK.getCapacity()) {
				return true;
			}
			if(TANK.getFluidAmount() + fluidstack.amount > TANK.getCapacity()) {
				return false;
			}
			if (TANK.getFluid() != null && fluidstack.isFluidEqual(TANK.getFluid())) {
				return true;
			}				
		}
		return false;
	}
	@Override
	public int getProcessingEnergy(ItemStack itemStack) {
		if(getResult(itemStack) != null) {
			return 100*PROCESSING_ENERGY_MULT;
		}
		return 0;
	}	
	public void process() {
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if(slots[5] == null){
			PROCESSING_TIMER = 0;
		}
		//Start Process
		if(!isProcessing() && !isMoving() && canProcess(slots[0])) {
			MOVE_TIMER = 1;
		}
		//Start Moving
		if(!isProcessing() && isMoving() && canProcess(slots[0])) {
			MOVE_TIMER++;
			if(MOVE_TIMER >= MOVE_SPEED) {
				MOVE_TIMER = 0;
				useEnergy(getProcessingEnergy(slots[0]));
				moveItem(0, 5);
				PROCESSING_TIMER = 1;	
			}
		}else{
			MOVE_TIMER = 0;
		}
		//Start Processing
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER < PROCESSING_TIME) {
				PROCESSING_TIMER++;
			}else{				
				if(canSlotAcceptItemstack(getResult(slots[5]), slots[1])) {
					placeItemStackInSlot(getResult(slots[5]), 1);
					TANK.fill(getFluidResult(slots[5]), true);
					slots[5] = null;
					PROCESSING_TIMER = 0;
				}
			}
		}	
	}
}


	
