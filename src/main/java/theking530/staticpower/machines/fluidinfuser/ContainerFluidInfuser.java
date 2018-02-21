package theking530.staticpower.machines.fluidinfuser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import theking530.staticpower.container.BaseContainer;
import theking530.staticpower.handlers.crafting.registries.InfuserRecipeRegistry;
import theking530.staticpower.machines.tileentitycomponents.slots.BatterySlot;
import theking530.staticpower.machines.tileentitycomponents.slots.FluidContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.OutputSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.StaticPowerContainerSlot;
import theking530.staticpower.machines.tileentitycomponents.slots.UpgradeSlot;

public class ContainerFluidInfuser extends BaseContainer {
	
	private TileEntityFluidInfuser infuserTileEntity;
	
	public ContainerFluidInfuser(InventoryPlayer invPlayer, TileEntityFluidInfuser teFluidInfuser) {
		infuserTileEntity = teFluidInfuser;
		
		//Input
		this.addSlotToContainer(new StaticPowerContainerSlot(teFluidInfuser.slotsInput, 0, 50, 32));
		
		//FluidContainerSlots
		this.addSlotToContainer(new FluidContainerSlot(infuserTileEntity.slotsInternal, 1, -24, 11));
		this.addSlotToContainer(new OutputSlot(infuserTileEntity.slotsInternal, 2, -24, 43));
		
		//Battery
		this.addSlotToContainer(new BatterySlot(infuserTileEntity.slotsInternal, 3, 8, 54));
		
		//Output
		this.addSlotToContainer(new OutputSlot(infuserTileEntity.slotsOutput, 0, 107, 32));
		
		//Upgrades
		this.addSlotToContainer(new UpgradeSlot(infuserTileEntity.slotsUpgrades, 0, -24, 76));
		this.addSlotToContainer(new UpgradeSlot(infuserTileEntity.slotsUpgrades, 1, -24, 94));
		this.addSlotToContainer(new UpgradeSlot(infuserTileEntity.slotsUpgrades, 2, -24, 112));
		
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

            if (invSlot == 1 || invSlot == 0) {
                if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }else if (invSlot != 1 && invSlot != 0){
            	if (InfuserRecipeRegistry.Infusing().getInfusingItemStackResult(itemstack1, infuserTileEntity.fluidTank.getFluid()) != null){
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
		return infuserTileEntity.isUseableByPlayer(player);
	}
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
    }
}

