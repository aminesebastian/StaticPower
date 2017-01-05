package theking530.staticpower.machines.distillery;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.handlers.crafting.registries.CondenserRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.DistilleryRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.condenser.TileEntityCondenser;
import theking530.staticpower.machines.heatingelement.HeatStorage;
import theking530.staticpower.machines.heatingelement.IHeatable;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent.FluidContainerInteractionMode;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityDistillery extends BaseMachineWithTank implements IHeatable{

	private PowerDistributor POWER_DIST;
	public HeatStorage HEAT_STORAGE;
	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	public DrainToBucketComponent DRAIN_COMPONENT_EVAPORATED_MASH;
	public DrainToBucketComponent DRAIN_COMPONENT_MASH;
	
	public TileEntityDistillery() {
		initializeBaseMachineWithTank(0, 0, 0, 0, 2, 0, 2, 2, 5000);
		HEAT_STORAGE = new HeatStorage(150);
		TANK2 = new FluidTank(5000);
		
		DRAIN_COMPONENT_MASH = new DrainToBucketComponent("LeftBucketDrain", SLOTS_INPUT, 0, SLOTS_OUTPUT, 0, this, TANK, FLUID_TO_CONTAINER_RATE);
		DRAIN_COMPONENT_MASH.setMode(FluidContainerInteractionMode.FillFromContainer);
		DRAIN_COMPONENT_EVAPORATED_MASH = new DrainToBucketComponent("RightBucketDrain", SLOTS_INPUT, 1, SLOTS_OUTPUT, 1, this, TANK2, FLUID_TO_CONTAINER_RATE);
	}
	@Override
	public String getName() {
		return "Distillery";	
	}	
	public void process() {
		if(!worldObj.isRemote) {
			DRAIN_COMPONENT_EVAPORATED_MASH.update();
			DRAIN_COMPONENT_MASH.update();
			if(!isProcessing() && canProcess() && PROCESSING_STACK == null) {
				PROCESSING_STACK = TANK.drain(getInputFluidAmount(), true);
				PROCESSING_TIMER++;
			}
			if(isProcessing()) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					PROCESSING_TIMER++;
					updateBlock();
				}else{
					if(PROCESSING_STACK != null) {
						PROCESSING_TIMER = 0;
						HEAT_STORAGE.extractHeat(1);
						TANK2.fill(getOutputFluid(), true);
						PROCESSING_STACK = null;	
						updateBlock();
					}
				}
			}
			if(TANK2.getFluid() != null) {
				if(CondenserRecipeRegistry.Condensing().getFluidOutput(TANK2.getFluid()) != null) {
					if(worldObj.getTileEntity(pos.offset(EnumFacing.UP)) instanceof TileEntityCondenser) {
						TileEntityCondenser te = (TileEntityCondenser) worldObj.getTileEntity(pos.offset(EnumFacing.UP));
						TANK2.drain(te.fill(TANK2.getFluid(), true), true);
					}	
				}
			}
		}
	}
	public boolean canProcess() {
		if(hasOutput()) {
			if(TANK.getFluid().amount >= getInputFluidAmount()) {
				if(getHeat() >= getOutputMinHeat()) {
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
		}
		return false;
	}
	public boolean hasOutput() {
		return getOutputFluid() != null;
	}
	public int getInputFluidAmount() {
		if(TANK.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getFluidInputAmount(TANK.getFluid(), getHeat());
		}
		return 0;
	}
	public FluidStack getOutputFluid() {
		if(TANK.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getFluidOutput(TANK.getFluid(), getHeat());
		}
		return null;
	}
	public int getOutputHeatCost() {
		if(TANK.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getHeatCost(TANK.getFluid(), getHeat());
		}
		return 0;
	}
	public int getOutputMinHeat() {
		if(TANK.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getHeatMin(TANK.getFluid(), getHeat());
		}
		return 0;
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
			updateBlock();
		}
		if(DistilleryRecipeRegistry.Distillery().getFluidOutput(resource, 10000000) != null){
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
		

	


	
