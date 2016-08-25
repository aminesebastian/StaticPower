package theking530.staticpower.machines.cropsqueezer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.SqueezerRecipeRegistry;

public class ContainerCropSqueezer extends Container {
	
	private TileEntityCropSqueezer CropSqueezer;
	private int PROCESSING_TIMER;
	private int FLUID_AMOUNT;
	private int ENERGY_STORED;
	private int FLUID_ID;
	
	private int upgradeSlotX;
	private int upgradeSlotY;
	public ContainerCropSqueezer(InventoryPlayer invPlayer, TileEntityCropSqueezer teCropSqueezer) {
		PROCESSING_TIMER = 0;

		CropSqueezer = teCropSqueezer;
		
		//Input
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.SLOTS_INPUT, 0, 84, 16));
		
		//Output
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.SLOTS_OUTPUT, 0, 84, 56) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemStack) != null;
		        }
		});
		
		//Upgrades
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.SLOTS_UPGRADES, 0, upgradeSlotX+152, upgradeSlotY+12));
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.SLOTS_UPGRADES, 1, upgradeSlotX+152, upgradeSlotY+32));
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.SLOTS_UPGRADES, 2, upgradeSlotX+152, upgradeSlotY+52));
		
		//Processing
		this.addSlotToContainer(new SlotItemHandler(teCropSqueezer.SLOTS_INTERNAL, 0, 10000, 10000));
		
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
	public void moveUpgradeSlots(int x, int y) {
		this.upgradeSlotY = y;
		this.upgradeSlotX = x;
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
                if (SqueezerRecipeRegistry.Squeezing().getSqueezingItemResult(itemstack1) != null){
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)){
                        return null;
                    }
                }else if (invSlot >= 6 && invSlot < 33) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return null;
                    }
                }else if (invSlot >= 33 && invSlot < 42 && !this.mergeItemStack(itemstack1, 6, 33, false))  {
                    return null;
                }
            }else if (!this.mergeItemStack(itemstack1, 6, 42, false)) {
                return null;
            }
            if (itemstack1.stackSize == 0){
                slot.putStack((ItemStack)null);
            }else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize == itemstack.stackSize){
                return null;
            }
            slot.onPickupFromSlot(player, itemstack1);
        }
        return itemstack;
    }

	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return CropSqueezer.isUseableByPlayer(player);
	}
	
	public void detectAndSendChanges(){
        super.detectAndSendChanges();
        for (int i = 0; i < this.listeners.size(); ++i){
            IContainerListener icontainerlistener = (IContainerListener)this.listeners.get(i);
            if(PROCESSING_TIMER != CropSqueezer.PROCESSING_TIMER){
                icontainerlistener.sendProgressBarUpdate(this, 0, CropSqueezer.PROCESSING_TIMER);
            }
            if(ENERGY_STORED != CropSqueezer.STORAGE.getEnergyStored()) {
                icontainerlistener.sendProgressBarUpdate(this, 1, CropSqueezer.STORAGE.getEnergyStored());
            }
            if(FLUID_AMOUNT != CropSqueezer.TANK.getFluidAmount()) {
                icontainerlistener.sendProgressBarUpdate(this, 2, CropSqueezer.TANK.getFluidAmount());
            }
        }
        PROCESSING_TIMER = CropSqueezer.PROCESSING_TIMER;
        ENERGY_STORED = CropSqueezer.STORAGE.getEnergyStored();
        FLUID_AMOUNT = CropSqueezer.TANK.getFluidAmount();
    }
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
    	if(id == 0) {
    		CropSqueezer.PROCESSING_TIMER = data;
    	}else if(id == 1) {
    		CropSqueezer.STORAGE.setEnergyStored(data);
    	}
    }
}

