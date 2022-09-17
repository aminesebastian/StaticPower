package theking530.staticpower.blockentities.power.wireconnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockWireConnector extends StaticPowerBlockEntityBlock {
	public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();
	static {
		for (Direction shape : Direction.values()) {
			VoxelShape result = null;
			if (shape == Direction.DOWN) {
				result = Block.box(5.5D, 15.0D, 5.5D, 10.5D, 16.0D, 10.5D);
				result = Shapes.join(result, Block.box(6.0D, 14.0D, 6.0D, 10D, 15.0D, 10D), BooleanOp.OR);
				result = Shapes.join(result, Block.box(6.5D, 10.0D, 6.5D, 9.5D, 14.0D, 9.5D), BooleanOp.OR);
			} else if (shape == Direction.UP) {
				result = Block.box(5.5D, 0.0D, 5.5D, 10.5D, 1.0D, 10.5D);
				result = Shapes.join(result, Block.box(6.0D, 1.0D, 6.0D, 10D, 2.0D, 10D), BooleanOp.OR);
				result = Shapes.join(result, Block.box(6.5D, 2.0D, 6.5D, 9.5D, 6.0D, 9.5D), BooleanOp.OR);
			} else if (shape == Direction.WEST) {
				result = Block.box(15D, 5.5D, 5.5D, 16.0D, 10.5D, 10.5D);
				result = Shapes.join(result, Block.box(14.0D, 6.0D, 6.0D, 15D, 10.0D, 10D), BooleanOp.OR);
				result = Shapes.join(result, Block.box(10D, 6.5D, 6.5D, 14D, 9.5D, 9.5D), BooleanOp.OR);
			} else if (shape == Direction.EAST) {
				result = Block.box(0D, 5.5D, 5.5D, 1.0D, 10.5D, 10.5D);
				result = Shapes.join(result, Block.box(1.0D, 6.0D, 6.0D, 2D, 10.0D, 10D), BooleanOp.OR);
				result = Shapes.join(result, Block.box(2D, 6.5D, 6.5D, 6D, 9.5D, 9.5D), BooleanOp.OR);
			} else if (shape == Direction.NORTH) {
				result = Block.box(5.5D, 5.5D, 15D, 10.5D, 10.5D, 16.0D);
				result = Shapes.join(result, Block.box(6.0D, 6.0D, 14.0D, 10D, 10.0D, 15D), BooleanOp.OR);
				result = Shapes.join(result, Block.box(6.5D, 6.5D, 10.0D, 9.5D, 9.5D, 14D), BooleanOp.OR);
			} else if (shape == Direction.SOUTH) {
				result = Block.box(5.5D, 5.5D, 0D, 10.5D, 10.5D, 1.0D);
				result = Shapes.join(result, Block.box(6.0D, 6.0D, 1.0D, 10D, 10.0D, 2D), BooleanOp.OR);
				result = Shapes.join(result, Block.box(6.5D, 6.5D, 2.0D, 9.5D, 9.5D, 6D), BooleanOp.OR);
			}
			SHAPES.put(shape, result);
		}
	}

	public BlockWireConnector(ResourceLocation tier) {
		super(tier);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);
		StaticPowerTier tierObject = StaticPowerConfig.getTier(tier);
		PowerTooltips.addVoltageInputTooltip(tooltip, new StaticVoltageRange(StaticPowerVoltage.LOW, tierObject.cablePowerConfiguration.wireTerminalMaxVoltage.get()));
		PowerTooltips.addMaximumPowerTransferTooltip(tooltip, tierObject.cablePowerConfiguration.wireTerminalMaxPower.get());
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
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityWireConnector.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityWireConnector.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityWireConnector.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityWireConnector.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityWireConnector.TYPE_LUMUM.create(pos, state);
		}
		return null;
	}

	public DirectionProperty getFacingType() {
		return FACING;
	}
}
