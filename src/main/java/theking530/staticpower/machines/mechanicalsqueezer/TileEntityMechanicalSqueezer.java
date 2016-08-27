package theking530.staticpower.machines.mechanicalsqueezer;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityMechanicalSqueezer extends BaseTileEntity {

	private String customName;
	public FluidTank TANK = new FluidTank(5000);
	public int PROCESSING_TIMER = 0;
	public int PROCESSING_TIME = 20;
	public int MOVE_TIMER = 0;
	public int MOVE_SPEED = 4;
	
	public TileEntityMechanicalSqueezer() {
		initializeBasicTileEntity(1, 2, 1);
	}
	@Override
	public String getName() {
		return "Mechanical Squeezer";		
	}		
	
	public void readFromSyncNBT(NBTTagCompound nbt) {
		TANK.readFromNBT(nbt);
		PROCESSING_TIMER = nbt.getInteger("P_TIMER");
	}
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		TANK.writeToNBT(nbt);
		nbt.setInteger("P_TIMER", PROCESSING_TIMER);
		return nbt;
	}
	
	
    //Process
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
	public boolean canProcess(ItemStack itemstack) {
		FluidStack fluidstack = SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemstack);
		if(hasResult(itemstack) && fluidstack != null && canSlotAcceptItemstack(getResult(itemstack), SLOTS_OUTPUT.getStackInSlot(0))) {
			if(fluidstack.amount + TANK.getFluidAmount() > TANK.getCapacity()) {
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
	public boolean isProcessing(){
		return PROCESSING_TIMER > 0;
	}
	public boolean isMoving(){
		return MOVE_TIMER > 0;
	}
	public int getProgressScaled(int i) {
		float ratio = (float)PROCESSING_TIMER/(float)PROCESSING_TIME;
		return (int) (ratio * i);
	}
	@Override
	public void process(){
		useFluidContainer();
	}
	public void useFluidContainer() {
		if(SLOTS_INPUT.getStackInSlot(1) != null && SLOTS_INPUT.getStackInSlot(1).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && SLOTS_INPUT.getStackInSlot(1).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
			FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)SLOTS_INPUT.getStackInSlot(1).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			if(TANK.getFluid() != null) {
				if(tempContainer.getFluid() == null || tempContainer.getFluid().isFluidEqual(TANK.getFluid())) {
					int drained = 0;				
					if(!worldObj.isRemote) {
						drained = tempContainer.fill(new FluidStack(TANK.getFluid(), 100), true);
						TANK.drain(drained, true);						
					}
					if(drained > 0) {
						worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, (float) 0.25, 1, false);						
					}
				}
			}
		}	
	}
	public void rightClick() {
		if(SLOTS_INTERNAL.getStackInSlot(0) == null){
			PROCESSING_TIMER = 0;
		}
		//Start Process
		if(!isProcessing() && !isMoving() && canProcess(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER = 1;
		}
		//Start Moving
		if(!isProcessing() && isMoving() && canProcess(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER++;
			if(MOVE_TIMER >= MOVE_SPEED) {
				MOVE_TIMER = 0;
				moveItem(SLOTS_INPUT, 0, SLOTS_INTERNAL, 0);
				PROCESSING_TIMER = 1;	
			}
		}else{
			MOVE_TIMER = 0;
		}
		//Start Processing
		if(isProcessing() && !isMoving() && canProcess(SLOTS_INTERNAL.getStackInSlot(0))) {
			if(PROCESSING_TIMER < PROCESSING_TIME) {
				PROCESSING_TIMER++;
			}else{				
				if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, getResult(SLOTS_INTERNAL.getStackInSlot(0)))) {
					TANK.fill(getFluidResult(SLOTS_INTERNAL.getStackInSlot(0)), true);
					SLOTS_OUTPUT.insertItem(0, getResult(SLOTS_INTERNAL.getStackInSlot(0)).copy(), false);
					SLOTS_INTERNAL.setStackInSlot(0, null);
					PROCESSING_TIMER = 0;
					markForUpdate();
				}
			}
		}	
	}
}


	
