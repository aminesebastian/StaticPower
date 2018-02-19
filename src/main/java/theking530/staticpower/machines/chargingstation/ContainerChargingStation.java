package theking530.staticpower.machines.chargingstation;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerChargingStation extends BaseContainer {
	
	private TileEntityChargingStation C_CHARGING;

	public ContainerChargingStation(InventoryPlayer invPlayer, TileEntityChargingStation teCharging) {	
		C_CHARGING = teCharging;
		
		//Input
		this.addSlotToContainer(new StaticPowerContainerSlot(teCharging.slotsInput, 0, 49, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		this.addSlotToContainer(new StaticPowerContainerSlot(teCharging.slotsInput, 1, 69, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		this.addSlotToContainer(new StaticPowerContainerSlot(teCharging.slotsInput, 2, 89, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		this.addSlotToContainer(new StaticPowerContainerSlot(teCharging.slotsInput, 3, 109, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		    }
		});	
		
		//Output
		this.addSlotToContainer(new OutputSlot(teCharging.slotsOutput, 0, 44, 52));
		this.addSlotToContainer(new OutputSlot(teCharging.slotsOutput, 1, 68, 52));
		this.addSlotToContainer(new OutputSlot(teCharging.slotsOutput, 2, 92, 52));
		this.addSlotToContainer(new OutputSlot(teCharging.slotsOutput, 3, 116, 52));
		
		//Upgrades
		this.addSlotToContainer(new UpgradeSlot(teCharging.slotsUpgrades, 0, 152, 8));
		this.addSlotToContainer(new UpgradeSlot(teCharging.slotsUpgrades, 1, 152, 30));
		this.addSlotToContainer(new UpgradeSlot(teCharging.slotsUpgrades, 2, 152, 52));
		
		//Battery
		this.addSlotToContainer(new BatterySlot(teCharging.slotsInternal, 0, 8, 54));
		
		//Armor
		this.addSlotToContainer(new Slot(invPlayer, 39, -24, 14) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem().isValidArmor(itemStack, EntityEquipmentSlot.HEAD, invPlayer.player);
		    }
		});	
		this.addSlotToContainer(new Slot(invPlayer, 38, -24, 33) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem().isValidArmor(itemStack, EntityEquipmentSlot.CHEST, invPlayer.player);
		    }
		});	
		this.addSlotToContainer(new Slot(invPlayer, 37, -24, 52) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
                return itemStack.getItem().isValidArmor(itemStack, EntityEquipmentSlot.LEGS, invPlayer.player);
		    }
		});	
		this.addSlotToContainer(new Slot(invPlayer, 36, -24, 71) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
	            return itemStack.getItem().isValidArmor(itemStack, EntityEquipmentSlot.FEET, invPlayer.player);
		    }
		});	

		this.addPlayerInventory(invPlayer, 8, 84);
		this.addPlayerHotbar(invPlayer, 8, 142);
	}
	
	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
	    ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = (Slot)this.inventorySlots.get(invSlot);
	
	    if (slot != null && slot.getHasStack()) {
	        ItemStack itemstack1 = slot.getStack();
	        itemstack = itemstack1.copy();
	
	        if (invSlot < 4) {
	            if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
	                return ItemStack.EMPTY;
	            }
	            slot.onSlotChange(itemstack1, itemstack);
	        }else if (invSlot != 1 && invSlot != 0){
	        	if (itemstack1.getItem() instanceof IEnergyContainerItem){
	                if (!this.mergeItemStack(itemstack1, 0, 4, false)){
	                    return ItemStack.EMPTY;
	                }
	            }else if (invSlot >= 6 && invSlot < 33) {
	                if (!this.mergeItemStack(itemstack1, 33, 42, false)) {
	                    return ItemStack.EMPTY;
	            }
	            }else if (invSlot >= 33 && invSlot < 42 && !this.mergeItemStack(itemstack1, 6, 33, false))  {
	                return ItemStack.EMPTY;
	            }
	        }else if (!this.mergeItemStack(itemstack1, 6, 42, false)) {
	            return ItemStack.EMPTY;
	        }
	        if (itemstack1.getCount() == 0){
	            slot.putStack(ItemStack.EMPTY);
	        }else {
	            slot.onSlotChanged();
	        }
	        if (itemstack1.getCount() == itemstack.getCount()){
	            return ItemStack.EMPTY;
	        }
	        slot.onTake(player, itemstack1);
	    }
	    return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return C_CHARGING.isUseableByPlayer(player);
	}
}

