package theking530.staticpower.blockentities.power.lamp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockLightSocket extends StaticPowerBlockEntityBlock {
	public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
	static {
		for (Direction shape : Direction.values()) {
			VoxelShape result = null;
			if (shape == Direction.DOWN) {
				result = Block.box(5.5D, 15D, 5.5D, 10.5D, 16D, 10.5D);
				result = Shapes.join(result, Block.box(6D, 10D, 6D, 10D, 15D, 10D), BooleanOp.OR);
			} else if (shape == Direction.UP) {
				result = Block.box(5.5D, 0D, 5.5D, 10.5D, 1D, 10.5D);
				result = Shapes.join(result, Block.box(6D, 1D, 6D, 10D, 6D, 10D), BooleanOp.OR);
			} else if (shape == Direction.WEST) {
				result = Block.box(15D, 5.5D, 5.5D, 16D, 10.5D, 10.5D);
				result = Shapes.join(result, Block.box(10D, 6D, 6D, 15D, 10D, 10D), BooleanOp.OR);
			} else if (shape == Direction.EAST) {
				result = Block.box(0D, 5.5D, 5.5D, 1D, 10.5D, 10.5D);
				result = Shapes.join(result, Block.box(1D, 6D, 6D, 6D, 10D, 10D), BooleanOp.OR);
			} else if (shape == Direction.NORTH) {
				result = Block.box(5.5D, 5.5D, 15D, 10.5D, 10.5D, 16D);
				result = Shapes.join(result, Block.box(6D, 6D, 10D, 10D, 10D, 15D), BooleanOp.OR);
			} else if (shape == Direction.SOUTH) {
				result = Block.box(5.5D, 5.5D, 0.0D, 10.5D, 10.5D, 1D);
				result = Shapes.join(result, Block.box(6D, 6D, 1D, 10D, 10D, 6D), BooleanOp.OR);
			}
			SHAPES.put(shape, result);
		}
	}

	public BlockLightSocket() {
		super(StaticPowerTiers.BASIC);
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
		super.getAdvancedTooltip(stack, worldIn, tooltip);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facingDirection = state.getValue(FACING);
		return SHAPES.get(facingDirection);
	}

	@Override
	public BlockState updateShape(BlockState p_153483_, Direction p_153484_, BlockState p_153485_, LevelAccessor p_153486_, BlockPos p_153487_, BlockPos p_153488_) {
		return !p_153483_.canSurvive(p_153486_, p_153487_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_153483_, p_153484_, p_153485_, p_153486_, p_153487_, p_153488_);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_153494_) {
		return PushReaction.DESTROY;
	}

	@Override
	public boolean canSurvive(BlockState p_57499_, LevelReader p_57500_, BlockPos p_57501_) {
		Direction mountedSide = p_57499_.getValue(FACING).getOpposite();
		return !p_57500_.getBlockState(p_57501_.relative(mountedSide)).isAir();
	}

	@Override
	public DirectionProperty getFacingType() {
		return FACING;
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BlockEntityLightSocket) {
			return ((BlockEntityLightSocket) te).isLit() ? 15 : 0;
		}
		return super.getLightEmission(state, world, pos);
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		return BlockEntityLightSocket.TYPE.create(pos, state);
	}
}
