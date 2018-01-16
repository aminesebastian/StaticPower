package theking530.staticpower.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import theking530.staticpower.conduits.TileEntityBaseConduit;
import theking530.staticpower.items.ItemBase;

public class BlockReader extends ItemBase{

	public BlockReader(String name) {
		super(name);
	}
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
    	TileEntity tile = (TileEntity) world.getTileEntity(pos);
    	if(tile instanceof TileEntityBaseConduit) {
    		TileEntityBaseConduit te = (TileEntityBaseConduit) tile;
    		if(te.GRID != null) {
    			if(!world.isRemote) {
        			player.sendMessage(new TextComponentString(te.GRID.toString()));			
    			}    		
    	    	return EnumActionResult.SUCCESS;
    		}
    	}
    	return EnumActionResult.PASS;
    }
}
