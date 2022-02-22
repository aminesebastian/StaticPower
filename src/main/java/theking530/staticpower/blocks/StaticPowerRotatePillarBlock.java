package theking530.staticpower.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class StaticPowerRotatePillarBlock extends StaticPowerBlock {
	public StaticPowerRotatePillarBlock(String name, Properties properties) {
		super(name, properties);
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
		switch (direction) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch ((Direction.Axis) state.getValue(AXIS)) {
			case X:
				return state.setValue(AXIS, Direction.Axis.Z);
			case Z:
				return state.setValue(AXIS, Direction.Axis.X);
			default:
				return state;
			}
		default:
			return state;
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
	}
}
