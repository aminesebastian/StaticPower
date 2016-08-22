package theking530.staticpower.machines.solderingtable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import theking530.staticpower.machines.BaseTileEntity;
import theking530.staticpower.utils.StaticMath;

public class TileEntitySolderingTable extends BaseTileEntity {
		
	private int SOLDERING_IRON_DAMAGE = 1;
	
	public TileEntitySolderingTable() {
		initializeBasicTileEntity(45, null, null);
	}
	@Override
	public String getName() {
		return "SolderingTable";		
	}
	public void onCrafted(EntityPlayer player, ItemStack item, int amount) {
		MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, slots[18], EnumHand.MAIN_HAND));
		slots[18].setItemDamage(slots[18].getItemDamage() + SOLDERING_IRON_DAMAGE);
		if(slots[18].getItemDamage() >= slots[18].getMaxDamage()) {
			slots[18] = null;
		}
    	boolean flag = false;
		for (int i = 0; i < 9; ++i){
    		flag = false;
            ItemStack itemstack1 = getStackInSlot(i);
            if (itemstack1 != null) {
            	for(int j=9; j<16; j++) {
            		if(slots[j] != null) {
            			if(slots[j].isItemEqual(slots[i])) {
            				decrStackSize(j, 1);
            				flag = true;
            			}
            		}
            	}
            }
            if(!flag) {
				decrStackSize(i, 1);
            }
		}
	}
}
