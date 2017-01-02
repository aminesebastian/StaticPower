package theking530.staticpower.machines.solderingtable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import theking530.staticpower.handlers.crafting.registries.SolderingRecipeRegistry;
import theking530.staticpower.items.tools.ISolderingIron;
import theking530.staticpower.items.tools.SolderingIron;

public class ContainerSolderingTable extends Container {
	
 /** The crafting matrix inventory (3x3). */
    private TileEntitySolderingTable SOLDERING_TABLE;
    private static final String __OBFID = "CL_00001744";

    public ContainerSolderingTable(InventoryPlayer invPlayer, TileEntitySolderingTable teTable) {
        SOLDERING_TABLE = teTable;
        int l;
        int i1;
               
        //Crafting Area 0 - 8
        for (l = 0; l < 3; l++) {
            for (i1 = 0; i1 < 3; i1++){
                this.addSlotToContainer(new SlotSolderingTableInput(this, teTable.SLOTS_INPUT, i1 + l * 3, 62 + i1 * 18, 17 + l * 18));
            }
        }
        
        
        //Extra Slots 9 - 15
        for (l = 0; l < 7; l++) {
        	this.addSlotToContainer(new SlotItemHandler(teTable.SLOTS_INPUT, l+9, 26 + l * 18, 74));
        }
        
        //Output
        this.addSlotToContainer(new SlotSolderingTable(this, invPlayer.player, teTable, teTable.SLOTS_OUTPUT, 0, 140, 39));
        
        //Soldering Iron
		this.addSlotToContainer(new SlotSolderingTableInput(this, teTable.SLOTS_INPUT, 16, 11, 17) {
			@Override
	        public boolean isItemValid(ItemStack itemStack) {
		          return itemStack.getItem() instanceof ISolderingIron;
		        }
		});
        
		//Inventory
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 97 + i * 18));
			}
		}
		
		//ActionBar
		for(int i = 0; i < 9; i++) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 155));
		}
        this.detectAndSendChanges();
    }
    public void onSolderingAreaChanged(){
    	if(SOLDERING_TABLE.getInputStack(16) != null && SOLDERING_TABLE.getInputStack(16).getItem() instanceof ISolderingIron) {
    		ISolderingIron tempIron = (ISolderingIron) SOLDERING_TABLE.getInputStack(16).getItem();
    		if(tempIron.canSolder(SOLDERING_TABLE.getInputStack(16))) {
    			SOLDERING_TABLE.SLOTS_OUTPUT.setStackInSlot(0, SolderingRecipeRegistry.Soldering().findSolderingOutput(SOLDERING_TABLE.SLOTS_INPUT, SOLDERING_TABLE.getWorld())); 		
    		}else{
        		SOLDERING_TABLE.SLOTS_OUTPUT.setStackInSlot(0, null); 	
    		}    	
    	}else{
    		SOLDERING_TABLE.SLOTS_OUTPUT.setStackInSlot(0, null); 
    	}
    }
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_){
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack()){
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (p_82846_2_ == 0){
                if (!this.mergeItemStack(itemstack1, 10, 46, true)){
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }else if (p_82846_2_ >= 10 && p_82846_2_ < 37){
                if (!this.mergeItemStack(itemstack1, 37, 46, false)){
                    return null;
                }
            }else if (p_82846_2_ >= 37 && p_82846_2_ < 46){
                if (!this.mergeItemStack(itemstack1, 10, 37, false)){
                    return null;
                }
            }else if (!this.mergeItemStack(itemstack1, 10, 46, false)){
                return null;
            }
            if (itemstack1.stackSize == 0){
                slot.putStack((ItemStack)null);
            }else{
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize){
                return null;
            }
            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }
        return itemstack;
    }
	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}
}
