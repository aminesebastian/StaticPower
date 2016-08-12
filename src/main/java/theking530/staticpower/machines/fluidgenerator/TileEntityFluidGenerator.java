package theking530.staticpower.machines.fluidgenerator;

import javax.annotation.Nullable;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import theking530.staticpower.fluids.ModFluids;
import theking530.staticpower.machines.BaseMachineWithTank;
import theking530.staticpower.power.PowerDistributor;

public class TileEntityFluidGenerator extends BaseMachineWithTank{

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {0};
	private static final int[] slots_side = new int[] {0};		
	
	private PowerDistributor POWER_DIST;
	public int SOUND_TIMER = 15;
	private FluidStack PROCESSING_FLUID;
			
	public TileEntityFluidGenerator() {
		initializeBaseMachineWithTank(1, 50000, 480, 0, 4, new int[]{0}, new int[]{0}, new int[]{1,2,3}, 10000);
		POWER_DIST = new PowerDistributor(this, STORAGE);
		MOVE_SPEED = 10;
	}
	
	//Internal TANK			
	public boolean hasFuel() {
		if (TANK.getFluidAmount() > 0) {
			return true;
		}
		return false;
	}		
	public float getAdjustedVolume() {
		float amount = TANK.getFluidAmount();
		float capacity = TANK.getCapacity();
		float volume = (amount/capacity)*0.8F;		
			return volume;	
	}
			
    @Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        STORAGE.readFromNBT(nbt);
        TANK.readFromNBT(nbt);
        if(slots != null) {
            NBTTagList list = nbt.getTagList("Items", 10);
    		slots = new ItemStack[getSizeInventory()];
            for (int i =0; i < list.tagCount(); i++) {
    			NBTTagCompound nbt1 = (NBTTagCompound)list.getCompoundTagAt(i);
    			byte b0 = nbt1.getByte("Slot");
    			
    			if (b0 >= 0 && b0 < slots.length) {
    				slots[b0] = ItemStack.loadItemStackFromNBT(nbt1);
    			}
    		}	
        }
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        STORAGE.writeToNBT(nbt);
        TANK.writeToNBT(nbt);
    	if(slots != null) {
        	NBTTagList list = new NBTTagList();
    		for (int i = 0; i < slots.length; i++) {
    			if (slots[i] != null) {
    				NBTTagCompound nbt1 = new NBTTagCompound();
    				nbt1.setByte("Slot", (byte)i);
    				slots[i].writeToNBT(nbt1);
    				list.appendTag(nbt1);
    			}
    			
    		}
    		nbt.setTag("Items", list);
    	}	
    	return nbt;
	}
	@Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound tag = pkt.getNbtCompound();
		readFromNBT(pkt.getNbtCompound());
	}
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket(){
    	NBTTagCompound tag = new NBTTagCompound();
    	writeToNBT(tag);
    	return new SPacketUpdateTileEntity(pos, getBlockMetadata(), tag);
    }

	//Functionality		
	@Override
	public String getName() {
		return "Fluid Generator";	
	}	
		   
	//Function
	@Override
	public boolean hasResult(ItemStack itemstack) {
		/**
		if(itemstack.getItem() instanceof StaticBucket) {
			return true;
		}
		if(itemstack.getItem() instanceof EnergizedBucket) {
			return true;
		}
		if(itemstack.getItem() instanceof LumumBucket) {
			return true;
		}
		*/
		return false;
	}
	public void process() {
		if(!isTankEmpty() && !isProcessing() && getFluidRFOutput(TANK.getFluid()) > 0 && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored()) {
			PROCESSING_TIMER = 1;
			PROCESSING_FLUID = TANK.getFluid();
			TANK.drain(1, true);
		}
		if(isProcessing() && STORAGE.getEnergyStored() < STORAGE.getMaxEnergyStored() && PROCESSING_FLUID != null) {
			STORAGE.receiveEnergy(getFluidRFOutput(PROCESSING_FLUID), false);
			PROCESSING_TIMER = 0;
		}
		POWER_DIST.distributePower();
		if(slots[0] != null) {
			MOVE_TIMER++;
			if(MOVE_TIMER == MOVE_SPEED) {
				if(slots[0].getItem() instanceof IFluidContainerItem) {
					IFluidContainerItem tempContainer = (IFluidContainerItem)slots[0].getItem();
					if(tempContainer.getFluid(slots[0]).amount + TANK.getFluidAmount() <= TANK.getCapacity()) {
						if(TANK.getFluid() == null) {
							TANK.fill(tempContainer.drain(slots[0], TANK.getCapacity(), true), true);
						}else if(tempContainer.getFluid(slots[0]).isFluidEqual(TANK.getFluid())) {
							TANK.fill(tempContainer.drain(slots[0], TANK.getCapacity(), true), true);
						}
						worldObj.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, (float) 0.5, 1, false);
					}
				}	
			}
		}else{
			MOVE_TIMER = 0;
		}
	}
	public int getFluidRFOutput(FluidStack fluid) {
		if(fluid != null) {
			if(fluid.getFluid() == ModFluids.StaticFluid) {
				return 120;
			}
			if(fluid.getFluid() == ModFluids.EnergizedFluid) {
				return 240;
			}
			if(fluid.getFluid() == ModFluids.LumumFluid) {
				return 480;
			}
		}
		return 0;
	}
	@Override
	public int receiveEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;	
	}
}
		

	


	
