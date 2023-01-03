package theking530.staticpower.blockentities.power.transformer;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import theking530.staticcore.gui.text.PowerTooltips;
import theking530.staticcore.gui.text.TooltipUtilities;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockTransformer extends StaticPowerMachineBlock {
	private static final HashMap<Direction, VoxelShape> SHAPES = new HashMap<>();

	static {
		VoxelShape base = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
		base = Shapes.join(base, Block.box(1.0D, 2.0D, 1.0D, 15.0D, 4.0D, 15.0D), BooleanOp.OR);

		VoxelShape north = Shapes.join(base, Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 2.0D), BooleanOp.OR);
		north = Shapes.join(north, Block.box(5.0D, 5.0D, 14.0D, 11.0D, 11.0D, 16.0D), BooleanOp.OR);
		north = Shapes.join(north, Block.box(2.0D, 4.0D, 2.0D, 7.5D, 14.0D, 7.5D), BooleanOp.OR);
		north = Shapes.join(north, Block.box(2.0D, 4.0D, 8.5D, 7.5D, 16.0D, 14.0D), BooleanOp.OR);
		north = Shapes.join(north, Block.box(8.5D, 4.0D, 2.0D, 14.0D, 14.0D, 7.5D), BooleanOp.OR);
		north = Shapes.join(north, Block.box(8.5D, 4.0D, 8.5D, 14.0D, 16.0D, 14.0D), BooleanOp.OR);
		SHAPES.put(Direction.NORTH, north);

		VoxelShape south = Shapes.join(base, Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 2.0D), BooleanOp.OR);
		south = Shapes.join(south, Block.box(5.0D, 5.0D, 14.0D, 11.0D, 11.0D, 16.0D), BooleanOp.OR);
		south = Shapes.join(south, Block.box(2.0D, 4.0D, 2.0D, 7.5D, 16.0D, 7.5D), BooleanOp.OR);
		south = Shapes.join(south, Block.box(2.0D, 4.0D, 8.5D, 7.5D, 14.0D, 14.0D), BooleanOp.OR);
		south = Shapes.join(south, Block.box(8.5D, 4.0D, 2.0D, 14.0D, 16.0D, 7.5D), BooleanOp.OR);
		south = Shapes.join(south, Block.box(8.5D, 4.0D, 8.5D, 14.0D, 14.0D, 14.0D), BooleanOp.OR);
		SHAPES.put(Direction.SOUTH, south);

		VoxelShape east = Shapes.join(base, Block.box(0.0D, 5.0D, 5.0D, 2.0D, 11.0D, 11.0D), BooleanOp.OR);
		east = Shapes.join(east, Block.box(14.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D), BooleanOp.OR);
		east = Shapes.join(east, Block.box(2.0D, 4.0D, 2.0D, 7.5D, 16.0D, 7.5D), BooleanOp.OR);
		east = Shapes.join(east, Block.box(2.0D, 4.0D, 8.5D, 7.5D, 16.0D, 14.0D), BooleanOp.OR);
		east = Shapes.join(east, Block.box(8.5D, 4.0D, 2.0D, 14.0D, 14.0D, 7.5D), BooleanOp.OR);
		east = Shapes.join(east, Block.box(8.5D, 4.0D, 8.5D, 14.0D, 14.0D, 14.0D), BooleanOp.OR);
		SHAPES.put(Direction.EAST, east);

		VoxelShape west = Shapes.join(base, Block.box(0.0D, 5.0D, 5.0D, 2.0D, 11.0D, 11.0D), BooleanOp.OR);
		west = Shapes.join(west, Block.box(14.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D), BooleanOp.OR);
		west = Shapes.join(west, Block.box(2.0D, 4.0D, 2.0D, 7.5D, 14.0D, 7.5D), BooleanOp.OR);
		west = Shapes.join(west, Block.box(2.0D, 4.0D, 8.5D, 7.5D, 14.0D, 14.0D), BooleanOp.OR);
		west = Shapes.join(west, Block.box(8.5D, 4.0D, 2.0D, 14.0D, 16.0D, 7.5D), BooleanOp.OR);
		west = Shapes.join(west, Block.box(8.5D, 4.0D, 8.5D, 14.0D, 16.0D, 14.0D), BooleanOp.OR);
		SHAPES.put(Direction.WEST, west);
	}

	public BlockTransformer(ResourceLocation tier) {
		super(tier);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		Direction facing = state.getValue(StaticPowerBlockEntityBlock.HORIZONTAL_FACING);
		return SHAPES.get(facing);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (this.tier == StaticPowerTiers.LUMUM || this.tier == StaticPowerTiers.CREATIVE) {
			return HasGuiType.ALWAYS;
		}
		return HasGuiType.NEVER;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		super.getTooltip(stack, worldIn, tooltip, isShowingAdvanced);

		if (tier == StaticPowerTiers.LUMUM) {
			TooltipUtilities.addSingleLineBullet(tooltip, "gui.staticpower.transfomer_ratio", ChatFormatting.GREEN, Component.translatable("gui.staticpower.variable"));
		} else {
			int transformerRatio = StaticPowerConfig.getTier(tier).powerConfiguration.transfomerRatio.get();
			PowerTooltips.addTransformerRatioTooltip(tooltip, transformerRatio);
		}

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {

	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityTransformer.BASIC_TRANSFORMER.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityTransformer.ADVANCED_TRANSFORMER.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityTransformer.STATIC_TRANSFORMER.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityTransformer.ENERGIZED_TRANSFORMER.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityTransformer.LUMUM_TRANSFORMER.create(pos, state);
		}
		return null;
	}
}
