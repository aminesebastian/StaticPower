package theking530.staticpower.machines.poweredgrinder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerPoweredGrinder extends BaseContainer {
	
	private TileEntityPoweredGrinder tileEntityGrinder;

	public ContainerPoweredGrinder(InventoryPlayer invPlayer, TileEntityPoweredGrinder tePoweredGrinder) {		
		tileEntityGrinder = tePoweredGrinder;
		
		//Input
		this.addSlotToContainer(new StaticPowerContainerSlot(tePoweredGrinder.slotsInput, 0, 80, 18) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return GrinderRecipeRegistry.Grinding().getGrindingResult(itemStack) != null;	          
			}
		});
		
		//Battery
		this.addSlotToContainer(new BatterySlot(tePoweredGrinder.slotsInternal, 1, 8, 65));
		
		//Output
		this.addSlotToContainer(new OutputSlot(tePoweredGrinder.slotsOutput, 0, 80, 60));
		this.addSlotToContainer(new OutputSlot(tePoweredGrinder.slotsOutput, 1, 106, 46));
		this.addSlotToContainer(new OutputSlot(tePoweredGrinder.slotsOutput, 2, 54, 46));
		
		//Upgrades
		this.addSlotToContainer(new UpgradeSlot(tePoweredGrinder.slotsUpgrades, 0, 152, 12));
		this.addSlotToContainer(new UpgradeSlot(tePoweredGrinder.slotsUpgrades, 1, 152, 32));
		this.addSlotToContainer(new UpgradeSlot(tePoweredGrinder.slotsUpgrades, 2, 152, 52));
		
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
	        
	        if(invSlot >= 10) {
	        	if( GrinderRecipeRegistry.Grinding().getGrindingResult(itemstack1) != null) {
		            if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}  
	        	if(itemstack1.getItem() instanceof BaseUpgrade && tileEntityGrinder.canAcceptUpgrade(itemstack1)) {
		            if (!this.mergeItemStack(itemstack1, 5, 8, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}    
	        }else{
	            if (!this.mergeItemStack(itemstack1, 35, 43, false)) {
	                return ItemStack.EMPTY;
	            }
	            if (!this.mergeItemStack(itemstack1, 8, 35, false)) {
	                return ItemStack.EMPTY;
	            }
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
		return tileEntityGrinder.isUseableByPlayer(player);
	}
}

