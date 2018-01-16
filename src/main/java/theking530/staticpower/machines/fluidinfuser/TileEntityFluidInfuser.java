package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityFluidInfuser extends BaseMachineWithTank {
	
	public DrainToBucketComponent DRAIN_COMPONENT;
	public FluidStack LAST_CONTAINED_FLUID;
	
	public TileEntityFluidInfuser() {
		initializeBaseMachineWithTank(2, 1000, 50000, 80, 100, 1, 2, 2, 10000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", SLOTS_INPUT, 1, SLOTS_OUTPUT, 1, this, TANK, FLUID_TO_CONTAINER_RATE);
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
			if(getResult(itemStack) != ItemStack.EMPTY && TANK.getFluidAmount() >= InfuserRecipeRegistry.Infusing().getInfusingFluidCost(SLOTS_INPUT.getStackInSlot(0), TANK.getFluid()) &&
					STORAGE.getEnergyStored() >= getProcessingEnergy(itemStack) && canSlotAcceptItemstack(getResult(itemStack), SLOTS_OUTPUT.getStackInSlot(0))) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int getProcessingCost() {
		if(SLOTS_INPUT.getStackInSlot(0) != ItemStack.EMPTY) {
			return getProcessingEnergy(SLOTS_INPUT.getStackInSlot(0));
		}else if(SLOTS_INTERNAL.getStackInSlot(0) != ItemStack.EMPTY){
			return getProcessingEnergy(SLOTS_INTERNAL.getStackInSlot(0));
		}
		return 0;
	}
	@Override
	public int getProcessingEnergy(ItemStack itemStack) {
		if(getResult(itemStack) != ItemStack.EMPTY) {
			return InfuserRecipeRegistry.Infusing().getInfusingFluidCost(itemStack, TANK.getFluid())*5*PROCESSING_ENERGY_MULT;
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
		if(SLOTS_INTERNAL.getStackInSlot(0) == ItemStack.EMPTY){
			PROCESSING_TIMER = 0;
		}
		if(!isProcessing() && !isMoving() && !isTankEmpty() && hasPower() && hasResult(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && !isTankEmpty() && hasPower() && hasResult(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER++;
			if(MOVE_TIMER >= MOVE_SPEED) {
				MOVE_TIMER = 0;
				if(!getWorld().isRemote) {
					TANK.drain(InfuserRecipeRegistry.Infusing().getInfusingFluidCost(SLOTS_INPUT.getStackInSlot(0), TANK.getFluid()), true);
					useEnergy(getProcessingEnergy(SLOTS_INPUT.getStackInSlot(0)));
				}
				moveItem(SLOTS_INPUT, 0, SLOTS_INTERNAL, 0);
				PROCESSING_TIMER = 1;
				
			}
		}else{
			MOVE_TIMER = 0;
		}
		if(isProcessing() && !isMoving()) {
			if(PROCESSING_TIMER < PROCESSING_TIME) {
				PROCESSING_TIMER++;
			}else{
				if(InventoryUtilities.canFullyInsertItemIntoSlot(SLOTS_OUTPUT, 0, getResult(SLOTS_INTERNAL.getStackInSlot(0)))) {
					if(SLOTS_INTERNAL.getStackInSlot(0) != ItemStack.EMPTY && getResult(SLOTS_INTERNAL.getStackInSlot(0)) != ItemStack.EMPTY) { //Weird Bug when Serializing
						SLOTS_OUTPUT.insertItem(0, getResult(SLOTS_INTERNAL.getStackInSlot(0)).copy(), false);
					}
					SLOTS_INTERNAL.setStackInSlot(0, ItemStack.EMPTY);
					PROCESSING_TIMER = 0;
					updateBlock();
				}
			}
		}	
		
	}
}


	
