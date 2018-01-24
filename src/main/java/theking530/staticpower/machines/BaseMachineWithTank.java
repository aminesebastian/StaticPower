package theking530.staticpower.machines;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import theking530.staticpower.fluids.FluidDistributor;
import theking530.staticpower.items.upgrades.BaseTankUpgrade;

public class BaseMachineWithTank extends BaseMachine implements IFluidHandler {

	public int INITIAL_TANK_CAPACITY;
	public FluidTank TANK;
	public int FLUID_CONTAINER_SLOT = -1;
	public int FLUID_TO_CONTAINER_RATE = 10;

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

	public void useFluidContainer() {

	}
	@Override
	public void upgradeHandler(){
		super.upgradeHandler();
		tankUpgrade();
	}
	public void tankUpgrade() {
		boolean flag = false;
		int slot = 0;
		for(int i=0; i<3; i++) {
			if(slotsUpgrades.getStackInSlot(i) != null) {
				if(slotsUpgrades.getStackInSlot(i).getItem() instanceof BaseTankUpgrade) {
					flag = true;
					slot = i;
				}
			}
		}
		if(flag) {
			BaseTankUpgrade tempUpgrade = (BaseTankUpgrade) slotsUpgrades.getStackInSlot(slot).getItem();
			TANK.setCapacity((int)(tempUpgrade.getValueMultiplied(INITIAL_TANK_CAPACITY, tempUpgrade.getMultiplier(slotsUpgrades.getStackInSlot(slot), 0))));
		}else{
			TANK.setCapacity(INITIAL_TANK_CAPACITY);
		}
	}
	
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        TANK.readFromNBT(nbt.getCompoundTag("TANK"));
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound tank =  new NBTTagCompound();
        TANK.writeToNBT(tank);
        nbt.setTag("TANK", tank);
    	return nbt;
	}    
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound tag = new NBTTagCompound();
    	writeToNBT(tag);
    	return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }
	
    public void onMachinePlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)  {
		super.onMachinePlaced(nbt, world, pos, state, placer, stack);
        TANK.readFromNBT(nbt);
	}	
   
	public boolean isTankEmpty() {
		return TANK.getFluidAmount() <= 0 ? true : false;
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return TANK.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		int temp = TANK.fill(resource, doFill);
		return temp;
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return TANK.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return TANK.drain(maxDrain, doDrain);
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
}
