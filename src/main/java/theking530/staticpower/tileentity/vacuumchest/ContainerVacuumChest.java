package theking530.staticpower.tileentity.vacuumchest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.client.gui.widgets.SlotFilter;

public class ContainerVacuumChest extends Container {
	
	private TileEntityVacuumChest V_CHEST;
	private int numRows;

	public ContainerVacuumChest(InventoryPlayer invPlayer, TileEntityVacuumChest teVChest) {
		V_CHEST = teVChest;
		numRows = V_CHEST.SLOTS_OUTPUT.getSlots() / 9;
		
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new SlotItemHandler(V_CHEST.SLOTS_OUTPUT, x + y * 9, 8 + x * 18, 20 + y * 18));
			}
		}
		this.addSlotToContainer(new SlotFilter(V_CHEST.SLOTS_INTERNAL, 0, 8, 78));
		
		this.addSlotToContainer(new SlotItemHandler(V_CHEST.SLOTS_UPGRADES, 0, 116, 78));
		this.addSlotToContainer(new SlotItemHandler(V_CHEST.SLOTS_UPGRADES, 1, 134, 78));
		this.addSlotToContainer(new SlotItemHandler(V_CHEST.SLOTS_UPGRADES, 2, 152, 78));
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 103 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 161));
		}
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
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return V_CHEST.isUseableByPlayer(player);
	}	
	//Detect Changes
	public void detectAndSendChanges() {
		super.detectAndSendChanges();		
	}
}
	

