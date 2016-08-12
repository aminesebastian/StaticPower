package theking530.staticpower.machines.solderingtable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class SlotSolderingTable extends Slot {

	TileEntitySolderingTable TABLE;
	EntityPlayer PLAYER;
	ContainerSolderingTable CONTAINER;
	
	public SlotSolderingTable(ContainerSolderingTable container, EntityPlayer player, TileEntitySolderingTable teTable, int i, int j, int k) {
		super(teTable, i, j, k);
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
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, item, TABLE);
        onCrafting(item, item.stackSize);
    }
    public void onSlotChanged() {
        super.onSlotChanged();
        CONTAINER.onSolderingAreaChanged();
    }
}
