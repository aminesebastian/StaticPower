package theking530.staticpower.tileentity.astralquary.brain;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerAstralQuarryBrain extends BaseContainer {
	
	private TileEntityAstralQuarryBrain astralQuary;

	public ContainerAstralQuarryBrain(InventoryPlayer invPlayer, TileEntityAstralQuarryBrain teAstralQuary) {
		astralQuary = teAstralQuary;

        for (int l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 3; ++i1){
                this.addSlotToContainer(new StaticPowerContainerSlot(teAstralQuary.slotsOutput, i1 + l * 3, 61 + i1 * 18, 20 + l * 18));
            }
        }
        
		addPlayerHotbar(invPlayer, 8, 152);
		addPlayerInventory(invPlayer, 8, 94);

	}

	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if (slot != null && slot.getHasStack())   {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if(slotIndex >= 3) {
                if (astralQuary.canAcceptUpgrade(itemstack1) && !mergeItemStack(itemstack1, 0, 3, false)) {
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
		return astralQuary.isUseableByPlayer(player);
	}	
}
	

