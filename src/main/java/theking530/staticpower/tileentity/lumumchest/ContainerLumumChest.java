package theking530.staticpower.tileentity.lumumchest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerLumumChest extends Container {
	
	private IInventory chestInventory;
	private TileEntityLumumChest staticChest;
	private int numRows;

	public ContainerLumumChest(InventoryPlayer invPlayer, TileEntityLumumChest teStaticChest) {
		staticChest = teStaticChest;
		chestInventory = (IInventory) teStaticChest;
		this.numRows = teStaticChest.getSizeInventory() / 14;
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 14; x++) {
				this.addSlotToContainer(new Slot(teStaticChest, x + y * 14, 7 + (x * 18), 4+(19 + (y * 18))));
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 52 + j * 18, 212 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 52 + i * 18, 270));
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

            if (p_82846_2_ < this.numRows * 14)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 14, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 14, false))
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
	

