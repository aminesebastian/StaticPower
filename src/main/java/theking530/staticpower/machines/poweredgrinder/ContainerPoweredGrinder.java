package theking530.staticpower.machines.poweredgrinder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.GrinderRecipeRegistry;

public class ContainerPoweredGrinder extends Container {
	
	private TileEntityPoweredGrinder GRINDER;
	public int PROCESSING_TIMER;
	public int ENERGY_STORED;

	public ContainerPoweredGrinder(InventoryPlayer invPlayer, TileEntityPoweredGrinder tePoweredGrinder) {
		PROCESSING_TIMER = 0;
		ENERGY_STORED = 0;
		
		GRINDER = tePoweredGrinder;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_INPUT, 0, 80, 18) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return GrinderRecipeRegistry.Grinding().getgrindingResult(itemStack) != null;
		        }
		});
		
		//Output
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_OUTPUT, 0, 80, 60) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});	
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_OUTPUT, 1, 106, 46) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});	
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_OUTPUT, 2, 55, 46) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});
		
		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_UPGRADES, 0, 152, 12));
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_UPGRADES, 1, 152, 32));
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_UPGRADES, 2, 152, 52));
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(tePoweredGrinder.SLOTS_INTERNAL, 0, 10000, 10000) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});
				
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
		  ItemStack itemstack = null;
	        Slot slot = (Slot)this.inventorySlots.get(invSlot);

	        if (slot != null && slot.getHasStack()) {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();

	            if (invSlot == 0 || invSlot == 1 ||  invSlot == 2 || invSlot == 3) {
	                if (!this.mergeItemStack(itemstack1, 8, 44, true)) {
	                    return null;
	                }
	                slot.onSlotChange(itemstack1, itemstack);
	            }else if (invSlot != 0 && invSlot != 1 && invSlot != 2 && invSlot != 3){
	            	if (GrinderRecipeRegistry.Grinding().getgrindingResult(itemstack1) != null){
	                    if (!this.mergeItemStack(itemstack1, 0, 1, false)){
	                        return null;
	                    }
	                }else if (invSlot >= 8 && invSlot < 35) {
	                    if (!this.mergeItemStack(itemstack1, 35, 44, false)) {
	                        return null;
	                    }
	                }else if (invSlot >= 35 && invSlot < 44 && !this.mergeItemStack(itemstack1, 8, 35, false))  {
	                    return null;
	                }
	            }else if (!this.mergeItemStack(itemstack1, 8, 44, false)) {
	                return null;
	            }
	            if (itemstack1.stackSize == 0){
	                slot.putStack((ItemStack)null);
	            }else {
	                slot.onSlotChanged();
	            }
	            if (itemstack1.stackSize == itemstack.stackSize){
	                return null;
	            }
	            slot.onPickupFromSlot(player, itemstack1);
	        }
	        return itemstack;
	    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return GRINDER.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
        GRINDER.sync();
    }
}

