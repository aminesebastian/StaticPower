package theking530.staticpower.machines.fermenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.items.tools.ISolderingIron;

public class ContainerFermenter extends Container {
	
	public TileEntityFermenter FERMENTER;
	private int PROCESSING_TIMER;
	private int FLUID_AMOUNT;
	private int ENERGY_STORED;
	private int FLUID_ID;
	
	private int upgradeSlotX;
	private int upgradeSlotY;
	public ContainerFermenter(InventoryPlayer invPlayer, TileEntityFermenter teFERMENTER) {
		PROCESSING_TIMER = 0;
		ENERGY_STORED = 0;
		FLUID_AMOUNT = 0;
		
		FERMENTER = teFERMENTER;
		
		//Input
        for (int i= 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j){
                this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_INPUT, j + i * 3, 68 + j * 18, 16 + i * 18){
        			@Override
        	        public boolean isItemValid(ItemStack itemStack) {
        		          return FermenterRecipeRegistry.Fermenting().getFluidResult(itemStack)!= null;
        		        }
        		});
            }
        }
        //Battery
        this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_INPUT, 9, 43, 52));
        
        //Bucket
        this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_INPUT, 10, 8, 71));
		
        //Upgrades
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_UPGRADES, 0, upgradeSlotX+152, upgradeSlotY+12));
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_UPGRADES, 1, upgradeSlotX+152, upgradeSlotY+32));
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_UPGRADES, 2, upgradeSlotX+152, upgradeSlotY+52));
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(teFERMENTER.SLOTS_INTERNAL, 0, 10000, 10000));
		
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 90 + i * 18));
			}
		}
		
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 148));
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
		return FERMENTER.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
        FERMENTER.sync();
    }
}

