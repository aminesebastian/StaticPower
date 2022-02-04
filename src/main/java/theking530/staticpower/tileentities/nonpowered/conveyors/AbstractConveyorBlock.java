package theking530.staticpower.tileentities.nonpowered.conveyors;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.api.wrench.RegularWrenchMode;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;

public abstract class AbstractConveyorBlock extends StaticPowerMachineBlock implements IConveyorBlock {
	protected final Map<Direction, VoxelShape> ENTITY_SHAPES = new HashMap<>();
	protected final Map<Direction, VoxelShape> INTERACTION_SHAPES = new HashMap<>();

	protected AbstractConveyorBlock(String name) {
		super(name);
		cacheVoxelShapes();
	}

	protected AbstractConveyorBlock(String name, Properties properies) {
		super(name, properies);
		cacheVoxelShapes();
	}

	@Override
	protected void setFacingBlockStateOnPlacement(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos, state.setValue(FACING, placer.getDirection()), 2);
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		cacheVoxelShapes();
		// Get the appropriate shape.
		if (context.getEntity() instanceof Player) {
			return INTERACTION_SHAPES.get(state.getValue(StaticPowerTileEntityBlock.FACING));
		} else {
			return ENTITY_SHAPES.get(state.getValue(StaticPowerTileEntityBlock.FACING));
		}
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return ENTITY_SHAPES.get(state.getValue(StaticPowerTileEntityBlock.FACING));
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		// We only rotate here, no need to check the mode of the wrench.
		if (facing != Direction.UP && facing != Direction.DOWN) {
			if (facing != world.getBlockState(pos).getValue(FACING)) {
				world.setBlock(pos, world.getBlockState(pos).setValue(FACING, facing), 1 | 2);
			} else {
				world.setBlock(pos, world.getBlockState(pos).setValue(FACING, facing.getOpposite()), 1 | 2);
			}
		}
		return InteractionResult.SUCCESS;
	}

	public static VoxelShape generateSlantedBoundingBox(Direction facingDirection, float precision, float yStartOffset, float yEndOffset, float thickness, float angle, boolean upwards) {
		// Create an empty bounding box initially.
		VoxelShape output = Block.box(0, 0, 0, 0, 0, 0);

		// Define helper variables.
		float yStart = 0;
		float yEnd = 0;
		float forwardStart = 0;
		float forwardEnd = 0;
		float angleOffset = (float) Math.tan(Math.toRadians(angle));

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
			if (facingDirection == Direction.NORTH) {
				output = Shapes.joinUnoptimized(output, Block.box(0, yStart, forwardStart, 16, yEnd, forwardEnd), BooleanOp.OR);
			} else if (facingDirection == Direction.EAST) {
				output = Shapes.joinUnoptimized(output, Block.box(16 - forwardStart, yStart, 0, 16 - forwardEnd, yEnd, 16), BooleanOp.OR);
			} else if (facingDirection == Direction.WEST) {
				output = Shapes.joinUnoptimized(output, Block.box(forwardStart, yStart, 0, forwardEnd, yEnd, 16), BooleanOp.OR);
			} else if (facingDirection == Direction.SOUTH) {
				output = Shapes.joinUnoptimized(output, Block.box(0, yStart, 16 - forwardStart, 16, yEnd, 16 - forwardEnd), BooleanOp.OR);
			}
		}

		// Return the output
		return output;
	}

	public abstract void cacheVoxelShapes();
}
