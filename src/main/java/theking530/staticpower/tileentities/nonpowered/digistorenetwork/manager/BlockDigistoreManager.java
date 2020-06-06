package theking530.staticpower.tileentities.nonpowered.digistorenetwork.manager;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.nonpowered.digistorenetwork.BaseDigistoreBlock;

public class BlockDigistoreManager extends BaseDigistoreBlock {

	public BlockDigistoreManager(String name) {
		super(name);
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote) {
			return ActionResultType.PASS;
		} else if (!player.isSneaking()) {
			TileEntityDigistoreManager entity = (TileEntityDigistoreManager) world.getTileEntity(pos);
			if (entity != null) {
				// FMLNetworkHandler.openGui(player, StaticPower.staticpower,
				// GuiIDRegistry.guiIDBarrel, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return ActionResultType.CONSUME;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new TileEntityDigistoreManager();
	}
}
