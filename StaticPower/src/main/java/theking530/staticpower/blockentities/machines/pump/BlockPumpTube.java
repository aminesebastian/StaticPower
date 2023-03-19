package theking530.staticpower.blockentities.machines.pump;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticcore.utilities.rendering.VoxelUtilities;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerBlockProperties;
import theking530.staticpower.blocks.StaticPowerBlockProperties.TowerPiece;

public class BlockPumpTube extends StaticPowerBlock {
	protected static final Map<Direction, VoxelShape> MIDDLE_SHAPES;
	static {
		MIDDLE_SHAPES = new HashMap<>();
		VoxelShape middleShape = Block.box(5.5, 5.5, 0, 10.5, 10.5, 16);
		for (Direction dir : Direction.values()) {
			MIDDLE_SHAPES.put(dir, VoxelUtilities.rotateShape(Direction.NORTH, dir, middleShape));
		}
	}

	public BlockPumpTube() {
		super(Material.METAL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return MIDDLE_SHAPES.get(state.getValue(FACING));
	}

	@Override
	protected boolean canBeWaterlogged() {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(StaticPowerBlockProperties.TOWER_POSITION);
		builder.add(FACING);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(StaticPowerBlockProperties.TOWER_POSITION, TowerPiece.FULL).setValue(FACING, Direction.UP);
	}

	public TowerPiece getTubePiece(BlockState superState, BlockPos position, BlockGetter blockgetter) {
		Direction facingDirection = superState.getValue(FACING);

		BlockPos forwardPos = position.relative(facingDirection);
		BlockPos backPos = position.relative(facingDirection.getOpposite());

		BlockState forwardBlockState = blockgetter.getBlockState(forwardPos);
		Direction forwardDirection = forwardBlockState.hasProperty(FACING) ? forwardBlockState.getValue(FACING) : Direction.UP;

		BlockState backBlockState = blockgetter.getBlockState(backPos);
		Direction backDirection = backBlockState.hasProperty(FACING) ? backBlockState.getValue(FACING) : Direction.UP;

		if (forwardBlockState.getBlock() == this && backBlockState.getBlock() == this && facingDirection.getAxis() == forwardDirection.getAxis()
				&& facingDirection.getAxis() == backDirection.getAxis()) {
			return TowerPiece.MIDDLE;
		} else if (forwardBlockState.getBlock() == this && facingDirection.getAxis() == forwardDirection.getAxis()) {
			return TowerPiece.BOTTOM;
		} else if (backBlockState.getBlock() == this && facingDirection.getAxis() == backDirection.getAxis()) {
			return TowerPiece.TOP;
		} else {
			return TowerPiece.FULL;
		}
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
		BlockState superState = super.updateShape(state, direction, facingState, level, pos, facingPos);
		return superState.setValue(StaticPowerBlockProperties.TOWER_POSITION, getTubePiece(state, pos, level));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		state = state.setValue(StaticPowerBlockProperties.TOWER_POSITION, getTubePiece(state, context.getClickedPos(), context.getLevel()));
		state = state.setValue(FACING, context.getClickedFace());
		return state;
	}
}
