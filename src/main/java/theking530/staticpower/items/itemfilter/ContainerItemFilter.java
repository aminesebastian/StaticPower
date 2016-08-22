package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.client.gui.widgets.SlotPhantom;

public class ContainerItemFilter extends Container {
	
	private int numRows = 1;
	public InventoryItemFilter FILTER_INV;
	public FilterTier TIER;
	
	public ContainerItemFilter(InventoryPlayer invPlayer, InventoryItemFilter invFilter, FilterTier tier) {
		FILTER_INV = invFilter;
		TIER = tier;
		if(TIER == FilterTier.BASIC) {
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 0, 8 + 3 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 1, 8 + 4 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 2, 8 + 5 * 18, 19));
			this.addSlotToContainer(new Slot(FILTER_INV, 3, 8, 41));
		}
		if(TIER == FilterTier.UPGRADED) {
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 0, 8 + 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 1, 8 + 2 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 2, 8 + 3 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 3, 8 + 4 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 4, 8 + 5 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 5, 8 + 6 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 6, 8 + 7 * 18, 19));
			this.addSlotToContainer(new Slot(FILTER_INV, 7, 8, 41));
		}
		if(TIER == FilterTier.ADVANCED) {
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 0, 8, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 1, 8 + 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 2, 8 + 2 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 3, 8 + 3 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 4, 8 + 4 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 5, 8 + 5 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 6, 8 + 6 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 7, 8 + 7 * 18, 19));
			this.addSlotToContainer(new SlotPhantom(FILTER_INV, 8, 8 + 8 * 18, 19));
			this.addSlotToContainer(new Slot(FILTER_INV, 9, 8, 41));
		}
			
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
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
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
			return null;
		}
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
	//Detect Changes
	public void detectAndSendChanges() {
		super.detectAndSendChanges();		
	}
}
	

