package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityFluidInfuser extends BaseMachineWithTank {
	
	public DrainToBucketComponent DRAIN_COMPONENT;
	public FluidStack LAST_CONTAINED_FLUID;
	
	public TileEntityFluidInfuser() {
		initializeBaseMachineWithTank(2, 1000, 50000, 80, 100, 1, 2, 2, 10000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", slotsInput, 1, slotsOutput, 1, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT.setMode(FluidContainerInteractionMode.FillFromContainer);
	}
	//IInventory				
	@Override
	public String getName() {
		return "Fluid Infuser";		
	}
	
	//Functionality
	@Override
 	public ItemStack getResult(ItemStack itemStack) {
		if(itemStack != ItemStack.EMPTY) {
			if(TANK.getFluid() != null) {
				return InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemStack, TANK.getFluid());
			}else{			
				return InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemStack, LAST_CONTAINED_FLUID);
			}

		}else{
			return ItemStack.EMPTY;
		}
	}
	@Override
	public boolean hasResult(ItemStack itemStack) {
		if(itemStack != ItemStack.EMPTY) {
			if(getResult(itemStack) != ItemStack.EMPTY && TANK.getFluidAmount() >= InfuserRecipeRegistry.Infusing().getInfusingFluidCost(slotsInput.getStackInSlot(0), TANK.getFluid()) &&
					energyStorage.getEnergyStored() >= getProcessingEnergy(itemStack) && InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemStack))) {
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
			return InfuserRecipeRegistry.Infusing().getInfusingFluidCost(itemStack, TANK.getFluid())*5*processingEnergyMult;
		}
		return 0;
	}
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        LAST_CONTAINED_FLUID = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("LAST_FLUID"));
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(LAST_CONTAINED_FLUID != null) {
            NBTTagCompound last =  new NBTTagCompound();
            LAST_CONTAINED_FLUID.writeToNBT(last);	
            nbt.setTag("LAST_FLUID", last);
        }
    	return nbt;
	}

	@Override
	public void process() {
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT.update();
		}
		if(slotsInternal.getStackInSlot(0) == ItemStack.EMPTY){
			processingTimer = 0;
		}
		if(!isProcessing() && !isMoving() && !isTankEmpty() && hasPower() && hasResult(slotsInput.getStackInSlot(0))) {
			moveTimer = 1;
		}
		if(!isProcessing() && isMoving() && !isTankEmpty() && hasPower() && hasResult(slotsInput.getStackInSlot(0))) {
			moveTimer++;
			if(moveTimer >= moveSpeed) {
				moveTimer = 0;
				if(!getWorld().isRemote) {
					TANK.drain(InfuserRecipeRegistry.Infusing().getInfusingFluidCost(slotsInput.getStackInSlot(0), TANK.getFluid()), true);
					useEnergy(getProcessingEnergy(slotsInput.getStackInSlot(0)));
				}
				moveItem(slotsInput, 0, slotsInternal, 0);
				processingTimer = 1;
				
			}
		}else{
			moveTimer = 0;
		}
		if(isProcessing() && !isMoving()) {
			if(processingTimer < processingTime) {
				processingTimer++;
			}else{
				if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(slotsInternal.getStackInSlot(0)))) {
					if(slotsInternal.getStackInSlot(0) != ItemStack.EMPTY && getResult(slotsInternal.getStackInSlot(0)) != ItemStack.EMPTY) { //Weird Bug when Serializing
						slotsOutput.insertItem(0, getResult(slotsInternal.getStackInSlot(0)).copy(), false);
					}
					slotsInternal.setStackInSlot(0, ItemStack.EMPTY);
					processingTimer = 0;
					updateBlock();
				}
			}
		}	
		
	}
}


	
