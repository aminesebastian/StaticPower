package theking530.staticpower.machines.mechanicalsqueezer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;

public class ContainerMechanicalSqueezer extends Container {
	
	private TileEntityMechanicalSqueezer CropSqueezer;

	@SuppressWarnings("unused")
	private int upgradeSlotX;
	@SuppressWarnings("unused")
	private int upgradeSlotY;
	
	public ContainerMechanicalSqueezer(InventoryPlayer invPlayer, TileEntityMechanicalSqueezer teCropSqueezer) {
		CropSqueezer = teCropSqueezer;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.slotsInput, 0, 103, 18));
		
		//Fluid Slots
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.slotsInput, 1, 7, 17) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
		    }
		});
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.slotsOutput, 1, 7, 47) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		    }
		});
		
		//Output
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.slotsOutput, 0, 103, 58) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemStack) != null;
		        }
		});
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.slotsInternal, 0, 10000, 10000));
		
		//Inventory
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 9; j++) {
						this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 27 + j * 18, 84 + i * 18));
					}
				}
				
				//ActionBar
				for(int i = 0; i < 9; i++) {
					this.addSlotToContainer(new Slot(invPlayer, i, 27 + i * 18, 142));
			}
	}
	
	public void moveUpgradeSlots(int x, int y) {
		this.upgradeSlotY = y;
		this.upgradeSlotX = x;
	}
	

	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(invSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (invSlot == 1 || invSlot == 0) {
                if (!this.mergeItemStack(itemstack1, 6, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }else if (invSlot != 1 && invSlot != 0){
                if (SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemstack1) != null){
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)){
                        return ItemStack.EMPTY;
                    }
                }else if (invSlot >= 6 && invSlot < 33) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
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
		return CropSqueezer.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

