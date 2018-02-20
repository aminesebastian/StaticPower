package theking530.staticpower.machines.fluidgenerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerFluidGenerator extends BaseContainer {
	
	private TileEntityFluidGenerator fluidGeneratorTileEntity;

	public ContainerFluidGenerator(InventoryPlayer invPlayer, TileEntityFluidGenerator teFluidGenerator) {
		fluidGeneratorTileEntity = teFluidGenerator;
		
		//Fluid Slots
		this.addSlotToContainer(new FluidContainerSlot(teFluidGenerator.slotsInput, 0, -24, 11));;
		this.addSlotToContainer(new OutputSlot(teFluidGenerator.slotsOutput, 0, -24, 43));
		
		//Upgrades
		this.addSlotToContainer(new UpgradeSlot(teFluidGenerator.slotsUpgrades, 0, -24, 76));
		this.addSlotToContainer(new UpgradeSlot(teFluidGenerator.slotsUpgrades, 1, -24, 94));
		this.addSlotToContainer(new UpgradeSlot(teFluidGenerator.slotsUpgrades, 2, -24, 112));
		
		this.addPlayerHotbar(invPlayer, 8, 142);
		this.addPlayerInventory(invPlayer, 8, 84);		
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
		return fluidGeneratorTileEntity.isUseableByPlayer(player);
	}
}

