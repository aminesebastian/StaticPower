package theking530.staticpower.client.gui.widgets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotPhantom extends Slot{

	public SlotPhantom(IInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
	}
    @Override
    public boolean canTakeStack(EntityPlayer player) {
    	super.canTakeStack(player);
    	inventory.setInventorySlotContents(slotNumber, null);
        return false;
    }
    public boolean isItemValid(ItemStack itemStack){
    	ItemStack tempItemStack = itemStack.copy();
    	tempItemStack.stackSize = 1;
    	inventory.setInventorySlotContents(slotNumber, tempItemStack);
        return false;
    }
}
