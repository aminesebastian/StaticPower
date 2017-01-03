package theking530.staticpower.machines.condenser;

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

public class TileEntityCondenser extends BaseMachineWithTank  {

	private PowerDistributor POWER_DIST;
	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	
	public TileEntityCondenser() {
		initializeBaseMachineWithTank(0, 0, 0, 0, 100, 0, 2, 0, 5000);
		TANK2 = new FluidTank(5000);
	}
	@Override
	public String getName() {
		return "Condenser";	
	}	
	public void process() {
		if(!worldObj.isRemote) {
			if(!isProcessing() && PROCESSING_STACK == null && TANK.getFluid() != null && TANK.getFluidAmount() > 0 && TANK2.getFluidAmount() + 100 <= TANK2.getCapacity()) {
				PROCESSING_STACK = TANK.drain(Math.min(TANK.getFluidAmount(), 100), true);
				PROCESSING_TIMER++;
			}
			if(isProcessing()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					PROCESSING_TIMER++;
				}else{
					if(PROCESSING_STACK != null) {
						TANK2.fill(new FluidStack(ModFluids.Ethanol, PROCESSING_STACK.amount), true);
						PROCESSING_STACK = null;
						PROCESSING_TIMER = 0;		
					}
				}
			}
		}
	}
	@Override
	public void readFromSyncNBT(NBTTagCompound nbt) {
		super.readFromSyncNBT(nbt);
        FluidStack tempStack = null;

        TANK2.setFluid(tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2")));
        PROCESSING_STACK = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFLUID"));
	}
	@Override
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
        if(TANK2.getFluid() != null) {
            NBTTagCompound fTag = new NBTTagCompound();
            TANK2.getFluid().writeToNBT(fTag);
            nbt.setTag("TANK2", fTag);    	
        }
        if(PROCESSING_STACK != null) {
        	NBTTagCompound pTag = new NBTTagCompound();
        	PROCESSING_STACK.writeToNBT(pTag);
        	nbt.setTag("PFLUID", pTag);
        }
		return nbt;
	}	
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        FluidStack tempStack = null;
        
        TANK2.setFluid(tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2")));
        PROCESSING_STACK = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFLUID"));
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(TANK2.getFluid() != null) {
            NBTTagCompound fTag = new NBTTagCompound();
            TANK2.getFluid().writeToNBT(fTag);
            nbt.setTag("TANK2", fTag);    	
        }
        if(PROCESSING_STACK != null) {
        	NBTTagCompound pTag = new NBTTagCompound();
        	PROCESSING_STACK.writeToNBT(pTag);
        	nbt.setTag("PFLUID", pTag);
        }
        return nbt;
	}	
	public void onMachinePlaced(NBTTagCompound nbt) {
		super.onMachinePlaced(nbt);
        FluidStack tempStack = null;
        tempStack = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2"));
        TANK2.setFluid(tempStack);
        tempStack = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFluid"));
        PROCESSING_STACK = tempStack;
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
	public int fill(FluidStack resource, boolean doFill) {
		if(!worldObj.isRemote) {
			updateBlock();
		}
		if(resource.getFluid() == ModFluids.EvaporatedMash) {
			return TANK.fill(resource, doFill);	
		}
		return 0;
	}
	@Override	
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(!worldObj.isRemote) {
			updateBlock();
		}
		return TANK2.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(!worldObj.isRemote) {
			updateBlock();
		}
		return TANK2.drain(maxDrain, doDrain);
	}
}
		

	


	
