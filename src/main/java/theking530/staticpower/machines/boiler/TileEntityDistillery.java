package theking530.staticpower.machines.boiler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.heatingelement.HeatStorage;
import theking530.staticpower.machines.heatingelement.IHeatable;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityDistillery extends BaseMachineWithTank implements IHeatable{

	private PowerDistributor POWER_DIST;
	public HeatStorage HEAT_STORAGE;
	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	
	public TileEntityDistillery() {
		initializeBaseMachineWithTank(0, 0, 0, 0, 20, 0, 0, 0, 10000);
		HEAT_STORAGE = new HeatStorage(150);
		TANK2 = new FluidTank(10000);
	}
	@Override
	public String getName() {
		return "Distillery";	
	}	
	public void process() {
		if(!worldObj.isRemote) {
			if(!isProcessing() && TANK2.getFluidAmount() + 100 <= TANK2.getCapacity() && HEAT_STORAGE.getHeat() >= 100 
					&& TANK.getFluid().isFluidEqual(new FluidStack(ModFluids.Mash, 1)) && PROCESSING_STACK == null) {
				PROCESSING_STACK = TANK.drain(Math.max(TANK.getFluidAmount(), 100), true);
			}
			if(isProcessing()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					PROCESSING_TIMER++;
					HEAT_STORAGE.extractHeat(10);
				}else{
					PROCESSING_TIMER = 0;
					TANK2.fill(new FluidStack(ModFluids.EvaporatedMash, 100), true);
					PROCESSING_STACK = null;
				}
			}
		}
	}
	public void onMachinePlaced(NBTTagCompound nbt) {
		super.onMachinePlaced(nbt);
        TANK.readFromNBT(nbt);
	}	
   
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        TANK.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		TANK.writeToNBT(nbt);
    	return nbt;
	}

	@Override
	public void readFromSyncNBT(NBTTagCompound nbt) {
		super.readFromSyncNBT(nbt);
		TANK.readFromNBT(nbt);
	}
	@Override
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		TANK.writeToNBT(nbt);
		return nbt;
	}
    
	@Override
	public int receiveEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;	
	}
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return false;
	}
	@Override
	public int getHeat() {
		return HEAT_STORAGE.getHeat();
	}
	@Override
	public int recieveHeat(int heat) {
		return HEAT_STORAGE.recieveHeat(heat);
	}
	@Override
	public boolean canHeat() {
		return HEAT_STORAGE.canHeat();
	}
	@Override
	public int extractHeat(int heat) {
		return HEAT_STORAGE.extractHeat(heat);
	}
	@Override
	public int getMaxHeat() {
		return HEAT_STORAGE.getMaxHeat();
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(!worldObj.isRemote) {
			sync();
		}
		return TANK2.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(!worldObj.isRemote) {
			sync();
		}
		return TANK2.drain(maxDrain, doDrain);
	}
}
		

	


	
