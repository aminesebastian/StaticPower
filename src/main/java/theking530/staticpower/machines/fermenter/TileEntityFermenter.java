package theking530.staticpower.machines.fermenter;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent;

public class TileEntityFermenter extends BaseMachineWithTank {

	public DrainToBucketComponent DRAIN_COMPONENT;
	
	public TileEntityFermenter() {
		initializeBaseMachineWithTank(4, 500, 100000, 160, 45, 1, 11, 2, 5000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", slotsInput, 10, slotsOutput, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		setBatterySlot(10);
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
	@Override 
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
			if(fluidstack.amount + TANK.getFluidAmount() > TANK.getCapacity()) {
				return false;
			}
			if(energyStorage.getEnergyStored() < getProcessingCost()) {
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
			if (TANK.getFluid() != null && fluidstack.isFluidEqual(TANK.getFluid())) {
				return true;
			}				
		}
		return false;
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
			DRAIN_COMPONENT.update();
			if(!isProcessing() && !isMoving()) {
				for(int i=0; i<9; i++) {
					if(slotsInput.getStackInSlot(i) != ItemStack.EMPTY && canProcess(slotsInput.getStackInSlot(i))) {
						moveItem(slotsInput, i, slotsInternal, 0);
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
						TANK.fill(getFermentingResult(slotsInternal.getStackInSlot(0)), true);
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


	
