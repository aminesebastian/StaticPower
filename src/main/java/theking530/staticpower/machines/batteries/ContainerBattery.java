package theking530.staticpower.machines.batteries;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import theking530.staticpower.machines.batteries.tileentities.TileEntityBattery;


public class ContainerBattery extends Container {
	
	private TileEntityBattery BATTERY;
	
	public ContainerBattery(InventoryPlayer invPlayer, TileEntityBattery teBattery) {
		BATTERY = teBattery;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
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

	            if (p_82846_2_ == 2)
	            {
	                if (!this.mergeItemStack(itemstack1, 3, 39, true))
	                {
	                    return null;
	                }

	                slot.onSlotChange(itemstack1, itemstack);
	            }
	            else if (p_82846_2_ != 1 && p_82846_2_ != 0)
	            {
	                if (FurnaceRecipes.instance().getSmeltingResult(itemstack1) != null)
	                {
	                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
	                    {
	                        return null;
	                    }
	                }
	                else if (TileEntityFurnace.isItemFuel(itemstack1))
	                {
	                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
	                    {
	                        return null;
	                    }
	                }
	                else if (p_82846_2_ >= 3 && p_82846_2_ < 30)
	                {
	                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
	                    {
	                        return null;
	                    }
	                }
	                else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
	                {
	                    return null;
	                }
	            }
	            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
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

	            if (itemstack1.getCount() == itemstack.getCount())
	            {
	                return null;
	            }

	            slot.onTake(p_82846_1_, itemstack1);
	        }

	        return itemstack;
	    }
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return BATTERY.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

