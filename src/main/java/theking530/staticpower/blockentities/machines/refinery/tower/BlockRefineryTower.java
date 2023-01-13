package theking530.staticpower.blockentities.machines.refinery.tower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.staticpower.blocks.StaticPowerBlock;

public class BlockRefineryTower extends StaticPowerBlock {
	public enum TowerPiece implements StringRepresentable {
		FULL("full"), MIDDLE("middle"), TOP("top"), BOTTOM("bottom");

		private final String name;

		TowerPiece(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public static final EnumProperty<TowerPiece> TOWER_POSITION = EnumProperty.create("position", TowerPiece.class);

	private static final VoxelShape MIDDLE_SHAPE = Block.box(3, 0, 3, 13, 16, 13);
	private static final VoxelShape TOP_SHAPE = Shapes.join(MIDDLE_SHAPE, Block.box(2, 8, 2, 14, 16, 14), BooleanOp.OR);
	private static final VoxelShape BOTTOM_SHAPE = Shapes.join(MIDDLE_SHAPE, Block.box(2, 0, 2, 14, 4, 14), BooleanOp.OR);
	private static final VoxelShape FULL_SHAPE = Shapes.join(TOP_SHAPE, BOTTOM_SHAPE, BooleanOp.OR);

	public BlockRefineryTower() {
		super(Material.METAL);
	}

	@Override
	protected BlockState getDefaultState() {
		return stateDefinition.any().setValue(TOWER_POSITION, TowerPiece.FULL);
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(TOWER_POSITION, getTowerPiece(context.getClickedPos(), context.getLevel()));
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
		TowerPiece position = state.getValue(TOWER_POSITION);
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

	public BlockState updateShape(BlockState p_53323_, Direction p_53324_, BlockState p_53325_, LevelAccessor p_53326_, BlockPos p_53327_, BlockPos blockPos) {
		return p_53323_.setValue(TOWER_POSITION, getTowerPiece(p_53327_, p_53326_));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_53334_) {
		p_53334_.add(TOWER_POSITION);
	}
}
