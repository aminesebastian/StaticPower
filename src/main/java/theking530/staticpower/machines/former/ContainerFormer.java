package theking530.staticpower.machines.former;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.FormerRecipeRegistry;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;

public class ContainerFormer extends Container {
	
	private TileEntityFormer former;


	public ContainerFormer(InventoryPlayer invPlayer, TileEntityFormer tePoweredGrinder) {
		former = tePoweredGrinder;
		//Input Former
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsInput, 0, 59, 34) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		        return tePoweredGrinder.hasResult(itemStack);		          
		    }
		});
		//Input Mold
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsInput, 1, 37, 34) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
				return FormerRecipeRegistry.Forming().isValidMold(itemStack);	          
		    }
		});

		//Battery
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsInternal, 1, 8, 65));
		
		//Output
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsOutput, 0, 118, 35) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		    }
		});	
		
		//Upgrades
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsUpgrades, 0, 152, 12));
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsUpgrades, 1, 152, 32));
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsUpgrades, 2, 152, 52));
		
		//Processing
		addSlotToContainer(new SlotItemHandler(tePoweredGrinder.slotsInternal, 0, 10000, 10000) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		    }
		});
				
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
			
		//ActionBar
		for(int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
		}
	}
	
	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
		  ItemStack itemstack = ItemStack.EMPTY;
	        Slot slot = (Slot)this.inventorySlots.get(invSlot);

	        if (slot != null && slot.getHasStack()) {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();

	            if (invSlot == 0 || invSlot == 1 ||  invSlot == 2 || invSlot == 3) {
	                if (!this.mergeItemStack(itemstack1, 8, 44, true)) {
	                    return ItemStack.EMPTY;
	                }
	                slot.onSlotChange(itemstack1, itemstack);
	            }else if (invSlot != 0 && invSlot != 1 && invSlot != 2 && invSlot != 3){
	            	if (GrinderRecipeRegistry.Grinding().getGrindingResult(itemstack1) != null){
	                    if (!this.mergeItemStack(itemstack1, 0, 1, false)){
	                        return ItemStack.EMPTY;
	                    }
	                }else if (invSlot >= 8 && invSlot < 35) {
	                    if (!this.mergeItemStack(itemstack1, 35, 44, false)) {
	                        return ItemStack.EMPTY;
	                    }
	                }else if (invSlot >= 35 && invSlot < 44 && !this.mergeItemStack(itemstack1, 8, 35, false))  {
	                    return ItemStack.EMPTY;
	                }
	            }else if (!this.mergeItemStack(itemstack1, 8, 44, false)) {
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
		return former.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

