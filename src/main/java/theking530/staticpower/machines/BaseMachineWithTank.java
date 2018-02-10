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

	public int initialTankCapacity;
	public int fluidToContainerRate = 10;
	
	public FluidTank fluidTank;	
	public FluidDistributor fluidDistributor;

	public void initializeTank(int InitialTankCapacity) {	
		initialTankCapacity = InitialTankCapacity;
		fluidTank = new FluidTank(initialTankCapacity);
		fluidDistributor = new FluidDistributor(this, fluidTank);
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
			fluidTank.setCapacity((int)(tempUpgrade.getValueMultiplied(initialTankCapacity, tempUpgrade.getMultiplier(slotsUpgrades.getStackInSlot(slot), 0))));
		}else{
			fluidTank.setCapacity(initialTankCapacity);
		}
	}
	
	@Override  
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        fluidTank.readFromNBT(nbt.getCompoundTag("TANK"));
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound tank =  new NBTTagCompound();
        fluidTank.writeToNBT(tank);
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
	
    public void deserializeOnPlaced(NBTTagCompound nbt, World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)  {
		super.deserializeOnPlaced(nbt, world, pos, state, placer, stack);
        fluidTank.readFromNBT(nbt);
	}	
   
	public boolean isTankEmpty() {
		return fluidTank.getFluidAmount() <= 0 ? true : false;
	}
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return fluidTank.getTankProperties();
	}
	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		int temp = fluidTank.fill(resource, doFill);
		return temp;
	}
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return fluidTank.drain(resource, doDrain);
	}
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		if(!getWorld().isRemote) {
			updateBlock();
		}
		return fluidTank.drain(maxDrain, doDrain);
	}

	public float getFluidLevelScaled(int height) {
		int capacity = fluidTank.getCapacity();
		int volume = fluidTank.getFluidAmount();
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
