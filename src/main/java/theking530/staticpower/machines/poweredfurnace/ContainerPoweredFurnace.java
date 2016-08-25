package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerPoweredFurnace extends Container {
	
	private TileEntityPoweredFurnace Smelter;
	private int PROCESSING_TIMER;

	public ContainerPoweredFurnace(InventoryPlayer invPlayer, TileEntityPoweredFurnace tePoweredSmelter) {
		PROCESSING_TIMER = 0;
		
		Smelter = tePoweredSmelter;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(tePoweredSmelter.SLOTS_INPUT, 0, 50, 28));
		
		//Output
		this.addSlotToContainer(new SlotItemHandler(tePoweredSmelter.SLOTS_OUTPUT, 0, 110, 32) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return FurnaceRecipes.instance().getSmeltingResult(itemStack) != null;
		        }
		});	
		
		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(tePoweredSmelter.SLOTS_UPGRADES, 0, 152, 12));
		this.addSlotToContainer(new SlotItemHandler(tePoweredSmelter.SLOTS_UPGRADES, 1, 152, 32));
		this.addSlotToContainer(new SlotItemHandler(tePoweredSmelter.SLOTS_UPGRADES, 2, 152, 52));
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(tePoweredSmelter.SLOTS_INTERNAL, 0, 10000, 10000) {
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
		return Smelter.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i){
            IContainerListener icontainerlistener = (IContainerListener)this.listeners.get(i);
            if (PROCESSING_TIMER != Smelter.PROCESSING_TIMER){
                icontainerlistener.sendProgressBarUpdate(this, 2, Smelter.PROCESSING_TIMER);
            }
        }
        PROCESSING_TIMER = Smelter.PROCESSING_TIMER;
    }
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
    	Smelter.PROCESSING_TIMER = data;
    }
}

