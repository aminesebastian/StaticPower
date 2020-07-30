package theking530.staticpower.tileentities.powered.basicfarmer;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.StaticPowerMachineBlock;

public class BlockBasicFarmer extends StaticPowerMachineBlock {

	public BlockBasicFarmer(String name) {
		super(name);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ModTileEntityTypes.BASIC_FARMER.create();
	}
}
