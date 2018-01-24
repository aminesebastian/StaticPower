package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;

public class ContainerFluidInfuser extends Container {
	
	private TileEntityFluidInfuser INFUSER;
	
	public ContainerFluidInfuser(InventoryPlayer invPlayer, TileEntityFluidInfuser teFluidInfuser) {
		INFUSER = teFluidInfuser;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsInput, 0, 74, 32));
		
		//Fluid Slots
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsInput, 1, 7, 17));
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsOutput, 1, 7, 47));
		
		//Output
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsOutput, 0, 131, 32) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemStack, INFUSER.TANK.getFluid()) != null;
		    }
		});
		
		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsUpgrades, 0, 171, 12));
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsUpgrades, 1, 171, 32));
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsUpgrades, 2, 171, 52));
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(teFluidInfuser.slotsInternal, 0, 10000, 10000) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});
				
		//Inventory
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 9; j++) {
						this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 27 + j * 18, 84 + i * 18));
					}
				}
				
				//ActionBar
				for(int i = 0; i < 9; i++) {
					this.addSlotToContainer(new Slot(invPlayer, i, 27 + i * 18, 142));
			}
	}

	//Shift Click Functionality
	public ItemStack transferStackInSlot(EntityPlayer player, int invSlot) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(invSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (invSlot == 1 || invSlot == 0) {
                if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }else if (invSlot != 1 && invSlot != 0){
            	if (InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemstack1, INFUSER.TANK.getFluid()) != null){
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)){
                        return null;
                    }
                }else if (invSlot >= 6 && invSlot < 33) {
                    if (!this.mergeItemStack(itemstack1, 33, 42, false)) {
                        return null;
                    }
                }else if (invSlot >= 33 && invSlot < 42 && !this.mergeItemStack(itemstack1, 6, 33, false))  {
                    return null;
                }
            }else if (!this.mergeItemStack(itemstack1, 6, 42, false)) {
                return null;
            }
            if (itemstack1.getCount() == 0){
                slot.putStack(ItemStack.EMPTY);
            }else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()){
                return null;
            }
            slot.onTake(player, itemstack1);
        }
        return itemstack;
	   }
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return INFUSER.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

