package theking530.staticpower.blockentities.machines.pump;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticcore.utilities.VoxelUtilities;
import theking530.staticpower.blocks.StaticPowerBlock;

public class BlockPumpTube extends StaticPowerBlock {
	public static final BooleanProperty AUTOMATICALLY_PLACED = BooleanProperty.create("automatically_placed");

	protected static final Map<Direction, VoxelShape> SHAPE;
	static {
		SHAPE = new HashMap<>();
		VoxelShape shape = Shapes.join(Block.box(5.5, 5.5, 0, 10.5, 10.5, 16), Shapes.join(Block.box(5, 5, 0, 11, 11, 3), Block.box(5, 5, 13, 11, 11, 16), BooleanOp.OR),
				BooleanOp.OR);
		for (Direction dir : Direction.values()) {
			SHAPE.put(dir, VoxelUtilities.rotateShape(Direction.NORTH, dir, shape));
		}
	}

	public BlockPumpTube() {
		super(Material.METAL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected boolean canBeWaterlogged() {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(AUTOMATICALLY_PLACED);
		builder.add(FACING);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(AUTOMATICALLY_PLACED, false).setValue(FACING, Direction.UP);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		state = state.setValue(AUTOMATICALLY_PLACED, false);
		state = state.setValue(FACING, context.getClickedFace());
		return state;
	}
}
