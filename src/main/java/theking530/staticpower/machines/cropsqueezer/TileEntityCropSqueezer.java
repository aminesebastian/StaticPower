package theking530.staticpower.machines.cropsqueezer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityInputServo;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityCropSqueezer extends BaseMachineWithTank {

	public DrainToBucketComponent bucketDrainComponent;
	
	public TileEntityCropSqueezer() {
		initializeBaseMachineWithTank(2, 100, 100000, 80, 50, 1, 2, 2, 5000);
		bucketDrainComponent = new DrainToBucketComponent("BucketDrain", slotsInput, 1, slotsOutput, 1, this, TANK, FLUID_TO_CONTAINER_RATE);
		registerComponent(bucketDrainComponent);
		registerComponent(new TileEntityInputServo(this, 2, slotsInput, 0));
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
		if(hasResult(itemstack) && fluidstack != null && InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemstack))) {
			if(fluidstack.amount + TANK.getFluidAmount() > TANK.getCapacity()) {
				return false;
			}
			if(energyStorage.getEnergyStored() < getProcessingEnergy(itemstack)) {
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
			return initialPowerUse*processingEnergyMult;
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
					moveItem(slotsInput, 0, slotsInternal, 0);
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
						TANK.fill(getFluidResult(slotsInternal.getStackInSlot(0)), true);
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
		return TANK.fill(resource, doFill);
	}
}


	
