package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityFluidInfuser extends BaseMachineWithTank {
	
	public TileEntityFluidInfuser() {
		initializeBaseMachineWithTank(2, 1000, 50000, 80, 100, 1, 1, 1, 10000);
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
			if(getResult(itemStack) != null && TANK.getFluidAmount() >= InfuserRecipeRegistry.Infusing().getInfusingFluidCost(SLOTS_INPUT.getStackInSlot(0), TANK.getFluid()) &&
					STORAGE.getEnergyStored() >= getProcessingEnergy(itemStack) && canSlotAcceptItemstack(getResult(itemStack), SLOTS_OUTPUT.getStackInSlot(0))) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int getProcessingCost() {
		if(SLOTS_INPUT.getStackInSlot(0) != null) {
			return getProcessingEnergy(SLOTS_INPUT.getStackInSlot(0));
		}else if(SLOTS_INTERNAL.getStackInSlot(0) != null){
			return getProcessingEnergy(SLOTS_INTERNAL.getStackInSlot(0));
		}
		return 0;
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
		if(SLOTS_INTERNAL.getStackInSlot(0) == null){
			PROCESSING_TIMER = 0;
		}
		if(!isProcessing() && !isMoving() && !isTankEmpty() && hasPower() && hasResult(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER = 1;
		}
		if(!isProcessing() && isMoving() && !isTankEmpty() && hasPower() && hasResult(SLOTS_INPUT.getStackInSlot(0))) {
			MOVE_TIMER++;
			if(MOVE_TIMER >= MOVE_SPEED) {
				MOVE_TIMER = 0;
				TANK.drain(InfuserRecipeRegistry.Infusing().getInfusingFluidCost(SLOTS_INPUT.getStackInSlot(0), TANK.getFluid()), true);
				useEnergy(getProcessingEnergy(SLOTS_INPUT.getStackInSlot(0)));
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
					SLOTS_OUTPUT.insertItem(0, getResult(SLOTS_INTERNAL.getStackInSlot(0)).copy(), false);
					SLOTS_INTERNAL.setStackInSlot(0, null);
					PROCESSING_TIMER = 0;
				}
			}
		}	
	}
}


	
