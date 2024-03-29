package theking530.staticpower.blockentities.nonpowered.conveyors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.api.wrench.RegularWrenchMode;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.data.StaticCoreTier;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticpower.blocks.tileentity.StaticPowerRotateableBlockEntityBlock;

public abstract class AbstractConveyorBlock extends StaticPowerRotateableBlockEntityBlock implements IConveyorBlock {
	protected final Map<Direction, VoxelShape> ENTITY_SHAPES = new HashMap<>();
	protected final Map<Direction, VoxelShape> INTERACTION_SHAPES = new HashMap<>();

	public AbstractConveyorBlock(ResourceLocation tier) {
		super(tier);
		cacheVoxelShapes();
	}

	public AbstractConveyorBlock(ResourceLocation tier, Properties properies) {
		super(tier, properies);
		cacheVoxelShapes();
	}

	@Override
	protected void setFacingBlockStateOnPlacement(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos, state.setValue(HORIZONTAL_FACING, placer.getDirection()), 2);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		cacheVoxelShapes();
		// Get the appropriate shape.
		if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof Player) {
			return INTERACTION_SHAPES.get(state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING));
		} else {
			return ENTITY_SHAPES.get(state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING));
		}
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return ENTITY_SHAPES.get(state.getValue(StaticPowerRotateableBlockEntityBlock.HORIZONTAL_FACING));
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			StaticCoreTier tierObject = StaticCoreConfig.getTier(getTier());
			tooltip.add(Component.literal("- ").append(Component.translatable("gui.staticpower.conveyor_speed_multiplier"))
					.append(GuiTextUtilities.formatNumberAsPercentStringOneDecimal(tierObject.conveyorSpeedMultiplier.get()).withStyle(ChatFormatting.GREEN)));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		// We only rotate here, no need to check the mode of the wrench.
		if (facing != Direction.UP && facing != Direction.DOWN) {
			if (facing != world.getBlockState(pos).getValue(HORIZONTAL_FACING)) {
				world.setBlock(pos, world.getBlockState(pos).setValue(HORIZONTAL_FACING, facing), 1 | 2);
			} else {
				world.setBlock(pos, world.getBlockState(pos).setValue(HORIZONTAL_FACING, facing.getOpposite()), 1 | 2);
			}
		}
		return InteractionResult.SUCCESS;
	}

	public static VoxelShape generateSlantedBoundingBox(Direction facingDirection, float precision, float yStartOffset, float yEndOffset, float thickness, float angle,
			boolean upwards) {
		// Create an empty bounding box initially.
		VoxelShape output = Block.box(0, 0, 0, 0, 0, 0);

		// Define helper variables.
		double yStart = 0;
		double yEnd = 0;
		double forwardStart = 0;
		double forwardEnd = 0;
		double angleOffset = Math.tan(Math.toRadians(angle));

		// Calculate the amount of steps, and building the bounding box.
		int steps = (int) (16.0f / precision);
		for (int i = 0; i < steps; i++) {
			// Calculate the y positions.
			yStart = i * angleOffset * precision - yStartOffset;
			yEnd = (i + 1) * angleOffset * precision + thickness - yEndOffset;

			// Make sure we clamp to not go below 0.
			yStart = Math.max(yStart, 0);
			yEnd = Math.max(yEnd, 0);

			// Calculate the x & z positions.
			if (upwards) {
				forwardStart = (steps - i - 1) * precision;
				forwardEnd = (steps - i) * precision;
			} else {
				forwardStart = (i - 1) * precision + 0.25f;
				forwardEnd = i * precision + 0.25f;
			}

			// Build the bounds.
			double minX = 0, minY = yStart, minZ = 0, maxX = 16, maxY = yEnd, maxZ = 16;
			if (facingDirection == Direction.NORTH) {
				minZ = forwardStart;
				maxZ = forwardEnd;
			} else if (facingDirection == Direction.EAST) {
				minX = 16 - forwardStart;
				maxX = 16 - forwardEnd;
			} else if (facingDirection == Direction.WEST) {
				minX = forwardStart;
				maxX = forwardEnd;
			} else if (facingDirection == Direction.SOUTH) {
				minZ = 16 - forwardStart;
				maxZ = 16 - forwardEnd;
			}

			output = Shapes.joinUnoptimized(output,
					Block.box(Math.min(minX, maxX), Math.min(minY, maxY), Math.min(minZ, maxZ), Math.max(minX, maxX), Math.max(minY, maxY), Math.max(minZ, maxZ)), BooleanOp.OR);
		}

		// Return the output
		return output;
	}

	public abstract void cacheVoxelShapes();
}
