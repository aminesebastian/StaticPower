package theking530.staticpower.tileentity.vacuumchest;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityVacuumChest extends BaseTileEntity implements Predicate<EntityItem> {

	private int RANGE = 3;
	
	public TileEntityVacuumChest() {
		initializeBasicTileEntity(1, 0, 30);
	}		
	@Override
	public void process() {
	    AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	    aabb = aabb.expand(RANGE, RANGE, RANGE);
	    List<EntityItem> droppedItems = worldObj.getEntitiesWithinAABB(EntityItem.class, aabb, this);
		for (EntityItem entity : droppedItems) {

			double x = (pos.getX() + 0.5D - entity.posX);
			double y = (pos.getY() + 0.5D - entity.posY);
			double z = (pos.getZ() + 0.5D - entity.posZ);
			EntityItem item = entity;
			ItemStack stack = item.getEntityItem().copy();
			if(canAcceptItem(stack) && doesItemPassFilter(stack)) {
				double distance = Math.sqrt(x * x + y * y + z * z);
				if (distance < 1.1) {
					for(int i=0; i<27; i++){
						if(getOutputStack(i) == null) {
							SLOTS_OUTPUT.setStackInSlot(i, stack);
							item.setDead();
							worldObj.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
							break;
						}else{
							if(getOutputStack(i).isItemEqual(stack)) {
								int stackSize = getOutputStack(i).stackSize + stack.stackSize;
								if(stackSize <= 64) {
									getOutputStack(i).stackSize = stackSize;
									item.setDead();
									worldObj.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
									break;
								}else{
									int difference = 64 - stack.stackSize;
									getOutputStack(i).stackSize = 64;
									item.getEntityItem().stackSize = difference;
									worldObj.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
									break;
								}
							}
						}
					}
				} else {
					double var11 = 1.0 - distance / 15.0;
					if (var11 > 0.0D) {
						var11 *= var11;
						entity.motionX += x / distance * var11 * 0.03;
						entity.motionY += y / distance * var11 * 0.15;
						entity.motionZ += z / distance * var11 * 0.03;
					}
				}
			}
		}
	}
	public boolean canAcceptItem(ItemStack stack) {
		for(int i=0; i<27; i++) {
			if(canSlotAcceptItemstack(stack, getOutputStack(i))) {
				return true;
			}
		}
		return false;
	}
	public boolean hasFilter() {
		if(getInternalStack(0) != null) {
			return true;
		}
		return false;
	}
	public boolean doesItemPassFilter(ItemStack stack) {
		if(hasFilter()) {
			ItemFilter tempFilter = (ItemFilter)getInternalStack(0).getItem();
			return tempFilter.evaluateFilter(getInternalStack(0), stack);
		}else{
			return true;
		}
	}
	
	//NBT
	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return nbt;
	}	  	    
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return (T) SLOTS_OUTPUT;
    	}
    	return super.getCapability(capability, facing);
    }
    //IInventory
	@Override
	public String getName() {
		return "VacuumChest";		
	}
	@Override
	public boolean apply(EntityItem input) {
		return true;
	}
}
