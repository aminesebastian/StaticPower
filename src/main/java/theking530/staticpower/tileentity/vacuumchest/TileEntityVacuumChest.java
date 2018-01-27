package theking530.staticpower.tileentity.vacuumchest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BasePowerUpgrade;
import theking530.staticpower.items.upgrades.BaseRangeUpgrade;
import theking530.staticpower.tileentity.BaseTileEntity;
import theking530.staticpower.tileentity.IUpgradeableTileEntity;
import theking530.staticpower.utils.InventoryUtilities;

public class TileEntityVacuumChest extends BaseTileEntity implements Predicate<EntityItem>, IUpgradeableTileEntity {

	private float vacuumDiamater;
	private float initialVacuumDiamater;
	
	public TileEntityVacuumChest() {
		initializeBasicTileEntity(1, 0, 30);
		initialVacuumDiamater = 6;
		vacuumDiamater = initialVacuumDiamater;
	}		
	@Override
	public void process() {
		handleUpgrades();
		
	    AxisAlignedBB aabb = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
	    aabb = aabb.expand(vacuumDiamater, vacuumDiamater, vacuumDiamater);
	    aabb = aabb.offset(-vacuumDiamater/2, -vacuumDiamater/2, -vacuumDiamater/2);
	    List<EntityItem> droppedItems = getWorld().getEntitiesWithinAABB(EntityItem.class, aabb, this);
		for (EntityItem entity : droppedItems) {

			double x = (pos.getX() + 0.5D - entity.posX);
			double y = (pos.getY() + 0.5D - entity.posY);
			double z = (pos.getZ() + 0.5D - entity.posZ);
			EntityItem item = entity;
			ItemStack stack = item.getItem().copy();
			if(InventoryUtilities.canFullyInsertItemIntoInventory(slotsOutput, stack) && doesItemPassFilter(stack)) {
				double distance = Math.sqrt(x * x + y * y + z * z);
				if (distance < 1.1) {
					if(InventoryUtilities.canFullyInsertItemIntoInventory(slotsOutput, stack)) {
						InventoryUtilities.insertItemIntoInventory(slotsOutput, stack);
						item.setDead();
						getWorld().playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
					}
				} else {
					double var11 = 1.0 - distance / 15.0;
					if (var11 > 0.0D) {
						var11 *= var11;
						entity.motionX += x / distance * var11 * 0.06;
						entity.motionY += y / distance * var11 * 0.15;
						entity.motionZ += z / distance * var11 * 0.06;
					}
				}
			}
		}
	}
	public boolean hasFilter() {
		if(getInternalStack(0) != ItemStack.EMPTY) {
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
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, net.minecraft.util.EnumFacing facing){
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return (T) slotsOutput;
    	}
    	return super.getCapability(capability, facing);
    }
    //IInventory
	@Override
	public String getName() {
		return "container.VacuumChest";		
	}
	@Override
	public boolean apply(EntityItem input) {
		return true;
	}
	public float getRadius() {
		return vacuumDiamater/2.0f;
	}
	
	/*Update Handling*/
	@Override
	public void handleUpgrades() {
		handleRangeUpgrade();
	}
	public void handleRangeUpgrade() {
		ItemStack upgrade = ItemStack.EMPTY;
		int slot = -1;
		for(int i=0; i<getUpgradeSlots().size(); i++) {
			slot = getUpgradeSlots().get(i);
			upgrade = slotsUpgrades.getStackInSlot(slot);
			if(upgrade != ItemStack.EMPTY) {
				if(isValidUpgrade(upgrade)) {
					if(upgrade.getItem() instanceof BaseRangeUpgrade) {
						break;
					}
				}
			}
		}
		if(upgrade != ItemStack.EMPTY) {
			BaseRangeUpgrade tempUpgrade = (BaseRangeUpgrade) upgrade.getItem();
			vacuumDiamater = tempUpgrade.getValueMultiplied(initialVacuumDiamater, tempUpgrade.getMultiplier(upgrade, 0));
		}else{
			vacuumDiamater = initialVacuumDiamater;
		}
	}
	@Override
	public boolean isUpgradeable() {
		return true;
	}
	@Override
	public ItemStackHandler getUpgradeInventory() {
		return slotsUpgrades;
	}
	@Override
	public List<Integer> getUpgradeSlots() {
		return new ArrayList<Integer>(Arrays.asList(0, 1, 2));
	}
	@Override
	public boolean isValidUpgrade(@Nonnull ItemStack update) {
		if(update != ItemStack.EMPTY && update.getItem() instanceof BaseRangeUpgrade) {
			return true;
		}
		return false;
	}
}
