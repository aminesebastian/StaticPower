package theking530.staticpower.machines.refinery.controller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerFluidRefineryController extends BaseContainer {
	
	private TileEntityFluidRefineryController fluidRefinery;

	public ContainerFluidRefineryController(InventoryPlayer invPlayer, TileEntityFluidRefineryController teAstralQuary) {
		fluidRefinery = teAstralQuary;

		this.addSlotToContainer(new StaticPowerContainerSlot(fluidRefinery.slotsInput, 0, 8, 20));
		this.addSlotToContainer(new StaticPowerContainerSlot(fluidRefinery.slotsInput, 1, 8, 44));
		this.addSlotToContainer(new StaticPowerContainerSlot(fluidRefinery.slotsInput, 2, 8, 68));
		
        
		addPlayerHotbar(invPlayer, 8, 156);
		addPlayerInventory(invPlayer, 8, 98);

	}

	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())   {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(slotIndex >= 3) {
                if (fluidRefinery.canAcceptUpgrade(itemstack1) && !mergeItemStack(itemstack1, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }else{
                if (!mergeItemStack(itemstack1, 3,inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.onSlotChanged();
        }

        return itemstack;
    }
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return fluidRefinery.isUseableByPlayer(player);
	}	
}
	

