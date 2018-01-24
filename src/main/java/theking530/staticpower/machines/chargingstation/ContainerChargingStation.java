package theking530.staticpower.machines.chargingstation;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerChargingStation extends Container {
	
	private TileEntityChargingStation C_CHARGING;

	public ContainerChargingStation(InventoryPlayer invPlayer, TileEntityChargingStation teCharging) {	
		C_CHARGING = teCharging;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsInput, 0, 63, 29) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem ? true : false;
		    }
		});	
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsInput, 1, 81, 29) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem ? true : false;
		    }
		});	
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsInput, 2, 99, 29) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem ? true : false;
		    }
		});	
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsInput, 3, 117, 29) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem ? true : false;
		    }
		});	
		
		//Output
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsOutput, 0, 60, 61) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem ? true : false;
		        }
		});
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsOutput, 1, 80, 61) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		    }
		});	
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsOutput, 2, 100, 61) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		    }
		});	
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsOutput, 3, 120, 61) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		    }
		});	
		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsUpgrades, 0, 155, 8));
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsUpgrades, 1, 155, 30));
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsUpgrades, 2, 155, 52));
		
		//Battery/Bucket
		this.addSlotToContainer(new SlotItemHandler(teCharging.slotsInput, 4, 8, 73) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem ? true : false;
		    }
		});	
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 27 + j * 18, 95 + i * 18));
			}
		}
		
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 27 + i * 18, 153));
		}
		//Armor
		for (int k = 0; k < 4; ++k){
			addSlotToContainer(new Slot(invPlayer, 36 + (3 - k), 7, 95 + k * 18));
		}
	}
	
	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
	    ItemStack itemstack = null;
	    Slot slot = (Slot)this.inventorySlots.get(invSlot);
	
	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	
	        if (invSlot == 1 || invSlot == 0) {
	            if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
	                return null;
	            }
	            slot.onSlotChange(itemstack1, itemstack);
	        }else if (invSlot != 1 && invSlot != 0){
	        	if (itemstack1.getItem() instanceof IEnergyContainerItem){
	                if (!this.mergeItemStack(itemstack1, 0, 4, false)){
	                    return null;
	                }
	            }else if (invSlot >= 6 && invSlot < 33) {
	                if (!this.mergeItemStack(itemstack1, 33, 42, false)) {
	                    return null;
	            }
	            }else if (invSlot >= 33 && invSlot < 42 && !this.mergeItemStack(itemstack1, 6, 33, false))  {
	                return null;
	            }
	        }else if (!this.mergeItemStack(itemstack1, 6, 42, false)) {
	            return null;
	        }
	        if (itemstack1.getCount() == 0){
	            slot.putStack(ItemStack.EMPTY);
	        }else {
	            slot.onSlotChanged();
	        }
	        if (itemstack1.getCount() == itemstack.getCount()){
	            return null;
	        }
	        slot.onTake(player, itemstack1);
	    }
	    return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return C_CHARGING.isUseableByPlayer(player);
	}
	
	//Detect Changes
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }	
}

