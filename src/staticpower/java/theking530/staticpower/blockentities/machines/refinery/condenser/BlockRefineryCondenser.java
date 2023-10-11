package theking530.staticpower.blockentities.machines.refinery.condenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticcore.utilities.rendering.VoxelUtilities;
import theking530.staticpower.blocks.StaticPowerBlockProperties;
import theking530.staticpower.blocks.StaticPowerBlockProperties.TowerPiece;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockRefineryCondenser extends StaticPowerMachineBlock {
	public static final BooleanProperty IS_CONNECTED = BooleanProperty.create("is_connected");

	public BlockRefineryCondenser() {
		super(StaticPowerTiers.STATIC);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		BlockEntityRefineryCondenser te = (BlockEntityRefineryCondenser) tileEntity;
		return te.hasController() ? HasGuiType.ALWAYS : HasGuiType.NEVER;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IS_CONNECTED);
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(IS_CONNECTED, false);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = super.getStateForPlacement(context);
		return blockstate.setValue(IS_CONNECTED,
				isConnectedToTower(context.getLevel(), blockstate, context.getClickedPos()));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor world,
			BlockPos pos, BlockPos facingPos) {
		BlockState superState = super.updateShape(state, dir, facingState, world, pos, facingPos);
		superState = superState.setValue(IS_CONNECTED, isConnectedToTower(world, superState, pos));
		return superState;
	}

	private boolean isConnectedToTower(LevelAccessor level, BlockState state, BlockPos pos) {
		Direction facingDirection = state.getValue(StaticCoreBlock.HORIZONTAL_FACING);
		BlockPos facingPos = pos.relative(facingDirection.getOpposite());
		BlockState facingState = level.getBlockState(facingPos);

		if (!facingState.hasProperty(StaticPowerBlockProperties.TOWER_POSITION)) {
			return false;
		}

		TowerPiece towerPosition = facingState.getValue(StaticPowerBlockProperties.TOWER_POSITION);
		return towerPosition == TowerPiece.MIDDLE || towerPosition == TowerPiece.BOTTOM;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityRefineryCondenser.TYPE.create(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING);

		VoxelShape body = Shapes.join(Block.box(3.5, 3.5, 1.5, 12.5, 12.5, 3.5),
				Block.box(3.5, 3.5, 12.5, 12.5, 12.5, 14.5), BooleanOp.OR);
		body = Shapes.join(body, Block.box(4, 4, 3.5, 12, 12, 12.5), BooleanOp.OR);
		body = Shapes.join(body, Block.box(5, 5, 0, 11, 11, 16), BooleanOp.OR);
		body = Shapes.join(body, Block.box(12.5, 9, 10, 15.5, 11, 12), BooleanOp.OR);
		body = Shapes.join(body, Block.box(13.5, 9, 10, 15.5, 13, 12), BooleanOp.OR);
		body = Shapes.join(body, Block.box(13.25, 12.5, 9.75, 15.75, 14, 12.25), BooleanOp.OR);
		return VoxelUtilities.rotateAlongYAxis(Direction.NORTH, facing, body);
	}
}
