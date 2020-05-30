package theking530.staticpower.tileentities.powered.poweredfurnace;

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

public class BlockPoweredFurnace extends StaticPowerTileEntityBlock {

	public BlockPoweredFurnace(String name) {
		super(name);
	}

	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			final TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityPoweredFurnace)
				NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityPoweredFurnace) tileEntity, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new TileEntityPoweredFurnace();
	}
}
