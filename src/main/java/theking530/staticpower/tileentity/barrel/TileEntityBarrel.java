package theking530.staticpower.tileentity.barrel;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.assists.utilities.WorldUtilities;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityBarrel extends BaseTileEntity {
	private int storedAmount;
	private int maxStoredItems;
	private ItemStack storedItem;
	
	public TileEntityBarrel() {
		initializeSlots(0, 0, 1);
		storedItem = ItemStack.EMPTY;
		maxStoredItems = 16384;
	}
	@Override
	public void process() {

	}
	public void onBarrelRightClicked(EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!getWorld().isRemote) {
			if(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
				if(!storedItem.isEmpty()) {
					for(int i=0; i<player.inventory.getSizeInventory(); i++) {
						if(maxStoredItems - storedAmount <= 0) {
							break;
						}
						if(player.inventory.getStackInSlot(i).isEmpty()) {
							continue;
						}
						ItemStack playerItemStack = player.inventory.getStackInSlot(i).copy();
						playerItemStack.setCount(1);
						if(ItemStack.areItemStacksEqual(playerItemStack, storedItem)) {
							int maxTake = maxStoredItems - storedAmount;
							int playerItemAmount = player.inventory.getStackInSlot(i).getCount();
							int actualTake = Math.max(playerItemAmount-maxTake, 0);
							player.inventory.getStackInSlot(i).setCount(actualTake);
							storedAmount += playerItemAmount - actualTake;
							slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount)));
						}
					}
				}
			}
			if(storedItem.isEmpty()) {
				initializeNewItem(player.getHeldItem(EnumHand.MAIN_HAND).copy(), player.getHeldItem(EnumHand.MAIN_HAND).getCount());
				player.getHeldItem(EnumHand.MAIN_HAND).setCount(0);
				slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, storedAmount));
			}else{
				ItemStack tempPlayerHeldCopy = player.getHeldItem(EnumHand.MAIN_HAND).copy();
				tempPlayerHeldCopy.setCount(1);
				if(ItemStack.areItemStacksEqual(storedItem, tempPlayerHeldCopy)) {
					int maxTake = maxStoredItems - storedAmount;
					int playerItemAmount = player.getHeldItem(EnumHand.MAIN_HAND).getCount();
					int actualTake = Math.max(playerItemAmount-maxTake, 0);
					player.getHeldItem(EnumHand.MAIN_HAND).setCount(actualTake);
					storedAmount += playerItemAmount - actualTake;
					slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount)));
				}
			}
		}
		updateBlock();
	}
    public void onBarrelLeftClicked(EntityPlayer playerIn) {
    	if(!getWorld().isRemote) {
    		BlockPos position = getPos().offset(getFacingDirection());
        	if(playerIn.isSneaking()) {
        		ItemStack dropItemStack = storedItem.copy();
        		dropItemStack.setCount(1);
        		storedAmount--;
				slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount)));
        		if(storedAmount <= 0) {
        			clearBarrel();
        		}
    			WorldUtilities.dropItem(getWorld(), position, dropItemStack);
        	}else{
        		ItemStack dropItemStack = storedItem.copy();
        		int dropAmount = Math.min(storedItem.getMaxStackSize(), storedAmount);
        		dropItemStack.setCount(dropAmount);
        		storedAmount -= dropAmount;
				slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount)));
        		if(storedAmount <= 0) {
        			clearBarrel();
        		}
    			WorldUtilities.dropItem(getWorld(), position, dropItemStack);
        	}
			ItemStack temp = storedItem.copy();
			temp.setCount(Math.min(storedItem.getMaxStackSize(), storedAmount));
			slotsOutput.setStackInSlot(0, temp);
    		updateBlock();
    	}
    }
    
    public ItemStack getStoredItem() {
    	return storedItem;
    }
    public int getStoredAmount() {
    	return storedAmount;
    }
    public int getMaxStoredAmount() {
    	return maxStoredItems;
    }
    public float getFilledRatio() {
    	return (float)getStoredAmount()/(float)getMaxStoredAmount();
    }

    @Override
    public String getName() {
    	return "container.Barrel";
    }
    
	@Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        storedAmount = nbt.getInteger("STORED_AMOUNT");
        storedItem = new ItemStack(nbt.getCompoundTag("STORED_ITEM"));
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("STORED_AMOUNT", storedAmount);
        nbt.setTag("STORED_ITEM", storedItem.serializeNBT());
		return nbt;	
	}

    private void initializeNewItem(ItemStack item, int amount) {
		storedItem = item;
		storedItem.setCount(1);
		storedAmount = amount;
    }
    private void clearBarrel() {
    	storedAmount = 0;
    	storedItem = ItemStack.EMPTY;
		slotsOutput.setStackInSlot(0, ItemStack.EMPTY);
    }
    
	/*Capability Handling*/
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing){
    	if(getSideConfiguration(facing) == Mode.Disabled) {
    		return false;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
    		return true;
    	}
        return super.hasCapability(capability, facing);
    }
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing){
       	if(getSideConfiguration(facing) == Mode.Disabled) {
    		return null;
    	}
    	if(capability==CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(new IItemHandler() {
				public int getSlots() {
			    	return evauluateRedstoneSettings() ? 1 : 0;
			    }
			    @Nonnull
			    public ItemStack getStackInSlot(int slot) {
			    	return ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount));
			    }
			    @Nonnull
			    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			    	int maxInsertAmount = maxStoredItems - storedAmount;
			    	
		            if (!storedItem.isEmpty() & !ItemHandlerHelper.canItemStacksStack(stack, storedItem)) {
			    		return stack;
			    	}  
			    	if(maxInsertAmount == 0) {
			    		return stack;
			    	}

		    		int originalAmount = stack.getCount();
		    		int insertAmount = Math.min(maxInsertAmount, originalAmount);
		    		if(!simulate) {
				    	if(storedItem.isEmpty()) {
				    		initializeNewItem(stack, insertAmount);
				    	}else{
				    		storedAmount += insertAmount;
				    	}
				    	updateBlock();
		    		}
		    		return ItemHandlerHelper.copyStackWithSize(stack, originalAmount - insertAmount);
			    }
			    @Nonnull
			    public ItemStack extractItem(int slot, int amount, boolean simulate) {
			    	if(storedAmount == 0) {
			    		return ItemStack.EMPTY;
			    	}else{
			    		int extractAmount = Math.min(storedAmount, amount);
			    		if(!simulate) {
				    		storedAmount -= extractAmount;
				    		if(storedAmount == 0) {
				    			clearBarrel();
				    		}
					    	updateBlock();
			    		}
			    		return ItemHandlerHelper.copyStackWithSize(storedItem, extractAmount);
			    	}	
			    }
			    public int getSlotLimit(int slot) {
			    	return maxStoredItems;
			    }
			});
    	}
    	return super.getCapability(capability, facing);
    }
}
