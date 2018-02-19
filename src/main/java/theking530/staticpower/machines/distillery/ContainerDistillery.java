package theking530.staticpower.machines.distillery;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;

public class ContainerDistillery extends BaseContainer {
	
	private TileEntityDistillery F_GENERATOR;
	
	public ContainerDistillery(InventoryPlayer invPlayer, TileEntityDistillery teFluidGenerator) {
		F_GENERATOR = teFluidGenerator;
		
		//Input Left
		//Input Left
		this.addSlotToContainer(new FluidContainerSlot(teFluidGenerator.slotsInput, 0, 10, 17));
		this.addSlotToContainer(new OutputSlot(teFluidGenerator.slotsOutput, 0, 10, 53));
		
		//Input Right
		this.addSlotToContainer(new FluidContainerSlot(teFluidGenerator.slotsInput, 1, 150, 17));
		this.addSlotToContainer(new OutputSlot(teFluidGenerator.slotsOutput, 1, 150, 53));

		this.addPlayerInventory(invPlayer, 8, 94);
		this.addPlayerHotbar(invPlayer, 8, 153);
	}
	
	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int p_82846_2_)
	    {
	        ItemStack itemstack = ItemStack.EMPTY;
	        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

	        if (slot != null && slot.getHasStack())
	        {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();

	            if (p_82846_2_ == 2)
	            {
	                if (!this.mergeItemStack(itemstack1, 3, 39, true))
	                {
	                    return ItemStack.EMPTY;
	                }

	                slot.onSlotChange(itemstack1, itemstack);
	            }
	            else if (p_82846_2_ != 1 && p_82846_2_ != 0)
	            {
	                if (itemstack1.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN))
	                {
	                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
	                    {
	                        return ItemStack.EMPTY;
	                    }
	                }
	                else if (TileEntityFurnace.isItemFuel(itemstack1))
	                {
	                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
	                    {
	                        return ItemStack.EMPTY;
	                    }
	                }
	                else if (p_82846_2_ >= 3 && p_82846_2_ < 30)
	                {
	                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
	                    {
	                        return ItemStack.EMPTY;
	                    }
	                }
	                else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
	                {
	                    return ItemStack.EMPTY;
	                }
	            }
	            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
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

	            if (itemstack1.getCount() == itemstack.getCount())
	            {
	                return ItemStack.EMPTY;
	            }

	            slot.onTake(player, itemstack1);
	        }

	        return itemstack;
	    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return F_GENERATOR.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

