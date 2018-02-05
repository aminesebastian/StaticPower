package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.client.gui.widgets.SlotPhantom;

public class ContainerItemFilter extends Container {
	
	private InventoryItemFilter filterInventory;
	private int numRows = 1;
	
	public ContainerItemFilter(InventoryPlayer invPlayer, InventoryItemFilter invFilter) {
		filterInventory = invFilter;

		int slotOffset = invFilter.getFilterTier()  == FilterTier.BASIC ? 3 : invFilter.getFilterTier() == FilterTier.UPGRADED ? 1 : 0;
		for(int i=0; i<invFilter.getFilterTier().getSlotCount(); i++) {
			this.addSlotToContainer(new SlotPhantom(filterInventory, i, 8 + (i+slotOffset) * 18, 19));
		}
		this.addSlotToContainer(new Slot(filterInventory, invFilter.getFilterTier().getSlotCount(), 8, 41));
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 69 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 127));
		}
	}
    public boolean canDragIntoSlot(Slot slot){
        return true;
    }
	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
	{
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0)
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
    }
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}	
	@Override
    public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
	//Detect Changes
	public void detectAndSendChanges() {
		super.detectAndSendChanges();		
	}
}
	

