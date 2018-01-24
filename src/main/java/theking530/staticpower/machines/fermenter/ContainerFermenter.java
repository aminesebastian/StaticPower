package theking530.staticpower.machines.fermenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;

public class ContainerFermenter extends Container {
	
	public TileEntityFermenter FERMENTER;
	
	private int upgradeSlotX;
	private int upgradeSlotY;
	public ContainerFermenter(InventoryPlayer invPlayer, TileEntityFermenter teFERMENTER) {
		FERMENTER = teFERMENTER;
		
		//Input
        for (int i= 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j){
                this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsInput, j + i * 3, 87 + j * 18, 16 + i * 18){
        			@Override
        	        public boolean isItemValid(ItemStack itemStack) {
        		          return FermenterRecipeRegistry.Fermenting().getFluidResult(itemStack) != null;
        		        }
        		});
            }
        }
        //Output
        this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsOutput, 1, 62, 52));
        
        //Battery
        this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsInput, 9, 27, 71));
        
        //Container Input and Output
        this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsInput, 10, 7, 17));
        this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsOutput, 0, 7, 47));
        
        //Upgrades
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsUpgrades, 0, upgradeSlotX+171, upgradeSlotY+12));
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsUpgrades, 1, upgradeSlotX+171, upgradeSlotY+32));
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsUpgrades, 2, upgradeSlotX+171, upgradeSlotY+52));
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.slotsInternal, 0, 10000, 10000));
		
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 27 + j * 18, 90 + i * 18));
			}
		}
		
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 27 + i * 18, 148));
		}
	}
	public void moveUpgradeSlots(int x, int y) {
		this.upgradeSlotY = y;
		this.upgradeSlotX = x;
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
                if (FermenterRecipeRegistry.Fermenting().getFluidResult(itemstack1)!= null){
                    if (!this.mergeItemStack(itemstack1, 0, 9, false)){
                        return null;
                    }
                }else if (invSlot >= 6 && invSlot < 33) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
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
		return FERMENTER.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

