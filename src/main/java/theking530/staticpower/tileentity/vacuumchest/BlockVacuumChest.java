package theking530.staticpower.tileentity.vacuumchest;

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
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentity.BlockMachineBase;

public class BlockVacuumChest extends BlockMachineBase {

	public BlockVacuumChest(String name) {
		super(name);
	}

	/**
	 * Called when a player right clicks our block. We use this method to open our
	 * gui.
	 *
	 * @deprecated Call via
	 *             {@link BlockState#onBlockActivated(World, PlayerEntity, Hand, BlockRayTraceResult)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	/**
	 * Called when a player right clicks our block. We use this method to open our
	 * gui.
	 *
	 * @deprecated Call via
	 *             {@link BlockState#onBlockActivated(World, PlayerEntity, Hand, BlockRayTraceResult)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
		if (!worldIn.isRemote) {
			final TileEntity tileEntity = worldIn.getTileEntity(pos);
			if (tileEntity instanceof TileEntityVacuumChest)
				NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityVacuumChest) tileEntity, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.VACCUM_CHEST.create();
	}
}
