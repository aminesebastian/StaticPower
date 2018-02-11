package theking530.staticpower.tileentity.digistore;

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
import theking530.staticpower.items.upgrades.BaseDigistoreCapacityUpgrade;
import theking530.staticpower.items.upgrades.DigistoreMiscUpgrades;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntityDigistore extends BaseTileEntity {
	public static final int DEFAULT_CAPACITY = 512;
	
	private int storedAmount;
	private int maxStoredItems;
	private ItemStack storedItem;
	private boolean shouldVoid;
	
	public TileEntityDigistore() {
		initializeSlots(0, 0, 1);
		storedItem = ItemStack.EMPTY;
		maxStoredItems = DEFAULT_CAPACITY;
		shouldVoid = false;
	}
	@Override
	public void process() {
		handleStorageUpgrades();
		handleMiscUpgradtes();
	}
	public void onBarrelRightClicked(EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!getWorld().isRemote) {
			if(canAcceptUpgrade(player.getHeldItem(EnumHand.MAIN_HAND))) {
				for(int i=0; i<slotsUpgrades.getSlots(); i++) {
					ItemStack remaining = slotsUpgrades.insertItem(i, player.getHeldItem(hand).copy(), false);
					player.getHeldItem(hand).setCount(remaining.getCount());
					if(remaining.getCount() <= 0) {
						break;
					}
				}
			}	
			
			if(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() || !ItemStack.areItemStacksEqual(ItemHandlerHelper.copyStackWithSize(player.getHeldItem(EnumHand.MAIN_HAND), 1), storedItem)) {
				if(!storedItem.isEmpty()) {
					for(int i=0; i<player.inventory.getSizeInventory(); i++) {
						if(maxStoredItems - storedAmount <= 0 && !shouldVoid) {
							break;
						}
						if(player.inventory.getStackInSlot(i).isEmpty()) {
							continue;
						}
						ItemStack insertedItemstack = insertItemstack(player.inventory.getStackInSlot(i));
						player.inventory.getStackInSlot(i).setCount(insertedItemstack.getCount());
					}
				}
			}
			ItemStack insertedItemstack = insertItemstack(player.getHeldItem(EnumHand.MAIN_HAND));
			player.getHeldItem(EnumHand.MAIN_HAND).setCount(insertedItemstack.getCount());
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
        		if(getFacingDirection() == EnumFacing.EAST) {
        			WorldUtilities.dropItem(getWorld(), position.getX(), position.getY(), position.getZ()-0.5, dropItemStack);
        		}else if(getFacingDirection() == EnumFacing.NORTH) {
        			WorldUtilities.dropItem(getWorld(), position.getX()+0.5, position.getY(), position.getZ(), dropItemStack);
        		}else if(getFacingDirection() == EnumFacing.SOUTH) {
        			WorldUtilities.dropItem(getWorld(), position.getX()+0.5, position.getY(), position.getZ()+0.5, dropItemStack);
        		}else{
        			WorldUtilities.dropItem(getWorld(), position.getX(), position.getY(), position.getZ()+0.5, dropItemStack);
        		}
        	}else{
        		ItemStack dropItemStack = storedItem.copy();
        		int dropAmount = Math.min(storedItem.getMaxStackSize(), storedAmount);
        		dropItemStack.setCount(dropAmount);
        		storedAmount -= dropAmount;
				slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount)));
        		if(storedAmount <= 0) {
        			clearBarrel();
        		}
        		if(getFacingDirection() == EnumFacing.EAST) {
        			WorldUtilities.dropItem(getWorld(), position.getX(), position.getY(), position.getZ()-0.5, dropItemStack);
        		}else if(getFacingDirection() == EnumFacing.NORTH) {
        			WorldUtilities.dropItem(getWorld(), position.getX()+0.5, position.getY(), position.getZ(), dropItemStack);
        		}else if(getFacingDirection() == EnumFacing.SOUTH) {
        			WorldUtilities.dropItem(getWorld(), position.getX()+0.5, position.getY(), position.getZ()+0.5, dropItemStack);
        		}else{
        			WorldUtilities.dropItem(getWorld(), position.getX(), position.getY(), position.getZ()+0.5, dropItemStack);
        		}

        	}
			ItemStack temp = storedItem.copy();
			temp.setCount(Math.min(storedItem.getMaxStackSize(), storedAmount));
			slotsOutput.setStackInSlot(0, temp);
    	}
		updateBlock();
    }
    
    /**
     * @param item - Item to Insert
     * @return Returns remaining itemstack after insert.
     */
    public ItemStack insertItemstack(ItemStack item) {
    	if(storedItem.isEmpty()) {
			initializeNewItem(item.copy(), item.getCount());
			slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, storedAmount));
			return ItemStack.EMPTY;
		}else{
			if(ItemHandlerHelper.canItemStacksStack(item, storedItem)) {
				int maxTake = getRemainingStorage(true);
				int playerItemAmount = item.getCount();
				int remaining = Math.max(playerItemAmount-maxTake, 0);
	    		storedAmount = Math.min(storedAmount + (playerItemAmount - remaining), maxStoredItems);
				slotsOutput.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(storedItem, Math.min(storedItem.getMaxStackSize(), storedAmount)));
				return ItemHandlerHelper.copyStackWithSize(item, remaining);
			}
		}
		return item;
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
    public boolean voidUpgradeInstalled() {
    	return shouldVoid;
    } 
    
    @Override
    public String getName() {
    	return "container.Digistore";
    }
    
	@Override  
	public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        storedAmount = nbt.getInteger("STORED_AMOUNT");
        storedItem = new ItemStack(nbt.getCompoundTag("STORED_ITEM"));
        shouldVoid = nbt.getBoolean("VOID");
    }		
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("STORED_AMOUNT", storedAmount);
        nbt.setTag("STORED_ITEM", storedItem.serializeNBT());
        nbt.setBoolean("VOID", shouldVoid);
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
    
    private int getRemainingStorage(boolean checkForVoidUpgrade) {
    	return checkForVoidUpgrade ? shouldVoid ? maxStoredItems :  maxStoredItems - storedAmount :  maxStoredItems - storedAmount;
    }
      
    /*Upgrade Handleing*/
    @Override
	public boolean canAcceptUpgrade(ItemStack upgrade) {	
    	if(!upgrade.isEmpty()) {
    		return upgrade.getItem() instanceof BaseDigistoreCapacityUpgrade || upgrade.getItem() instanceof DigistoreMiscUpgrades;
    	}
		return false;
	}
    private void handleMiscUpgradtes() {
    	shouldVoid = hasUpgrade(DigistoreMiscUpgrades.VoidUprgade);
    }
    private void handleStorageUpgrades() {
		int additionalAmount = 0;
		for(ItemStack upgrade : getAllUpgrades()) {
			if(!upgrade.isEmpty() && upgrade.getItem() instanceof BaseDigistoreCapacityUpgrade) {
				BaseDigistoreCapacityUpgrade itemUpgrade = (BaseDigistoreCapacityUpgrade)upgrade.getItem();
				additionalAmount += itemUpgrade.getUpgradeValueAtIndex(upgrade, 0); 
			}
		}
		maxStoredItems = Math.max(0, DEFAULT_CAPACITY + additionalAmount);
		
		if(!getWorld().isRemote) {
			if(storedAmount > maxStoredItems && !shouldVoid) {
	        	while(storedAmount > maxStoredItems) {
	        		ItemStack droppedItem = getStoredItem().copy();
	        		int maxDrop = storedAmount - maxStoredItems;
	        		droppedItem.setCount(Math.min(maxDrop, droppedItem.getMaxStackSize()));
	        		storedAmount -= droppedItem.getCount();
					WorldUtilities.dropItem(getWorld(), pos.getX(), pos.getY(), pos.getZ(), droppedItem);
	        	}
				updateBlock();
			}
		}
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
			    	int maxInsertAmount = getRemainingStorage(true);
			    	
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
				    		storedAmount = Math.min(storedAmount + insertAmount, maxStoredItems);
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
