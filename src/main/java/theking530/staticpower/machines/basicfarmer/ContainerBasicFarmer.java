package theking530.staticpower.machines.basicfarmer;

import cofh.redstoneflux.api.IEnergyContainerItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.items.upgrades.IMachineUpgrade;

public class ContainerBasicFarmer extends Container {
	
	private TileEntityBasicFarmer FARMER;
	
	public ContainerBasicFarmer(InventoryPlayer invPlayer, TileEntityBasicFarmer teFarmer) {
		FARMER = teFarmer;
		
        for (int l = 0; l < 3; ++l) {
            for (int i1 = 0; i1 < 3; ++i1){
                this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_OUTPUT, i1 + l * 3, 87 + i1 * 18, 30 + l * 18));
            }
        }
        
		//Buckets
		this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_INPUT, 2, 7, 17) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
		        }
		});
		this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_OUTPUT, 9, 7, 47) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return false;
		        }
		});
		
		//Hoe
		this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_INPUT, 0, 90, 8) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof ItemHoe;
		        }
		});
		//Axe
		this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_INPUT, 1, 120, 8) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof ItemAxe;
		        }
		});

		this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_INPUT, 3, 27, 71) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof IEnergyContainerItem;
		        }
		});
		for(int y=0; y<3; y++) {
			this.addSlotToContainer(new SlotItemHandler(teFarmer.SLOTS_UPGRADES, y, 171, 12+(y*20)) {
				@Override
		        public boolean isItemValid(ItemStack itemStack) {
			          return itemStack.getItem() instanceof IMachineUpgrade;
			    }
			});
		}
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 27 + j * 18, 90 + i * 18));
			}
		}
		
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 27+ i * 18, 148));
		}
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
		return FARMER.isUseableByPlayer(player);
	}
	
	//Detect Changes
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
}

