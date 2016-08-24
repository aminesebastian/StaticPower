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
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityVacuumChest extends BaseTileEntity implements Predicate<EntityItem> {

	private int RANGE = 3;
	
	public TileEntityVacuumChest() {
		int[] tempSlots = new int[27];
		for(int i=0; i<27; i++) {
			tempSlots[i] = i;
		}
		initializeBasicTileEntity(31, tempSlots, null);
	}
	@Override
	public void update() {
		int redstoneSignal = worldObj.getStrongPower(pos);
		if(REDSTONE_MODE == 0) {
			process();
			if(OUTPUT_SLOTS != null) {
				outputFunction(OUTPUT_SLOTS);	
			}
			if(INPUT_SLOTS != null) {
				inputFunction(INPUT_SLOTS);				
			}
		}
		if(REDSTONE_MODE == 1) {
			if(redstoneSignal == 0) {
				process();
				if(OUTPUT_SLOTS != null) {
					outputFunction(OUTPUT_SLOTS);	
				}
				if(INPUT_SLOTS != null) {
					inputFunction(INPUT_SLOTS);				
				}
			}
		}
		if(REDSTONE_MODE == 2) {
			if(redstoneSignal > 0) {
				process();
				if(OUTPUT_SLOTS != null) {
					outputFunction(OUTPUT_SLOTS);	
				}
				if(INPUT_SLOTS != null) {
					inputFunction(INPUT_SLOTS);				
				}
			}
		}	
	}			
	void process() {
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
						if(slots[i] == null) {
							slots[i] = stack;
							item.setDead();
							worldObj.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
							break;
						}else{
							if(slots[i].isItemEqual(stack)) {
								int stackSize = slots[i].stackSize + stack.stackSize;
								if(stackSize <= 64) {
									slots[i].stackSize = stackSize;
									item.setDead();
									worldObj.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
									break;
								}else{
									int difference = 64 - stack.stackSize;
									slots[i].stackSize = 64;
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
			if(canSlotAcceptItemstack(stack, slots[i])) {
				return true;
			}
		}
		return false;
	}
	public boolean hasFilter() {
		if(slots[27] != null) {
			return true;
		}
		return false;
	}
	public boolean doesItemPassFilter(ItemStack stack) {
		if(hasFilter()) {
			ItemFilter tempFilter = (ItemFilter)slots[27].getItem();
			return tempFilter.evaluateFilter(slots[27], stack);
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
