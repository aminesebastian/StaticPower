package theking530.staticpower.machines.distillery;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.condenser.TileEntityCondenser;
import theking530.staticpower.machines.heatingelement.HeatStorage;
import theking530.staticpower.machines.heatingelement.IHeatable;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityDistillery extends BaseMachineWithTank implements IHeatable{

	private PowerDistributor POWER_DIST;
	public HeatStorage HEAT_STORAGE;
	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	
	public TileEntityDistillery() {
		initializeBaseMachineWithTank(0, 0, 0, 0, 50, 0, 2, 0, 5000);
		HEAT_STORAGE = new HeatStorage(150);
		TANK2 = new FluidTank(5000);
	}
	@Override
	public String getName() {
		return "Distillery";	
	}	
	public void process() {
		if(!worldObj.isRemote) {
			if(!isProcessing() && TANK2.getFluidAmount() + 50 <= TANK2.getCapacity() && HEAT_STORAGE.getHeat() >= 100 
					&& TANK.getFluid() != null && TANK.getFluid().isFluidEqual(new FluidStack(ModFluids.Mash, 1)) && PROCESSING_STACK == null) {
				PROCESSING_STACK = TANK.drain(Math.min(TANK.getFluidAmount(), 50), true);
				PROCESSING_TIMER++;
			}
			if(isProcessing()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					PROCESSING_TIMER++;
				}else{
					if(PROCESSING_STACK != null) {
						PROCESSING_TIMER = 0;
						HEAT_STORAGE.extractHeat(1);
						TANK2.fill(new FluidStack(ModFluids.EvaporatedMash, PROCESSING_STACK.amount), true);
						PROCESSING_STACK = null;		
					}else{
						//PROCESSING_TIMER = 0;
					}
				}
			}
			if(TANK2.getFluid() != null) {
				if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) instanceof TileEntityCondenser) {
					TileEntityCondenser te = (TileEntityCondenser) worldObj.getTileEntity(pos.offset(EnumFacing.UP));
					TANK2.drain(te.fill(TANK2.getFluid(), true), true);
				}	
			}
		}
	}
	@Override
	public void readFromSyncNBT(NBTTagCompound nbt) {
		super.readFromSyncNBT(nbt);
        HEAT_STORAGE.readFromNBT(nbt);
        
        FluidStack tempStack = null;
        tempStack = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2"));
        TANK2.setFluid(tempStack);
        tempStack = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFLUID"));
        PROCESSING_STACK = tempStack;
	}
	@Override
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
        HEAT_STORAGE.writeToNBT(nbt);
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
        HEAT_STORAGE.readFromNBT(nbt);

        FluidStack tempStack = null;
        tempStack = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2"));
        TANK2.setFluid(tempStack);
        tempStack = tempStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFluid"));
        PROCESSING_STACK = tempStack;
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        HEAT_STORAGE.writeToNBT(nbt);
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
		HEAT_STORAGE.readFromNBT(nbt);

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
	public int getHeat() {
		return HEAT_STORAGE.getHeat();
	}
	@Override
	public int recieveHeat(int heat) {
		return HEAT_STORAGE.recieveHeat(heat);
	}
	@Override
	public boolean canHeat() {
		return true;
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
	public int fill(FluidStack resource, boolean doFill) {
		if(!worldObj.isRemote) {
			sync();
		}
		if(resource.getFluid() == ModFluids.Mash) {
			return TANK.fill(resource, doFill);	
		}
		System.out.println("HI");
		return 0;
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
		

	


	
