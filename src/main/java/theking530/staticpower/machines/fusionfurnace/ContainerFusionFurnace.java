package theking530.staticpower.machines.fusionfurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.handlers.crafting.registries.FusionRecipeRegistry;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;

public class ContainerFusionFurnace extends BaseContainer {
	
	private TileEntityFusionFurnace tileEntityFusionFurnace;
	
	public ContainerFusionFurnace(InventoryPlayer invPlayer, TileEntityFusionFurnace teFusionFurnace) {
		tileEntityFusionFurnace = teFusionFurnace;
		
		//Input
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsInput, 0, 36, 40));
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsInput, 1, 58, 28));
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsInput, 2, 80, 17));
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsInput, 3, 102, 28));
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsInput, 4, 124, 40));
		
		//Output
		this.addSlotToContainer(new OutputSlot(teFusionFurnace.slotsOutput, 0, 80, 59));

		//Upgrades
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsUpgrades, 0, 152, 12));
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsUpgrades, 1, 152, 32));
		this.addSlotToContainer(new StaticPowerContainerSlot(teFusionFurnace.slotsUpgrades, 2, 152, 52));
		
		this.addPlayerInventory(invPlayer, 8, 84);
		this.addPlayerHotbar(invPlayer, 8, 142);		
	}

	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
		  ItemStack itemstack = ItemStack.EMPTY;
	        Slot slot = (Slot)this.inventorySlots.get(invSlot);

	        if (slot != null && slot.getHasStack()) {
	            ItemStack itemstack1 = slot.getStack();
	            itemstack = itemstack1.copy();

	            if (invSlot == 0 || invSlot == 1 ||  invSlot == 2 || invSlot == 3) {
	                if (!this.mergeItemStack(itemstack1, 8, 44, true)) {
	                    return ItemStack.EMPTY;
	                }
	                slot.onSlotChange(itemstack1, itemstack);
	            }else if (invSlot != 0 && invSlot != 1 && invSlot != 2 && invSlot != 3){
	            	if (FusionRecipeRegistry.Fusing().getFusionResult(itemstack1) != null){
	                    if (!this.mergeItemStack(itemstack1, 0, 1, false)){
	                        return ItemStack.EMPTY;
	                    }
	                }else if (invSlot >= 8 && invSlot < 35) {
	                    if (!this.mergeItemStack(itemstack1, 35, 44, false)) {
	                        return ItemStack.EMPTY;
	                    }
	                }else if (invSlot >= 35 && invSlot < 44 && !this.mergeItemStack(itemstack1, 8, 35, false))  {
	                    return ItemStack.EMPTY;
	                }
	            }else if (!this.mergeItemStack(itemstack1, 8, 44, false)) {
	                return ItemStack.EMPTY;
	            }
	            if (itemstack1.getCount() == 0){
	                slot.putStack(ItemStack.EMPTY);
	            }else {
	                slot.onSlotChanged();
	            }
	            if (itemstack1.getCount() == itemstack.getCount()){
	                return ItemStack.EMPTY;
	            }
	            slot.onTake(player, itemstack1);
	        }
	        return itemstack;
	    }

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntityFusionFurnace.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

