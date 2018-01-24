package theking530.staticpower.machines.quarry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.client.gui.widgets.SlotFilter;
import theking530.staticpower.items.itemfilter.ItemFilter;

public class ContainerQuarry extends Container {
	
	private TileEntityQuarry QUARRY;

	public ContainerQuarry(InventoryPlayer invPlayer, TileEntityQuarry teQuarry) {
		QUARRY = teQuarry;
		
		//Filter
		this.addSlotToContainer(new SlotFilter(teQuarry.slotsInternal, 0, 27, 71) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof ItemFilter;
		    }
		});	
		
		//Fluid Slots
		this.addSlotToContainer(new SlotItemHandler(teQuarry.slotsInput, 0, 7, 17));
		this.addSlotToContainer(new SlotItemHandler(teQuarry.slotsOutput, 0, 7, 47));
		
		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(teQuarry.slotsUpgrades, 0, 171, 17));
		this.addSlotToContainer(new SlotItemHandler(teQuarry.slotsUpgrades, 1, 171, 37));
		this.addSlotToContainer(new SlotItemHandler(teQuarry.slotsUpgrades, 2, 171, 57));
			
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 27 + j * 18, 92 + i * 18));
			}
		}
		
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 27 + i * 18, 150));
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
	        	if (FurnaceRecipes.instance().getSmeltingResult(itemstack1) != null){
	                if (!this.mergeItemStack(itemstack1, 0, 1, false)){
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
		return QUARRY.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }	
}

