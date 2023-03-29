package theking530.staticpower.blockentities.power.circuit_breaker;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import theking530.api.wrench.RegularWrenchMode;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockCircuitBreaker extends StaticPowerRotateableBlockEntityBlock {
	public static final VoxelShape X_AXIS_SHAPE;
	public static final VoxelShape Z_AXIS_SHAPE;

	public static final BooleanProperty TRIPPED = BooleanProperty.create("tripped");

	static {
		VoxelShape zAxis = Block.box(4.5D, 4.5D, 0.0D, 11.5D, 11.5D, 16.0D);
		zAxis = Shapes.join(zAxis, Block.box(6.0D, 11.5D, 5.0D, 10.0D, 15.0D, 11.0D), BooleanOp.OR);
		Z_AXIS_SHAPE = zAxis;

		VoxelShape xAxis = Block.box(0, 4.5D, 4.5D, 16D, 11.5D, 11.5D);
		xAxis = Shapes.join(xAxis, Block.box(5.0D, 11.5D, 6.0D, 11.0D, 15.0D, 10.0D), BooleanOp.OR);
		X_AXIS_SHAPE = xAxis;
	}

	private final double tripCurrent;

	public BlockCircuitBreaker(double tripCurrent) {
		super(StaticPowerTiers.BASIC);
		this.tripCurrent = tripCurrent;
	}

	public double getTripCurrent() {
		return tripCurrent;
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		return super.getDefaultStateForRegistration().setValue(TRIPPED, false);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(TRIPPED);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING).getAxis() == Axis.Z) {
			return Z_AXIS_SHAPE;
		} else {
			return X_AXIS_SHAPE;
		}
	}

	@Override
	protected boolean canBeWaterlogged() {
		return true;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (facing != Direction.UP && facing != Direction.DOWN) {
			if (facing != world.getBlockState(pos).getValue(getFacingType())) {
				world.setBlock(pos, world.getBlockState(pos).setValue(getFacingType(), facing), 1 | 2);
			} else {
				world.setBlock(pos, world.getBlockState(pos).setValue(getFacingType(), facing.getOpposite()), 1 | 2);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {

	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
		BlockState parent = super.getStateForPlacement(p_49820_);
		return parent.setValue(TRIPPED, false);
	}

	@Override
	public Component getDisplayName(ItemStack stack) {
		return Component.translatable("block.staticpower.circuit_breaker").append(" (").append(PowerTextFormatting.formatCurrentToString(getTripCurrent()).append(")"));
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityCircuitBreaker.TYPE.create(pos, state);
	}
}
