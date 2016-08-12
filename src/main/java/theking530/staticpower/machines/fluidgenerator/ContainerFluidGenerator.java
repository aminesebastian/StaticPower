package theking530.staticpower.machines.fluidgenerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.ItemFluidContainer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class ContainerFluidGenerator extends Container {
	
	private TileEntityFluidGenerator FluidGenerator;
	private int PROCESSING_TIME;
	private int FLUID_AMOUNT;
	private int lastItemInfusionTime;
	
	public ContainerFluidGenerator(InventoryPlayer invPlayer, TileEntityFluidGenerator teFluidGenerator) {
		PROCESSING_TIME = 0;
		FLUID_AMOUNT = 0;
		lastItemInfusionTime = 0;
		
		FluidGenerator = teFluidGenerator;
		
		//Input
		this.addSlotToContainer(new Slot(teFluidGenerator, 0, 80, 31));
		
		//Upgrades
		this.addSlotToContainer(new Slot(teFluidGenerator, 1, 152, 12));
		this.addSlotToContainer(new Slot(teFluidGenerator, 2, 152, 32));
		this.addSlotToContainer(new Slot(teFluidGenerator, 3, 152, 52));
		
		//Inventory
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 9; j++) {
						this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
					}
				}
				
				//ActionBar
				for(int i = 0; i < 9; i++) {
					this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142));
			}
	}
	
	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int p_82846_2_)
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
	                if (itemstack1.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN))
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

	            if (itemstack1.stackSize == 0)
	            {
	                slot.putStack((ItemStack)null);
	            }
	            else
	            {
	                slot.onSlotChanged();
	            }

	            if (itemstack1.stackSize == itemstack.stackSize)
	            {
	                return null;
	            }

	            slot.onPickupFromSlot(player, itemstack1);
	        }

	        return itemstack;
	    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return FluidGenerator.isUseableByPlayer(player);
	}
	
	//Detect Changes
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		this.PROCESSING_TIME = this.FluidGenerator.PROCESSING_TIME;
	}
	
	//Send Gui Update
	public void updateProgressBar(int i, int j) {
		if (i == 0) {
			FluidGenerator.PROCESSING_TIME = j;
		}
	}
}

