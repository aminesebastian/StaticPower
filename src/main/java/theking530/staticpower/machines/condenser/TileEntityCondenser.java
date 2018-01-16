package theking530.staticpower.machines.condenser;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import theking530.staticpower.handlers.crafting.registries.CondenserRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;

public class TileEntityCondenser extends BaseMachineWithTank  {

	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	public DrainToBucketComponent DRAIN_COMPONENT_EVAPORATED_MASH;
	public DrainToBucketComponent DRAIN_COMPONENT_ETHANOL;

	public TileEntityCondenser() {
		initializeBaseMachineWithTank(0, 0, 0, 0, 100, 0, 2, 2, 5000);
		TANK2 = new FluidTank(5000);
		
		DRAIN_COMPONENT_EVAPORATED_MASH = new DrainToBucketComponent("LeftBucketDrain", SLOTS_INPUT, 0, SLOTS_OUTPUT, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT_EVAPORATED_MASH.setMode(FluidContainerInteractionMode.FillFromContainer);
		DRAIN_COMPONENT_ETHANOL = new DrainToBucketComponent("RightBucketDrain", SLOTS_INPUT, 1, SLOTS_OUTPUT, 1, this, TANK2, FLUID_TO_CONTAINER_RATE);	
	}
	
	@Override
	public String getName() {
		return "Condenser";	
	}	
	public void process() {
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT_EVAPORATED_MASH.update();
			DRAIN_COMPONENT_ETHANOL.update();
			if(!isProcessing() && PROCESSING_STACK == null && canProcess()) {
				PROCESSING_STACK = TANK.drain(getInputFluidAmount(), true);
				PROCESSING_TIME = Math.max(getOutputCondensingTime(), 0);
				PROCESSING_TIMER++;
			}
			if(isProcessing()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					PROCESSING_TIMER++;
					updateBlock();
				}else{
					if(PROCESSING_STACK != null) {
						TANK2.fill(getOutputFluid(), true);
						PROCESSING_STACK = null;
						PROCESSING_TIMER = 0;		
						updateBlock();
					}
				}
			}
		}
	}
	public boolean canProcess() {
		if(hasOutput()) {
			if(TANK.getFluid().amount >= getInputFluidAmount()) {
				if(TANK2.getFluid() == null) {
					return true;
				}
				FluidStack tempOutput = getOutputFluid();
				if(TANK2.getFluid().isFluidEqual(tempOutput)) {
					if(TANK2.getFluid().amount + tempOutput.amount <= TANK2.getCapacity()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean hasOutput() {
		return getOutputFluid() != null;
	}
	public int getInputFluidAmount() {
		if(TANK.getFluid() != null) {
			return CondenserRecipeRegistry.Condensing().getFluidInputAmount(TANK.getFluid());
		}
		return 0;
	}
	public FluidStack getOutputFluid() {
		if(TANK.getFluid() != null) {
			return CondenserRecipeRegistry.Condensing().getFluidOutput(TANK.getFluid());
		}
		return null;
	}
	public int getOutputCondensingTime() {
		if(TANK.getFluid() != null) {
			return CondenserRecipeRegistry.Condensing().getCondensingTime(TANK.getFluid());
		}
		return 0;
	}
	@Override
	public void readFromSyncNBT(NBTTagCompound nbt) {
		super.readFromSyncNBT(nbt);
        TANK2.setFluid(FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2")));
        PROCESSING_STACK = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFLUID"));
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
        TANK2.setFluid(FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2")));
        PROCESSING_STACK = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFLUID"));
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
        tempStack = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2"));
        TANK2.setFluid(tempStack);
        tempStack = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFluid"));
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
		if(!getWorld().isRemote) {
			updateBlock();
		}
		if(CondenserRecipeRegistry.Condensing().getFluidOutput(resource) != null){
			return TANK.fill(resource, doFill);	
		}
		return 0;
	}
	@Override	
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return TANK2.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return TANK2.drain(maxDrain, doDrain);
	}
}
		

	


	
