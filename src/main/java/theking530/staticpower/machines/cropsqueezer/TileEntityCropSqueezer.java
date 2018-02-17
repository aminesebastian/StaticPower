package theking530.staticpower.machines.cropsqueezer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;

public class TileEntityCropSqueezer extends BaseMachineWithTank {

	public TileEntityCropSqueezer() {
		initializeBasicMachine(2, 100, 100000, 80, 50);
		initializeTank(5000);
		initializeSlots(1, 2, 2);
		
		registerComponent(new FluidContainerComponent("BucketDrain", slotsInput, 1, slotsOutput, 1, this, fluidTank, fluidToContainerRate));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0));
	}
	@Override
	public String getName() {
		return "Crop Squeezer";		
	}		
	
    //Process
	@Override
	public ItemStack getResult(ItemStack itemStack) {
		return SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemStack);
	}
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
		if(hasResult(itemstack) && fluidstack != null && InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemstack))) {
			if(fluidstack.amount + fluidTank.getFluidAmount() > fluidTank.getCapacity()) {
				return false;
			}
			if(energyStorage.getEnergyStored() < getProcessingEnergy(itemstack)) {
				return false;
			}
			if (fluidTank.getFluid() != null && !fluidstack.isFluidEqual(fluidTank.getFluid())) {
				return false;
			}
			if(fluidTank.getFluid() == null) {
				return true;
			}
			if(fluidTank.getFluidAmount() + fluidstack.amount <= fluidTank.getCapacity()) {
				return true;
			}
			if(fluidTank.getFluidAmount() + fluidstack.amount > fluidTank.getCapacity()) {
				return false;
			}
			if (fluidTank.getFluid() != null && fluidstack.isFluidEqual(fluidTank.getFluid())) {
				return true;
			}				
		}
		return false;
	}
	@Override
	public int getProcessingCost() {
		if(slotsInput.getStackInSlot(0) != ItemStack.EMPTY) {
			return getProcessingEnergy(slotsInput.getStackInSlot(0));
		}else if(slotsInternal.getStackInSlot(0) != ItemStack.EMPTY){
			return getProcessingEnergy(slotsInternal.getStackInSlot(0));
		}
		return 0;
	}
	@Override
	public int getProcessingEnergy(ItemStack itemStack) {
		if(getResult(itemStack) != ItemStack.EMPTY) {
			return (int) (initialPowerUse*processingEnergyMult);
		}
		return 0;
	}	
	public void process() {
		if(!getWorld().isRemote) {
			if(slotsInternal.getStackInSlot(0) == ItemStack.EMPTY){
				processingTimer = 0;
			}
			//Start Process
			if(!isProcessing() && !isMoving() && canProcess(slotsInput.getStackInSlot(0))) {
				moveTimer = 1;
			}
			//Start Moving
			if(!isProcessing() && isMoving() && canProcess(slotsInput.getStackInSlot(0))) {
				moveTimer++;
				if(moveTimer >= moveSpeed) {
					moveTimer = 0;
					useEnergy(getProcessingEnergy(slotsInput.getStackInSlot(0)));
					transferItemInternally(slotsInput, 0, slotsInternal, 0);
					processingTimer = 1;	
				}
			}else{
				moveTimer = 0;
			}
			//Start Processing
			if(isProcessing() && !isMoving() && canProcess(slotsInternal.getStackInSlot(0))) {
				if(processingTimer < processingTime) {
					processingTimer++;
					updateBlock();
				}else{				
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(slotsInternal.getStackInSlot(0)))) {
						fluidTank.fill(getFluidResult(slotsInternal.getStackInSlot(0)), true);
						slotsOutput.insertItem(0, getResult(slotsInternal.getStackInSlot(0)).copy(), false);
						slotsInternal.setStackInSlot(0, ItemStack.EMPTY);
						processingTimer = 0;
						updateBlock();
					}
				}
			}	
		}
	}
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return fluidTank.fill(resource, doFill);
	}
}


	
