package theking530.staticpower.machines.basicfarmer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerBasicFarmer extends BaseContainer {
	
	private TileEntityBasicFarmer farmerTileEntity;
	
	public ContainerBasicFarmer(InventoryPlayer invPlayer, TileEntityBasicFarmer teFarmer) {
		farmerTileEntity = teFarmer;
		
        for (int l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 3; ++i1){
                this.addSlotToContainer(new OutputSlot(teFarmer.slotsOutput, i1 + l * 3, 76 + i1 * 18, 20 + l * 18));
            }
        }

		//FluidContainerSlots
		this.addSlotToContainer(new FluidContainerSlot(teFarmer.slotsInternal, 1, -24, 11));
		this.addSlotToContainer(new OutputSlot(teFarmer.slotsInternal, 2, -24, 43));
		
		//Hoe
		this.addSlotToContainer(new StaticPowerContainerSlot(new ItemStack(Items.IRON_HOE), teFarmer.slotsInput, 0, 48, 20) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
				return itemStack.getItem() instanceof ItemHoe;
		    }
		});
		
		//Axe
		this.addSlotToContainer(new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), teFarmer.slotsInput, 1, 48, 56) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
				return itemStack.getItem() instanceof ItemAxe;
		    }
		});
		
		//Battery
		this.addSlotToContainer(new BatterySlot(teFarmer.slotsInternal, 0, 8, 57));
		
		//Upgrades
		this.addSlotToContainer(new UpgradeSlot(teFarmer.slotsUpgrades, 0, -24, 76));
		this.addSlotToContainer(new UpgradeSlot(teFarmer.slotsUpgrades, 1, -24, 94));
		this.addSlotToContainer(new UpgradeSlot(teFarmer.slotsUpgrades, 2, -24, 112));
		
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
	        	if (FurnaceRecipes.instance().getSmeltingResult(itemstack1) != null){
	                if (!this.mergeItemStack(itemstack1, 0, 1, false)){
	                    return ItemStack.EMPTY;
	                }
	            }else if (invSlot >= 6 && invSlot < 33) {
	                if (!this.mergeItemStack(itemstack1, 33, 42, false)) {
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
		return farmerTileEntity.isUseableByPlayer(player);
	}
}

