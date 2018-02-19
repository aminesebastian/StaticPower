package theking530.staticpower.machines.fermenter;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.handlers.crafting.registries.FermenterRecipeRegistry;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerFermenter extends BaseContainer {
	
	public TileEntityFermenter FERMENTER;
	
	public ContainerFermenter(InventoryPlayer invPlayer, TileEntityFermenter teFERMENTER) {
		FERMENTER = teFERMENTER;
		
		//Input
        for (int i= 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j){
                this.addSlotToContainer(new StaticPowerContainerSlot(teFERMENTER.slotsInput, j + i * 3, 40 + j * 18, 21 + i * 18){
        			@Override
        	        public boolean isItemValid(ItemStack itemStack) {
        		          return FermenterRecipeRegistry.Fermenting().getFluidResult(itemStack) != null;
        		    }
        		});
            }
        }
        //Output
        this.addSlotToContainer(new OutputSlot(teFERMENTER.slotsOutput, 0, 115, 55));
        
        //Battery
		this.addSlotToContainer(new BatterySlot(teFERMENTER.slotsInternal, 1, 8, 54));
        
        //Container Input and Output
        this.addSlotToContainer(new FluidContainerSlot(teFERMENTER.slotsInternal, 2, -24, 11));
		this.addSlotToContainer(new OutputSlot(teFERMENTER.slotsInternal, 3, -24, 43));
        
        //Upgrades
		this.addSlotToContainer(new UpgradeSlot(teFERMENTER.slotsUpgrades, 0, -24, 76));
		this.addSlotToContainer(new UpgradeSlot(teFERMENTER.slotsUpgrades, 1, -24, 94));
		this.addSlotToContainer(new UpgradeSlot(teFERMENTER.slotsUpgrades, 2, -24, 112));
		
		
		this.addPlayerInventory(invPlayer, 8, 90);
		this.addPlayerHotbar(invPlayer, 8, 148);
	}

	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(invSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (invSlot == 1 || invSlot == 0) {
                if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }else if (invSlot != 1 && invSlot != 0){
                if (FermenterRecipeRegistry.Fermenting().getFluidResult(itemstack1)!= null){
                    if (!this.mergeItemStack(itemstack1, 0, 9, false)){
                        return ItemStack.EMPTY;
                    }
                }else if (invSlot >= 6 && invSlot < 33) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                }else if (invSlot >= 33 && invSlot < 42 && !this.mergeItemStack(itemstack1, 6, 33, false))  {
                    return ItemStack.EMPTY;
                }
            }else if (!this.mergeItemStack(itemstack1, 6, 42, false)) {
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
		return FERMENTER.isUseableByPlayer(player);
	}
}

