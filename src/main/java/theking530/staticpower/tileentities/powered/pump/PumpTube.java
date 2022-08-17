package theking530.staticpower.tileentities.powered.pump;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticpower.blocks.StaticPowerBlock;

public class PumpTube extends StaticPowerBlock {
	protected static final VoxelShape SHAPE = Shapes.join(Block.box(4.5, 0, 4.5, 11.5, 16, 11.5),
			Shapes.join(Block.box(3.5, 0, 3.5, 12.5, 4, 12.5), Block.box(3.5, 12, 3.5, 12.5, 16, 12.5), BooleanOp.OR), BooleanOp.OR);

	public PumpTube() {
		super(Material.METAL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
}
