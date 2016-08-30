package theking530.staticpower.machines;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.fluids.FluidDistributor;
import theking530.staticpower.handlers.PacketHandler;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;

public class BaseMachineWithTank extends BaseMachine implements IFluidHandler{

	public int INITIAL_TANK_CAPACITY;
	public FluidTank TANK;
	public int FLUID_CONTAINER_SLOT = -1;
	public FluidContainerMode FLUID_CONTAINER_MODE = FluidContainerMode.FILL;
	
	public int CONTAINER_MOVE_TIMER = 0;
	public int CONTAINER_MOVE_SPEED = 4;
	
	public FluidDistributor FLUID_DIST;
	
	public void initializeBaseMachineWithTank(int InitialEnergyMult, int InitialPowerUse, int InitialEnergyCapacity, int InitialEntryPerTick, int InitialProcessingTime, 
			int internalSlotCount, int inputSlots, int outputSlots, int InitialTankCapacity) {	
		initializeBasicMachine(InitialEnergyMult, InitialPowerUse, InitialEnergyCapacity, InitialEntryPerTick, InitialProcessingTime, internalSlotCount, inputSlots, outputSlots);
		INITIAL_TANK_CAPACITY = InitialTankCapacity;
		TANK = new FluidTank(INITIAL_TANK_CAPACITY);
		FLUID_DIST = new FluidDistributor(this, TANK);
	}
	public void setFluidContainerSlot(int slot, FluidContainerMode mode) {
		FLUID_CONTAINER_SLOT = slot;
		FLUID_CONTAINER_MODE = mode;
	}
	@Override
	public void update(){
		super.update();
		if(FLUID_CONTAINER_SLOT != -1) {
			useFluidContainer();
		}
		if(evauluateRedstoneSettings()) {
			FLUID_DIST.distributeFluid();
		}
	}
	public void useFluidContainer() {
		if(CONTAINER_MOVE_TIMER < CONTAINER_MOVE_SPEED) {
			CONTAINER_MOVE_TIMER++;	
		}else{
			if(SLOTS_INPUT.getStackInSlot(FLUID_CONTAINER_SLOT) != null && SLOTS_INPUT.getStackInSlot(FLUID_CONTAINER_SLOT).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && SLOTS_INPUT.getStackInSlot(FLUID_CONTAINER_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) instanceof FluidHandlerItemStack) {
				FluidHandlerItemStack tempContainer = (FluidHandlerItemStack)SLOTS_INPUT.getStackInSlot(FLUID_CONTAINER_SLOT).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				if(FLUID_CONTAINER_MODE ==  FluidContainerMode.DRAIN && tempContainer.getFluid() != null && tempContainer.getFluid().amount > 0) {
					if(tempContainer.getFluid().isFluidEqual(TANK.getFluid()) || TANK.getFluid() == null) {
						TANK.fill(tempContainer.drain(Math.min(TANK.getCapacity()-TANK.getFluidAmount(), 100), true), true);
						worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, .1f, 1, false);
						CONTAINER_MOVE_TIMER = 0;	
					}
				}else if(FLUID_CONTAINER_MODE == FluidContainerMode.FILL && TANK.getFluid() != null){
					if(tempContainer.getFluid() == null || tempContainer.getFluid().isFluidEqual(TANK.getFluid())) {
						TANK.drain(tempContainer.fill(new FluidStack(TANK.getFluid().getFluid(), Math.min(TANK.getCapacity()-TANK.getFluidAmount(), 100)), true), true);	
						worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, .1f, 1, false);
						CONTAINER_MOVE_TIMER = 0;	
					}
				}
			}	
		}
	}
	@Override
	public void upgradeHandler(){
		powerUpgrade();	
		tankUpgrade();
		processingUpgrade();
	}
	public void tankUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(SLOTS_UPGRADES.getStackInSlot(i) != null) {
				if(SLOTS_UPGRADES.getStackInSlot(i).getItem() instanceof BaseTankUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseTankUpgrade tempUpgrade = (BaseTankUpgrade) SLOTS_UPGRADES.getStackInSlot(slot).getItem();
			TANK.setCapacity((int)(tempUpgrade.getValueMultiplied(INITIAL_TANK_CAPACITY, tempUpgrade.getMultiplier(SLOTS_UPGRADES.getStackInSlot(slot), 0))));
		}else{
			TANK.setCapacity(INITIAL_TANK_CAPACITY);
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
    
	public boolean isTankEmpty() {
		return TANK.getFluidAmount() <= 0 ? true : false;
	}
	public boolean tankHasSpace() {
		return TANK.getFluidAmount() < TANK.getCapacity();
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return TANK.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(!worldObj.isRemote) {
			sync();
		}
		return TANK.fill(resource, doFill);
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(!worldObj.isRemote) {
			sync();
		}
		return TANK.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(!worldObj.isRemote) {
			sync();
		}
		return TANK.drain(maxDrain, doDrain);
	}
	public IFluidTank getFluidTank() {
		return TANK;
	}
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
}
