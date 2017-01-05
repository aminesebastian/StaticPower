package theking530.staticpower.tileentity.solderingtable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotSolderingTable extends SlotItemHandler {

	TileEntitySolderingTable TABLE;
	EntityPlayer PLAYER;
	ContainerSolderingTable CONTAINER;
	
	public SlotSolderingTable(ContainerSolderingTable container, EntityPlayer player, TileEntitySolderingTable teTable, IItemHandler inv, int i, int j, int k) {
		super(inv, i, j, k);
		TABLE = teTable;
		PLAYER = player;
		CONTAINER = container;
	}
	@Override
    protected void onCrafting(ItemStack item, int amount){
        onCrafting(item);
        TABLE.onCrafted(PLAYER, item, amount);
    }
    public boolean isItemValid(ItemStack item){
        return false;
    }
	public void onPickupFromSlot(EntityPlayer player, ItemStack item){
        //FMLCommonHandler.instance().firePlayerCraftingEvent(player, item, TABLE);
        onCrafting(item, item.stackSize);
    }
    public void onSlotChanged() {
        super.onSlotChanged();
        CONTAINER.onSolderingAreaChanged();
    }
}
