package theking530.staticpower.machines.fluidgenerator;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.FluidContainerMode;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityFluidGenerator extends BaseMachineWithTank{

	private PowerDistributor POWER_DIST;
	public int SOUND_TIMER = 15;
	private FluidStack PROCESSING_FLUID;
			
	public TileEntityFluidGenerator() {
		initializeBaseMachineWithTank(1, 0, 50000, 480, 0, 0, 1, 0, 10000);
		setFluidContainerSlot(0, FluidContainerMode.DRAIN);
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
		if(!worldObj.isRemote) {
			if(!isTankEmpty() && !isProcessing() && getFluidRFOutput(TANK.getFluid()) > 0 && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored()) {
				PROCESSING_TIMER = 1;
				PROCESSING_FLUID = TANK.getFluid();
				TANK.drain(1, true);
			}
			if(isProcessing() && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored() && PROCESSING_FLUID != null) {
				STORAGE.receiveEnergy(getFluidRFOutput(PROCESSING_FLUID), false);
				PROCESSING_TIMER = 0;
				sync();
			}
			POWER_DIST.distributePower();		
		}
	}
	public int getFluidRFOutput(FluidStack fluid) {
		if(fluid != null) {
			if(fluid.getFluid() == ModFluids.StaticFluid) {
				return 128;
			}
			if(fluid.getFluid() == ModFluids.EnergizedFluid) {
				return 256;
			}
			if(fluid.getFluid() == ModFluids.LumumFluid) {
				return 512;
			}
			if(fluid.getFluid() == ModFluids.Ethanol) {
				return 240;
			}
			if(fluid.getFluid() == ModFluids.EvaporatedMash) {
				return 40;
			}
			if(fluid.getFluid() == ModFluids.Mash) {
				return 20;
			}
		}
		return 0;
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;	
	}
}
		

	


	
