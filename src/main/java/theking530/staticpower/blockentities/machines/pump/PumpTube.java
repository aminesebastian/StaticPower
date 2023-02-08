package theking530.staticpower.blockentities.machines.pump;

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
	protected static final VoxelShape SHAPE;
	static {
		VoxelShape shape = Shapes.join(Block.box(6, 0, 6, 10, 16, 10), Shapes.join(Block.box(5, 0, 5, 11, 3, 11), Block.box(5, 13, 5, 11, 16, 11), BooleanOp.OR), BooleanOp.OR);
		for (int i = 0; i < 5; i++) {
			double start = 3.5 + (i * 2);
			shape = Shapes.join(Block.box(5.5, start, 5.5, 10.5, start + 1, 10.5), shape, BooleanOp.OR);
		}
		SHAPE = shape;
	}

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
