package theking530.staticpower.tileentity.staticchest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerStaticChest extends Container {
	
	private IInventory chestInventory;
	private TileEntityStaticChest staticChest;
	private int numRows;

	public ContainerStaticChest(InventoryPlayer invPlayer, TileEntityStaticChest teStaticChest) {
		staticChest = teStaticChest;
		this.numRows = teStaticChest.SLOTS_OUTPUT.getSlots() / 9;
		for (int y = 0; y < 5; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new SlotItemHandler(teStaticChest.SLOTS_OUTPUT, x + y * 9, 8 + x * 18, 19 + y * 18));
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 123 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 181));
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
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return staticChest.isUseableByPlayer(player);
	}	
	//Detect Changes
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
    public IInventory getChestInventory() {
        return this.chestInventory;
    }
}
	

