package theking530.staticpower.items.itemfilter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.client.gui.widgets.SlotPhantom;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerItemFilter extends BaseContainer {
	
	private InventoryItemFilter filterInventory;
	
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
        return false;
    }
	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, EntityPlayer player, InventoryPlayer invPlayer, Slot slot, int slotIndex) {
        if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 0, filterInventory.getFilterTier().getSlotCount(), false)) {
        	return true;
        }
		return false;	
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
}
	

