package theking530.staticpower.tileentities.nonpowered.conveyors;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
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
	protected void setFacingBlockStateOnPlacement(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlockState(pos, state.with(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		cacheVoxelShapes();
		// Get the appropriate shape.
		if (context.getEntity() instanceof PlayerEntity) {
			return INTERACTION_SHAPES.get(state.get(StaticPowerTileEntityBlock.FACING));
		} else {
			return ENTITY_SHAPES.get(state.get(StaticPowerTileEntityBlock.FACING));
		}
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return ENTITY_SHAPES.get(state.get(StaticPowerTileEntityBlock.FACING));
	}

	@Override
	public ActionResultType wrenchBlock(PlayerEntity player, RegularWrenchMode mode, ItemStack wrench, World world, BlockPos pos, Direction facing, boolean returnDrops) {
		// We only rotate here, no need to check the mode of the wrench.
		if (facing != Direction.UP && facing != Direction.DOWN) {
			if (facing != world.getBlockState(pos).get(FACING)) {
				world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing), 1 | 2);
			} else {
				world.setBlockState(pos, world.getBlockState(pos).with(FACING, facing.getOpposite()), 1 | 2);
			}
		}
		return ActionResultType.SUCCESS;
	}

	public static VoxelShape generateSlantedBoundingBox(Direction facingDirection, float precision, float yStartOffset, float yEndOffset, float thickness, float angle, boolean upwards) {
		// Create an empty bounding box initially.
		VoxelShape output = Block.makeCuboidShape(0, 0, 0, 0, 0, 0);

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
				output = VoxelShapes.combine(output, Block.makeCuboidShape(0, yStart, forwardStart, 16, yEnd, forwardEnd), IBooleanFunction.OR);
			} else if (facingDirection == Direction.EAST) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(16 - forwardStart, yStart, 0, 16 - forwardEnd, yEnd, 16), IBooleanFunction.OR);
			} else if (facingDirection == Direction.WEST) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(forwardStart, yStart, 0, forwardEnd, yEnd, 16), IBooleanFunction.OR);
			} else if (facingDirection == Direction.SOUTH) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(0, yStart, 16 - forwardStart, 16, yEnd, 16 - forwardEnd), IBooleanFunction.OR);
			}
		}

		// Return the output
		return output;
	}

	public abstract void cacheVoxelShapes();
}
