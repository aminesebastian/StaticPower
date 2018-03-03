package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.assists.utilities.InventoryUtilities;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemOutputServo;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent.FluidContainerInteractionMode;

public class TileEntityFluidInfuser extends BaseMachineWithTank {
	
	public FluidContainerComponent DRAIN_COMPONENT;
	public FluidStack LAST_CONTAINED_FLUID;
	
	public TileEntityFluidInfuser() {
		initializeBasicMachine(2, 1000, 100000, 80, 100);
		initializeTank(10000);
		initializeSlots(4, 1, 1);
		
		registerComponent(new BatteryInteractionComponent("BatteryInteraction", slotsInput, 3, energyStorage));
		registerComponent(DRAIN_COMPONENT = new FluidContainerComponent("BucketDrain", slotsInternal, 1, slotsInternal, 2, this, fluidTank));
		DRAIN_COMPONENT.setMode(FluidContainerInteractionMode.FILL);
		
		registerComponent(new TileEntityItemOutputServo(this, 1, slotsOutput, 0));
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 0));
	}
	//IInventory				
	@Override
	public String getName() {
		return "Fluid Infuser";		
	}
	public FluidContainerComponent getFluidInteractionComponent() {
		return DRAIN_COMPONENT;
	}

	//Functionality
	@Override
 	public ItemStack getResult(ItemStack itemStack) {
		if(!itemStack.isEmpty()) {		
			if(LAST_CONTAINED_FLUID != null) {
				return InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemStack, LAST_CONTAINED_FLUID);
			}else{
				return InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemStack, fluidTank.getFluid());
			}
		}else{
			return ItemStack.EMPTY;
		}
	}
	public boolean hasResult(ItemStack itemStack) {
		if(itemStack != ItemStack.EMPTY) {
			if(getResult(itemStack) != ItemStack.EMPTY && fluidTank.getFluidAmount() >= InfuserRecipeRegistry.Infusing().getInfusingFluidCost(slotsInput.getStackInSlot(0), fluidTank.getFluid()) &&
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
			return (int) (InfuserRecipeRegistry.Infusing().getInfusingFluidCost(itemStack, fluidTank.getFluid())*5*processingEnergyMult);
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
					LAST_CONTAINED_FLUID = fluidTank.drain(InfuserRecipeRegistry.Infusing().getInfusingFluidCost(slotsInput.getStackInSlot(0), fluidTank.getFluid()), true);
					useEnergy(getProcessingEnergy(slotsInput.getStackInSlot(0)));
				}
				transferItemInternally(slotsInput, 0, slotsInternal, 0);
				processingTimer = 1;
				
			}
		}else{
			moveTimer = 0;
		}
		if(isProcessing() && !isMoving()) {
			if(processingTimer < processingTime) {
				processingTimer++;
			}else{
				ItemStack output = slotsOutput.insertItem(0, getResult(slotsInternal.getStackInSlot(0)).copy(), false);
				if(output.isEmpty()) {
					slotsInternal.setStackInSlot(0, ItemStack.EMPTY);
					processingTimer = 0;
					updateBlock();
				}
			}
		}		
	}
}


	
