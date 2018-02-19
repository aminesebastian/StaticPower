package theking530.staticpower.machines.poweredfurnace;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.SlotWithExperienceOutput;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerPoweredFurnace extends BaseContainer {
	
	private TileEntityPoweredFurnace tileEntityFurnace;

	public ContainerPoweredFurnace(InventoryPlayer invPlayer, TileEntityPoweredFurnace tePoweredSmelter) {
		tileEntityFurnace = tePoweredSmelter;
		
		//Input
		this.addSlotToContainer(new StaticPowerContainerSlot(tePoweredSmelter.slotsInput, 0, 50, 28) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return !FurnaceRecipes.instance().getSmeltingResult(itemStack).isEmpty();	          
			}
		});
		
		//Battery
		this.addSlotToContainer(new BatterySlot(tePoweredSmelter.slotsInternal, 1, 8, 65));
		
		//Output
		this.addSlotToContainer(new SlotWithExperienceOutput(invPlayer.player, tePoweredSmelter.slotsOutput, 0, 109, 32));
		
		//Upgrades
		this.addSlotToContainer(new UpgradeSlot(tePoweredSmelter.slotsUpgrades, 0, 152, 12));
		this.addSlotToContainer(new UpgradeSlot(tePoweredSmelter.slotsUpgrades, 1, 152, 32));
		this.addSlotToContainer(new UpgradeSlot(tePoweredSmelter.slotsUpgrades, 2, 152, 52));
		
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
	        
	        if(invSlot >= 10) {
	        	if(!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
		            if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}  
	        	if(itemstack1.getItem() instanceof BaseUpgrade && tileEntityFurnace.canAcceptUpgrade(itemstack1)) {
		            if (!this.mergeItemStack(itemstack1, 3, 6, false)) {
		                return ItemStack.EMPTY;
		            }
	        	}    
	        }else{
	            if (!this.mergeItemStack(itemstack1, 33, 41, false)) {
	                return ItemStack.EMPTY;
	            }
	            if (!this.mergeItemStack(itemstack1, 6, 33, false)) {
	                return ItemStack.EMPTY;
	            }
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
		return tileEntityFurnace.isUseableByPlayer(player);
	}
}

