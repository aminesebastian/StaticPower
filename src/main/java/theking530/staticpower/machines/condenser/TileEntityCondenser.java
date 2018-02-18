package theking530.staticpower.machines.condenser;

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
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent;
import theking530.staticpower.machines.tileentitycomponents.FluidContainerComponent.FluidContainerInteractionMode;

public class TileEntityCondenser extends BaseMachineWithTank  {

	public FluidTank TANK2;
	
	public FluidStack PROCESSING_STACK;
	public FluidContainerComponent DRAIN_COMPONENT_EVAPORATED_MASH;
	public FluidContainerComponent DRAIN_COMPONENT_ETHANOL;

	public TileEntityCondenser() {
		initializeBasicMachine(0, 0, 0, 0, 20);
		initializeTank(5000);
		initializeSlots(0, 2, 2);
		
		TANK2 = new FluidTank(5000);
		
		DRAIN_COMPONENT_EVAPORATED_MASH = new FluidContainerComponent("LeftBucketDrain", slotsInput, 0, slotsOutput, 0, this, fluidTank, fluidToContainerRate);
		DRAIN_COMPONENT_EVAPORATED_MASH.setMode(FluidContainerInteractionMode.FillFromContainer);
		DRAIN_COMPONENT_ETHANOL = new FluidContainerComponent("RightBucketDrain", slotsInput, 1, slotsOutput, 1, this, TANK2, fluidToContainerRate);	
	}
	
	@Override
	public String getName() {
		return "Condenser";	
	}	
	public void process() {
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT_EVAPORATED_MASH.preProcessUpdate();
			DRAIN_COMPONENT_ETHANOL.preProcessUpdate();
			if(!isProcessing() && PROCESSING_STACK == null && canProcess()) {
				PROCESSING_STACK = fluidTank.drain(getInputFluidAmount(), true);
				processingTime = Math.max(getOutputCondensingTime(), 0);
				processingTimer++;
			}
			if(isProcessing()) {
				if(processingTimer < processingTime) {
					processingTimer++;
				}else{
					if(PROCESSING_STACK != null) {
						TANK2.fill(getOutputFluid(), true);
						PROCESSING_STACK = null;
						processingTimer = 0;		
						updateBlock();
					}
				}
			}
		}
	}
	public boolean canProcess() {
		if(hasOutput()) {
			if(fluidTank.getFluid().amount >= getInputFluidAmount()) {
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
		if(fluidTank.getFluid() != null) {
			return CondenserRecipeRegistry.Condensing().getFluidInputAmount(fluidTank.getFluid());
		}
		return 0;
	}
	public FluidStack getOutputFluid() {
		if(PROCESSING_STACK != null) {
			return CondenserRecipeRegistry.Condensing().getFluidOutput(PROCESSING_STACK);
		}else if(fluidTank.getFluid() != null) {
			return CondenserRecipeRegistry.Condensing().getFluidOutput(fluidTank.getFluid());
		}
		return null;
	}
	public int getOutputCondensingTime() {
		if(fluidTank.getFluid() != null) {
			return CondenserRecipeRegistry.Condensing().getCondensingTime(fluidTank.getFluid());
		}
		return 0;
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
	public void deserializeOnPlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)  {
		super.deserializeOnPlaced(nbt, world, pos, state, placer, stack);
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
	public int fill(FluidStack resource, boolean doFill, EnumFacing facing) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		if(CondenserRecipeRegistry.Condensing().getFluidOutput(resource) != null){
			return fluidTank.fill(resource, doFill);	
		}
		return 0;
	}
	@Override	
	public FluidStack drain(FluidStack resource, boolean doDrain, EnumFacing facing) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return TANK2.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain, EnumFacing facing) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return TANK2.drain(maxDrain, doDrain);
	}
}
		

	


	
