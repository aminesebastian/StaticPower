package theking530.staticpower.items.tools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import theking530.staticpower.items.ItemBase;
import theking530.staticpower.newconduits.BaseConduitTileEntity;
import theking530.staticpower.tileentity.chunkloader.TileEntityChunkLoader;

public class BlockReader extends ItemBase{

	public BlockReader(String name) {
		super(name);
	}
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
    	TileEntity tile = (TileEntity) world.getTileEntity(pos);
    	if(tile instanceof BaseConduitTileEntity) {
    		BaseConduitTileEntity te = (BaseConduitTileEntity) tile;
    		if(te.NETWORK != null) {
    			player.addChatComponentMessage(new TextComponentString(te.NETWORK.toString()));
    	    	return EnumActionResult.SUCCESS;
    		}
    	}else if(tile instanceof TileEntityChunkLoader) {
    		TileEntityChunkLoader te = (TileEntityChunkLoader) tile;
			player.addChatComponentMessage(new TextComponentString(MinecraftServer.getServer().getPlayerProfileCache().getProfileByUUID(te.getPlacer()).getName()));
    	}
    	return EnumActionResult.PASS;
    }
}
