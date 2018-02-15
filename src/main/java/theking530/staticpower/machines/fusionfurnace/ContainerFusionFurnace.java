package theking530.staticpower.machines.fusionfurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;

public class ContainerFusionFurnace extends Container {
	
	private TileEntityFusionFurnace FURNACE;
	
	public ContainerFusionFurnace(InventoryPlayer invPlayer, TileEntityFusionFurnace teFusionFurnace) {
		FURNACE = teFusionFurnace;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsInput, 0, 44, 44) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return true;
		        }
		});
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsInput, 1, 59, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return true;
		        }
		});
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsInput, 2, 80, 17) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return true;
		        }
		});
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsInput, 3, 101, 24) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return true;
		        }
		});
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsInput, 4, 116, 44) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return true;
		        }
		});
		//Output
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsOutput, 0, 80, 59) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});	

		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsUpgrades, 0, 152, 12));
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsUpgrades, 1, 152, 32));
		this.addSlotToContainer(new SlotItemHandler(teFusionFurnace.slotsUpgrades, 2, 152, 52));
		
				
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
			
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
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
	            	if (FusionRecipeRegistry.Fusing().getFusionResult(itemstack1) != null){
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
		return FURNACE.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

