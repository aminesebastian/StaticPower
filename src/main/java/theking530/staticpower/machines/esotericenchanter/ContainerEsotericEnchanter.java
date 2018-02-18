package theking530.staticpower.machines.esotericenchanter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.items.ModItems;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerEsotericEnchanter extends BaseContainer {
	
	private TileEsotericEnchanter esotericEnchanter;

	public ContainerEsotericEnchanter(InventoryPlayer invPlayer, TileEsotericEnchanter teEsotericEnchanter) {
		esotericEnchanter = teEsotericEnchanter;
		
		//InputSlots
		this.addSlotToContainer(new StaticPowerContainerSlot(new ItemStack(Items.BOOK), esotericEnchanter.slotsInput, 0, 30, 35));
		this.addSlotToContainer(new StaticPowerContainerSlot(esotericEnchanter.slotsInput, 1, 52, 35));
		this.addSlotToContainer(new StaticPowerContainerSlot(esotericEnchanter.slotsInput, 2, 74, 35));
		
		//OutputSlots
		this.addSlotToContainer(new OutputSlot(esotericEnchanter.slotsOutput, 0, 126, 35));
			
		//BatterySlot
		this.addSlotToContainer(new StaticPowerContainerSlot(new ItemStack(ModItems.BasicBattery), esotericEnchanter.slotsInternal, 3, 8, 54));
		
		//FluidContainerSlots
		this.addSlotToContainer(new FluidContainerSlot(esotericEnchanter.slotsInternal, 4, -24, 11));
		this.addSlotToContainer(new StaticPowerContainerSlot(esotericEnchanter.slotsInternal, 5, -24, 43));
		
		//Upgrades
		this.addSlotToContainer(new StaticPowerContainerSlot(esotericEnchanter.slotsUpgrades, 0, -24, 76));
		this.addSlotToContainer(new StaticPowerContainerSlot(esotericEnchanter.slotsUpgrades, 1, -24, 94));
		this.addSlotToContainer(new StaticPowerContainerSlot(esotericEnchanter.slotsUpgrades, 2, -24, 112));
		
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
	        	if(itemstack1.getItem() == Items.BOOK) {
		            if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}
	        	if(itemstack1.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
		            if (!this.mergeItemStack(itemstack1, 5, 6, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}	   
	        	if(itemstack1.getItem() instanceof BaseUpgrade && esotericEnchanter.canAcceptUpgrade(itemstack1)) {
		            if (!this.mergeItemStack(itemstack1, 7, 10, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}    
	            if (!this.mergeItemStack(itemstack1, 1, 3, false)) {
	                return ItemStack.EMPTY;
	            }
	        }else{
	            if (!this.mergeItemStack(itemstack1, 38, 46, false)) {
	                return ItemStack.EMPTY;
	            }
	            if (!this.mergeItemStack(itemstack1, 10, 38, false)) {
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
		return esotericEnchanter.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

