package theking530.staticpower.blockentities.machines.refinery.tower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockBlockStateProperties;
import theking530.staticpower.blocks.StaticPowerBlock;
import theking530.staticpower.blocks.StaticPowerBlockProperties;
import theking530.staticpower.blocks.StaticPowerBlockProperties.TowerPiece;

public class BlockRefineryTower extends StaticPowerBlock {
	private static final VoxelShape MIDDLE_SHAPE = Block.box(3, 0, 3, 13, 16, 13);
	private static final VoxelShape TOP_SHAPE = Shapes.join(MIDDLE_SHAPE, Block.box(2, 8, 2, 14, 16, 14), BooleanOp.OR);
	private static final VoxelShape BOTTOM_SHAPE = Shapes.join(MIDDLE_SHAPE, Block.box(2, 0, 2, 14, 4, 14),
			BooleanOp.OR);
	private static final VoxelShape FULL_SHAPE = Shapes.join(TOP_SHAPE, BOTTOM_SHAPE, BooleanOp.OR);

	public BlockRefineryTower() {
		super(Material.METAL);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration()
				.setValue(StaticPowerBlockProperties.TOWER_POSITION, TowerPiece.FULL)
				.setValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK, false);
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(StaticPowerBlockProperties.TOWER_POSITION,
				getTowerPiece(context.getClickedPos(), context.getLevel()));
	}

	public TowerPiece getTowerPiece(BlockPos position, BlockGetter blockgetter) {
		BlockPos abovePos = position.above();
		BlockPos belowPos = position.below();
		BlockState aboveBlockState = blockgetter.getBlockState(abovePos);
		BlockState bottomBlockState = blockgetter.getBlockState(belowPos);
		if (aboveBlockState.getBlock() == this && bottomBlockState.getBlock() == this) {
			return TowerPiece.MIDDLE;
		} else if (aboveBlockState.getBlock() == this) {
			return TowerPiece.BOTTOM;
		} else if (bottomBlockState.getBlock() == this) {
			return TowerPiece.TOP;
		} else {
			return TowerPiece.FULL;
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		TowerPiece position = state.getValue(StaticPowerBlockProperties.TOWER_POSITION);
		if (position == TowerPiece.TOP) {
			return TOP_SHAPE;
		} else if (position == TowerPiece.BOTTOM) {
			return BOTTOM_SHAPE;
		} else if (position == TowerPiece.FULL) {
			return FULL_SHAPE;
		}
		return MIDDLE_SHAPE;
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return getShape(state, worldIn, pos, context);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor level,
			BlockPos pos, BlockPos facingPos) {
		BlockState superState = super.updateShape(state, direction, facingState, level, pos, facingPos);
		return superState.setValue(StaticPowerBlockProperties.TOWER_POSITION, getTowerPiece(pos, level));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(StaticPowerBlockProperties.TOWER_POSITION);
		builder.add(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK);
	}
}
