package theking530.staticpower.machines.mechanicalsqueezer;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;
import theking530.staticpower.machines.machinecomponents.DrainToBucketComponent;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityMechanicalSqueezer extends BaseTileEntity implements IFluidHandler{

	public FluidTank TANK;
	public int PROCESSING_TIMER = 0;
	public int PROCESSING_TIME = 20;
	public int MOVE_TIMER = 0;
	public int MOVE_SPEED = 4;
	public int FLUID_TO_CONTAINER_RATE = 10; //1 Bucket
	
	public DrainToBucketComponent DRAIN_COMPONENT;

	public TileEntityMechanicalSqueezer() {
		initializeBasicTileEntity(1, 2, 2);
		TANK = new FluidTank(1000);
		DRAIN_COMPONENT = new DrainToBucketComponent("BucketDrain", slotsInput, 1, slotsOutput, 1, this, TANK, FLUID_TO_CONTAINER_RATE);
	}
	@Override
	public String getName() {
		return "Mechanical Squeezer";		
	}		
	
	public void readFromSyncNBT(NBTTagCompound nbt) {
        TANK.readFromNBT(nbt);
		PROCESSING_TIMER = nbt.getInteger("P_TIMER");
	}
	public NBTTagCompound writeToSyncNBT(NBTTagCompound nbt) {
		TANK.writeToNBT(nbt);
		nbt.setInteger("P_TIMER", PROCESSING_TIMER);
		return nbt;
	}
	
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        TANK.readFromNBT(nbt);
		PROCESSING_TIMER = nbt.getInteger("P_TIMER");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
		TANK.writeToNBT(nbt);
		nbt.setInteger("P_TIMER", PROCESSING_TIMER);
    	return nbt;
	}
   
	public NBTTagCompound onMachineBroken(NBTTagCompound nbt) {
		writeToNBT(nbt);
    	return nbt;
	}
	public void onMachinePlaced(NBTTagCompound nbt) {
		readFromNBT(nbt);
	}	
	
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public final NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.getPos(), 1, this.getUpdateTag());
	}
	
    //Process
	public ItemStack getResult(ItemStack itemStack) {
		return SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemStack);
	}
	public boolean hasResult(ItemStack itemstack) {
		if(itemstack != null && getResult(itemstack) != ItemStack.EMPTY) {
			return true;
		}
		return false;
	}
	public FluidStack getFluidResult(ItemStack itemStack) {
    	if(itemStack != null) {
    		return SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemStack);
    	}
    	return null;
    }
	public boolean canProcess(ItemStack itemstack) {
		FluidStack fluidstack = SqueezerRecipeRegistry.Squeezing().getSqueezingFluidResult(itemstack);
		if(hasResult(itemstack) && fluidstack != null && InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(itemstack))) {
			if(fluidstack.amount + TANK.getFluidAmount() > TANK.getCapacity()) {
				return false;
			}
			if (TANK.getFluid() != null && !fluidstack.isFluidEqual(TANK.getFluid())) {
				return false;
			}
			if(TANK.getFluid() == null) {
				return true;
			}
			if(TANK.getFluidAmount() + fluidstack.amount <= TANK.getCapacity()) {
				return true;
			}
			if(TANK.getFluidAmount() + fluidstack.amount > TANK.getCapacity()) {
				return false;
			}
			if (TANK.getFluid() != null && fluidstack.isFluidEqual(TANK.getFluid())) {
				return true;
			}				
		}
		return false;
	}
	public boolean isProcessing(){
		return PROCESSING_TIMER > 0;
	}
	public boolean isMoving(){
		return MOVE_TIMER > 0;
	}
	public int getProgressScaled(int i) {
		float ratio = (float)PROCESSING_TIMER/(float)PROCESSING_TIME;
		return (int) (ratio * i);
	}
	@Override
	public void process(){
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT.update();
		}
	}
	public void rightClick() {
		if(!getWorld().isRemote) {
			DRAIN_COMPONENT.update();
			if(slotsInternal.getStackInSlot(0) == ItemStack.EMPTY){
				PROCESSING_TIMER = 0;
			}
			//Start Process
			if(!isProcessing() && !isMoving() && canProcess(slotsInput.getStackInSlot(0))) {
				MOVE_TIMER = 1;
			}
			//Start Moving
			if(!isProcessing() && isMoving() && canProcess(slotsInput.getStackInSlot(0))) {
				MOVE_TIMER++;
				if(MOVE_TIMER >= MOVE_SPEED) {
					MOVE_TIMER = 0;
					moveItem(slotsInput, 0, slotsInternal, 0);
					PROCESSING_TIMER = 1;	
				}
			}else{
				MOVE_TIMER = 0;
			}
			//Start Processing
			if(isProcessing() && !isMoving() && canProcess(slotsInternal.getStackInSlot(0))) {
				if(PROCESSING_TIMER < PROCESSING_TIME) {
					PROCESSING_TIMER++;
					updateBlock();
					getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_SAND_STEP, SoundCategory.BLOCKS, 0.15f, 1, false);		
				}else{				
					if(InventoryUtilities.canFullyInsertItemIntoSlot(slotsOutput, 0, getResult(slotsInternal.getStackInSlot(0)))) {
						TANK.fill(getFluidResult(slotsInternal.getStackInSlot(0)), true);
						slotsOutput.insertItem(0, getResult(slotsInternal.getStackInSlot(0)).copy(), false);
						slotsInternal.setStackInSlot(0, ItemStack.EMPTY);
						PROCESSING_TIMER = 0;					
					}
				}
			}	
		}
	}
	
	//FLUID
	public float getFluidLevelScaled(int height) {
		int capacity = TANK.getCapacity();
		int volume = TANK.getFluidAmount();
		if(capacity != 0) {
			float percentage = (float)volume/(float)capacity;
			return percentage * height;
		}else{
			return 0;
		}
	}
    @SuppressWarnings("unchecked")
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
    		return (T) this;
    	}
    	return super.getCapability(capability, facing);
    }
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return TANK.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return TANK.fill(resource, doFill);
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return TANK.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return TANK.drain(maxDrain, doDrain);
	}
}
	
