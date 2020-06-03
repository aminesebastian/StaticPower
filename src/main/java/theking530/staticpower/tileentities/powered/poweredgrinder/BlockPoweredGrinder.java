package theking530.staticpower.tileentities.powered.poweredgrinder;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;

public class BlockPoweredGrinder extends StaticPowerTileEntityBlock {

	public BlockPoweredGrinder(String name) {
		super(name);
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			final TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityPoweredGrinder)
				NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityPoweredGrinder) tileEntity, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new TileEntityPoweredGrinder();
	}
}
