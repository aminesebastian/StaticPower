package theking530.staticpower.machines.fluidgenerator;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.handlers.crafting.registries.FluidGeneratorRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityFluidGenerator extends BaseMachineWithTank{

	private PowerDistributor POWER_DIST;
	public int SOUND_TIMER = 15;
	private FluidStack PROCESSING_FLUID;
	public DrainToBucketComponent DRAIN_COMPONENT;
	
	public TileEntityFluidGenerator() {
		initializeBaseMachineWithTank(1, 0, 50000, 480, 0, 0, 1, 1, 10000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", SLOTS_INPUT, 0, SLOTS_OUTPUT, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT.setMode(FluidContainerInteractionMode.FillFromContainer);
		POWER_DIST = new PowerDistributor(this, STORAGE);
		MOVE_SPEED = 10;
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
			if(!isTankEmpty() && !isProcessing() && getFluidRFOutput(TANK.getFluid()) > 0 && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored()) {
				PROCESSING_TIMER = 1;
				PROCESSING_FLUID = TANK.getFluid();
				TANK.drain(1, true);
			}
			if(isProcessing() && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored() && PROCESSING_FLUID != null) {
				STORAGE.receiveEnergy(getFluidRFOutput(PROCESSING_FLUID), false);
				PROCESSING_TIMER = 0;
				updateBlock();
			}
			POWER_DIST.distributePower();	
			DRAIN_COMPONENT.update();
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
		

	


	
