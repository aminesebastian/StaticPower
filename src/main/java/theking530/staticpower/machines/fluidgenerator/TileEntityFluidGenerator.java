package theking530.staticpower.machines.fluidgenerator;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.energy.PowerDistributor;
import theking530.staticpower.handlers.crafting.registries.FluidGeneratorRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent.FluidContainerInteractionMode;

public class TileEntityFluidGenerator extends BaseMachineWithTank{

	private PowerDistributor energyDistributor;
	public int soundTimer = 15;
	private FluidStack processingFluid;
	public BucketInteractionComponent fluidContainerComponent;
	
	public TileEntityFluidGenerator() {
		initializeBasicMachine(1, 0, 50000, 480, 0);
		initializeTank(5000);
		initializeSlots(0, 1, 1);
		
		fluidContainerComponent = new BucketInteractionComponent("BucketDrain", slotsInput, 0, slotsOutput, 0, this, fluidTank, fluidToContainerRate);
		fluidContainerComponent.setMode(FluidContainerInteractionMode.FillFromContainer);
		energyDistributor = new PowerDistributor(this, energyStorage);
		moveSpeed = 10;
	}
	
	//Internal TANK			
	public boolean hasFuel() {
		if (fluidTank.getFluidAmount() > 0) {
			return true;
		}
		return false;
	}		
	public float getAdjustedVolume() {
		float amount = fluidTank.getFluidAmount();
		float capacity = fluidTank.getCapacity();
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
			if(!isTankEmpty() && !isProcessing() && getFluidRFOutput(fluidTank.getFluid()) > 0 && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
				processingTimer = 1;
				processingFluid = fluidTank.getFluid();
				fluidTank.drain(1, true);
			}
			if(isProcessing() && energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored() && processingFluid != null) {
				energyStorage.receiveEnergy(getFluidRFOutput(processingFluid), false);
				processingTimer = 0;
			}
			energyDistributor.distributePower();	
			fluidContainerComponent.preProcessUpdate();
			updateBlock();
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
		

	


	
