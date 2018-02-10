package theking530.staticpower.machines.distillery;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import theking530.staticpower.handlers.crafting.registries.CondenserRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.DistilleryRecipeRegistry;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.condenser.TileEntityCondenser;
import theking530.staticpower.machines.heatingelement.HeatStorage;
import theking530.staticpower.machines.heatingelement.IHeatable;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent;
import theking530.staticpower.machines.tileentitycomponents.BucketInteractionComponent.FluidContainerInteractionMode;

public class TileEntityDistillery extends BaseMachineWithTank implements IHeatable{

	public HeatStorage HEAT_STORAGE;
	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	public BucketInteractionComponent DRAIN_COMPONENT_EVAPORATED_MASH;
	public BucketInteractionComponent DRAIN_COMPONENT_MASH;
	
	public TileEntityDistillery() {
		initializeBasicMachine(0, 0, 0, 0, 2);
		initializeTank(5000);
		initializeSlots(0, 2, 2);
		
		HEAT_STORAGE = new HeatStorage(150);
		TANK2 = new FluidTank(5000);
		
		DRAIN_COMPONENT_MASH = new BucketInteractionComponent("LeftBucketDrain", slotsInput, 0, slotsOutput, 0, this, fluidTank, fluidToContainerRate);
		DRAIN_COMPONENT_MASH.setMode(FluidContainerInteractionMode.FillFromContainer);
		DRAIN_COMPONENT_EVAPORATED_MASH = new BucketInteractionComponent("RightBucketDrain", slotsInput, 1, slotsOutput, 1, this, TANK2, fluidToContainerRate);
	}
	@Override
	public String getName() {
		return "Distillery";	
	}	
	public void process() {
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT_EVAPORATED_MASH.preProcessUpdate();
			DRAIN_COMPONENT_MASH.preProcessUpdate();
			if(!isProcessing() && canProcess() && PROCESSING_STACK == null) {
				PROCESSING_STACK = fluidTank.drain(getInputFluidAmount(), true);
				processingTimer++;
			}
			if(isProcessing()) {
				if(processingTimer < processingTime) {
					processingTimer++;
				}else{
					if(PROCESSING_STACK != null) {
						processingTimer = 0;
						TANK2.fill(getOutputFluid(), true);
						PROCESSING_STACK = null;	
						updateBlock();
					}
				}
			}
			if(TANK2.getFluid() != null) {
				if(CondenserRecipeRegistry.Condensing().getFluidOutput(TANK2.getFluid()) != null) {
					if(getWorld().getTileEntity(pos.offset(EnumFacing.UP)) instanceof TileEntityCondenser) {
						TileEntityCondenser te = (TileEntityCondenser) getWorld().getTileEntity(pos.offset(EnumFacing.UP));
						TANK2.drain(te.fill(TANK2.getFluid(), true), true);
					}	
				}
			}
		}
	}
	public boolean canProcess() {
		if(hasOutput()) {
			if(fluidTank.getFluid().amount >= getInputFluidAmount()) {
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
		if(fluidTank.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getFluidInputAmount(fluidTank.getFluid(), getHeat());
		}
		return 0;
	}
	public FluidStack getOutputFluid() {
		if(fluidTank.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getFluidOutput(fluidTank.getFluid(), getHeat());
		}
		return null;
	}
	public int getOutputHeatCost() {
		if(fluidTank.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getHeatCost(fluidTank.getFluid(), getHeat());
		}
		return 0;
	}
	public int getOutputMinHeat() {
		if(fluidTank.getFluid() != null) {
			return DistilleryRecipeRegistry.Distillery().getHeatMin(fluidTank.getFluid(), getHeat());
		}
		return 0;
	}
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        HEAT_STORAGE.readFromNBT(nbt);

        FluidStack tempStack = null;
        tempStack = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("TANK2"));
        TANK2.setFluid(tempStack);
        tempStack = FluidStack.loadFluidStackFromNBT((NBTTagCompound) nbt.getTag("PFluid"));
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
	public void deserializeOnPlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)  {
		super.deserializeOnPlaced(nbt, world, pos, state, placer, stack);
		HEAT_STORAGE.readFromNBT(nbt);

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
		if(!getWorld().isRemote) {
			updateBlock();
		}
		if(DistilleryRecipeRegistry.Distillery().getFluidOutput(resource, 10000000) != null){
			return fluidTank.fill(resource, doFill);	
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
		

	


	
