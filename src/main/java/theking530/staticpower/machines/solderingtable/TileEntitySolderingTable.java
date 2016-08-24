package theking530.staticpower.machines.solderingtable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import theking530.staticpower.items.tools.ISolderingIron;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntitySolderingTable extends BaseTileEntity {
		
	private int SOLDERING_IRON_DAMAGE = 1;
	
	public TileEntitySolderingTable() {
		initializeBasicTileEntity(17, 1, 0);
	}
	@Override
	public String getName() {
		return "SolderingTable";		
	}
	public void onCrafted(EntityPlayer player, ItemStack item, int amount) {
		MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, getInputStack(17), EnumHand.MAIN_HAND));
		if(getInputStack(17).getItem() instanceof ISolderingIron) {
			if(!worldObj.isRemote) {
				ISolderingIron tempIron = (ISolderingIron) getInputStack(17).getItem();
				tempIron.useSolderingItem(getInputStack(17));		
			}
		}
    	boolean flag = false;
		for (int i = 0; i < 9; ++i){
    		flag = false;
            ItemStack itemstack1 = getInputStack(i);
            if (itemstack1 != null) {
            	for(int j=9; j<16; j++) {
            		if(getInputStack(j) != null) {
            			if(getInputStack(j).isItemEqual(getInputStack(i))) {
            				SLOTS_INPUT.extractItem(j, 1, false);
            				flag = true;
            			}
            		}
            	}
            }
            if(!flag) {
				SLOTS_INPUT.extractItem(i, 1, false);
            }
		}
	}
}
