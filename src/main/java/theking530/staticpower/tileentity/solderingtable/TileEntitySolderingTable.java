package theking530.staticpower.tileentity.solderingtable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import theking530.staticpower.assists.utilities.SideModeList.Mode;
import theking530.staticpower.items.tools.ISolderingIron;
import theking530.staticpower.machines.tileentitycomponents.TileEntityItemInputServo;
import theking530.staticpower.tileentity.BaseTileEntity;

public class TileEntitySolderingTable extends BaseTileEntity {
		
	public TileEntitySolderingTable() {
		initializeSlots(1, 17, 1);
		registerComponent(new TileEntityItemInputServo(this, 2, slotsInput, 9, 10, 11, 12, 13, 14, 15));
	}
	@Override
	public String getName() {
		return "SolderingTable";		
	}
	public void onCrafted(EntityPlayer player, ItemStack item, int amount) {
		MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, getInputStack(16), EnumHand.MAIN_HAND));
		if(getInputStack(16).getItem() instanceof ISolderingIron) {
			if(!getWorld().isRemote) {
				ISolderingIron tempIron = (ISolderingIron) getInputStack(16).getItem();
				if(!tempIron.canSolder(getInputStack(16))) {
					return;
				}
				if(tempIron.useSolderingItem(getInputStack(16))) {
					slotsInput.setStackInSlot(16, ItemStack.EMPTY);
					getWorld().playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0f, 1, false);		
				}
			}
		}
    	boolean flag = false;
		for (int i = 0; i < 9; ++i){
    		flag = false;
            ItemStack itemstack1 = getInputStack(i);
            if (itemstack1 != ItemStack.EMPTY) {
            	for(int j=9; j<16; j++) {
            		if(getInputStack(j) != ItemStack.EMPTY && getInputStack(j).isItemEqual(getInputStack(i))) {
        				slotsInput.extractItem(j, 1, false);
        				flag = true;
        				break;
            		}
            	}
                if(!flag) {
    				slotsInput.extractItem(i, 1, false);
                }
            }
		}
	}
	@Override
	public List<Mode> getValidSideConfigurations() {
		List<Mode> modes = new ArrayList<Mode>();
		modes.add(Mode.Input);
		modes.add(Mode.Regular);
		modes.add(Mode.Disabled);
		return modes;
	}
}
