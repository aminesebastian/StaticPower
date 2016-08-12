package theking530.staticpower.machines.quarry;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import theking530.staticpower.StaticPower;
import theking530.staticpower.client.GuiIDRegistry;
import theking530.staticpower.items.tools.CoordinateMarker;
import theking530.staticpower.machines.BaseMachineBlock;
import theking530.staticpower.machines.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.machines.solderingtable.TileEntitySolderingTable;
import theking530.staticpower.utils.EnumTextFormatting;
import theking530.staticpower.utils.WorldUtilities;

public class BlockQuarry extends BaseMachineBlock{

	public BlockQuarry() {
		super("Quarry");
		setHardness(3.5f);
	    setResistance(5.0f);
	}
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if (world.isRemote) {
    		return true;
    	}else if (!player.isSneaking()) {
    		TileEntityQuarry entity = (TileEntityQuarry) world.getTileEntity(pos);
			if(player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof CoordinateMarker) {
				NBTTagCompound tempTag = player.getHeldItemMainhand().getTagCompound();
				if(tempTag.getIntArray("POS1").length > 2 && tempTag.getIntArray("POS2").length > 2) {
					BlockPos start = new BlockPos(tempTag.getIntArray("POS1")[0], tempTag.getIntArray("POS1")[1], tempTag.getIntArray("POS1")[2]);
					BlockPos end = new BlockPos(tempTag.getIntArray("POS2")[0], tempTag.getIntArray("POS2")[1], tempTag.getIntArray("POS2")[2]);
					if(WorldUtilities.getAreaBetweenCorners(start, end) <= 200000) {
						if(start.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 70 || end.getDistance(pos.getX(), pos.getY(), pos.getZ()) < 70) {
							entity.setCoordinates(start, end);
							player.addChatComponentMessage(new TextComponentString("Coordinates Set!"));	
						}else{
							player.addChatComponentMessage(new TextComponentString("Area too far! The area must be less than 70 blocks away."));						
						}
					}else{
						player.addChatComponentMessage(new TextComponentString("Area too large! The area must be less than 200,000 blocks."));						
					}
					return true;
				}
			}
    		if (entity != null) {
    			FMLNetworkHandler.openGui(player, StaticPower.staticpower, GuiIDRegistry.guiIDQuarry, world, pos.getX(), pos.getY(), pos.getZ());
    		}
    		return true;
    	}else{
    		return false;
    	}
	}
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityQuarry();
	}
}
