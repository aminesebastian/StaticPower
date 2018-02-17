package theking530.staticpower.machines.fermenter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.BatteryInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent;

public class TileEntityFermenter extends BaseMachineWithTank {

	public FluidContainerComponent DRAIN_COMPONENT;
	
	public TileEntityFermenter() {
		initializeBasicMachine(4, 500, 100000, 160, 45);
		initializeTank(5000);
		initializeSlots(4, 9, 1);
		
		DRAIN_COMPONENT = new FluidContainerComponent("BucketDrain", slotsInternal, 2, slotsInternal, 3, this, fluidTank, fluidToContainerRate);
		registerComponent(new BatteryInteractionComponent("BatteryComponent", slotsInternal, 1, this, energyStorage));

		//setFluidContainerSlot(9, FluidContainerMode.FILL);
	}
	@Override
	public String getName() {
		return "container.Fermenter";		
	}		
	
    //Process
	public FluidStack getFermentingResult(ItemStack itemStack) {
		return FermenterRecipeRegistry.Fermenting().getFluidResult(itemStack);
	}
	public boolean hasResult(ItemStack itemstack) {
		if(itemstack != ItemStack.EMPTY && getFermentingResult(itemstack) != null) {
			return true;
		}
		return false;
	}
	@Override
	public boolean canProcess(ItemStack itemstack) {
		if(itemstack == null) {
			return false;
		}
		FluidStack fluidstack = FermenterRecipeRegistry.Fermenting().getFluidResult(itemstack);
		if(hasResult(itemstack) && fluidstack != null) {
			if(fluidstack.amount + fluidTank.getFluidAmount() > fluidTank.getCapacity()) {
				return false;
			}
			if(energyStorage.getEnergyStored() < getProcessingCost()) {
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
			if (fluidTank.getFluid() != null && fluidstack.isFluidEqual(fluidTank.getFluid())) {
				return true;
			}				
		}
		return false;
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
			DRAIN_COMPONENT.preProcessUpdate();
			if(!isProcessing() && !isMoving()) {
				for(int i=0; i<9; i++) {
					if(slotsInput.getStackInSlot(i) != ItemStack.EMPTY && canProcess(slotsInput.getStackInSlot(i))) {
						transferItemInternally(slotsInput, i, slotsInternal, 0);
						moveTimer = 1;
						break;
					}
				}	
			}else{
				if(!isProcessing() && slotsInternal.getStackInSlot(0) != ItemStack.EMPTY) {
					processingTimer++;
				}
				if(isProcessing()) {
					if(processingTimer < processingTime) {
						processingTimer++;
						energyStorage.extractEnergy(getProcessingCost()/processingTime, false);
						updateBlock();
					}else{
						fluidTank.fill(getFermentingResult(slotsInternal.getStackInSlot(0)), true);
						slotsInternal.extractItem(0, slotsInternal.getStackInSlot(0).getCount(), false);
						processingTimer = 0;
						moveTimer = 0;
						markDirty();
						updateBlock();
					}
				}
			}			
		}
	}
}


	
