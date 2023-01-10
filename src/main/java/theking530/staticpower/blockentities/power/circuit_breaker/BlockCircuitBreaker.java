package theking530.staticpower.blockentities.power.circuit_breaker;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockCircuitBreaker extends StaticPowerMachineBlock {
	public static final VoxelShape X_AXIS_SHAPE;
	public static final VoxelShape Z_AXIS_SHAPE;

	static {
		VoxelShape zAxis = Block.box(5D, 5D, 0.0D, 11D, 11D, 16.0D);
		zAxis = Shapes.join(zAxis, Block.box(4.5D, 4.5D, 2D, 11.5D, 11.5D, 14D), BooleanOp.OR);
		zAxis = Shapes.join(zAxis, Block.box(6.0D, 11.5D, 5.0D, 10.0D, 15.0D, 11.0D), BooleanOp.OR);
		Z_AXIS_SHAPE = zAxis;

		VoxelShape xAxis = Block.box(0, 5D, 5D, 16D, 11.5D, 11D);
		xAxis = Shapes.join(xAxis, Block.box(2D, 4.5D, 4.5D, 14D, 11.5D, 11.5D), BooleanOp.OR);
		xAxis = Shapes.join(xAxis, Block.box(5.0D, 11.5D, 6.0D, 11.0D, 15.0D, 10.0D), BooleanOp.OR);
		X_AXIS_SHAPE = xAxis;
	}

	private final double tripCurrent;

	public BlockCircuitBreaker(double tripCurrent) {
		super(StaticPowerTiers.BASIC);
		this.tripCurrent = tripCurrent;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(StaticPowerBlockEntityBlock.HORIZONTAL_FACING).getAxis() == Axis.Z) {
			return Z_AXIS_SHAPE;
		} else {
			return X_AXIS_SHAPE;
		}
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
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

	public Component getDisplayName(ItemStack stack) {
		return Component.translatable("block.staticpower.circuit_breaker").append(" (").append(PowerTextFormatting.formatCurrentToString(getTripCurrent()).append(")"));
	}

	public double getTripCurrent() {
		return tripCurrent;
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityCircuitBreaker.TYPE_5_A.create(pos, state);
	}
}
