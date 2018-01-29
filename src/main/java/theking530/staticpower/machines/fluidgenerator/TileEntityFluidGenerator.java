package theking530.staticpower.machines.fluidgenerator;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.energy.PowerDistributor;
import theking530.staticpower.handlers.crafting.registries.FluidGeneratorRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent;
import theking530.staticpower.machines.tileentitycomponents.DrainToBucketComponent.FluidContainerInteractionMode;

public class TileEntityFluidGenerator extends BaseMachineWithTank{

	private PowerDistributor energyDistributor;
	public int soundTimer = 15;
	private FluidStack processingFluid;
	public DrainToBucketComponent fluidContainerComponent;
	
	public TileEntityFluidGenerator() {
		initializeBaseMachineWithTank(1, 0, 50000, 480, 0, 0, 1, 1, 10000);
		fluidContainerComponent = new DrainToBucketComponent("BucketDrain", slotsInput, 0, slotsOutput, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		fluidContainerComponent.setMode(FluidContainerInteractionMode.FillFromContainer);
		energyDistributor = new PowerDistributor(this, energyStorage);
		moveSpeed = 10;
	}
	
	//Internal TANK			
	public boolean hasFuel() {
		if (TANK.getFluidAmount() > 0) {
			return true;
		}
		return false;
	}		
	public float getAdjustedVolume() {
		float amount = TANK.getFluidAmount();
		float capacity = TANK.getCapacity();
		float volume = (amount/capacity)*0.8F;		
		return volume;	
	}

	//Functionality		
	@Override
	public String getName() {
		return "Fluid Generator";	
	}	
	public void process() {
		if(!getWorld().isRemote) {
			if(!isTankEmpty() && !isProcessing() && getFluidRFOutput(TANK.getFluid()) > 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
				processingTimer = 1;
				processingFluid = TANK.getFluid();
				TANK.drain(1, true);
			}
			if(isProcessing() && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() && processingFluid != null) {
				energyStorage.receiveEnergy(getFluidRFOutput(processingFluid), false);
				processingTimer = 0;
				updateBlock();
			}
			energyDistributor.distributePower();	
			fluidContainerComponent.update();
		}
	}
	public int getFluidRFOutput(FluidStack fluid) {
		if(fluid != null) {
			return FluidGeneratorRecipeRegistry.Generating().getPowerOutput(fluid);
		}
		return 0;
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;	
	}
}
		

	


	
