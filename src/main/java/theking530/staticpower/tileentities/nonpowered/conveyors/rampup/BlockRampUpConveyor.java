package theking530.staticpower.tileentities.nonpowered.conveyors.rampup;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;

public class BlockRampUpConveyor extends StaticPowerMachineBlock {
	private static VoxelShape ENTITY_SHAPE;
	private static VoxelShape INTERACTION_SHAPE;


	public BlockRampUpConveyor(String name) {
		super(name, Properties.create(Material.IRON, MaterialColor.BLACK).notSolid());
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction facingDirection = state.get(StaticPowerTileEntityBlock.FACING);
		if (context.getEntity() instanceof PlayerEntity) {
			INTERACTION_SHAPE = generateSlantedBoundingBox(facingDirection, 4.0f, -3f, -0.05f, 1.0f, 36.9f, true);
			return INTERACTION_SHAPE;
		} else {
			ENTITY_SHAPE = generateSlantedBoundingBox(facingDirection, 4.0f, -0.5f, -0.05f, 3.5f, 36.9f, true);
			return ENTITY_SHAPE;
		}
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return ENTITY_SHAPE;
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

			// Calculate the x & z positions.
			if (upwards) {
				forwardStart = (steps - i - 1) * precision;
				forwardEnd = (steps - i) * precision;
			} else {
				forwardStart = (i - 1) * precision;
				forwardEnd = i * precision;
			}

			// Build the bounds.
			if (facingDirection == Direction.NORTH) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(0, yStart, forwardStart, 16, yEnd, forwardEnd), IBooleanFunction.OR);
			} else if (facingDirection == Direction.EAST) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(forwardStart, 16 - yStart, 0, forwardEnd, 16 - yEnd, 16), IBooleanFunction.OR);
			} else if (facingDirection == Direction.WEST) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(forwardStart, yStart, 0, forwardEnd, yEnd, 16), IBooleanFunction.OR);
			} else if (facingDirection == Direction.SOUTH) {
				output = VoxelShapes.combine(output, Block.makeCuboidShape(0, 16 - yStart, forwardStart, 16, 16 - yEnd, forwardEnd), IBooleanFunction.OR);
			}
		}

		// Return the output
		return output;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return false;
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityRampUpConveyor.TYPE.create();
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			tooltip.add(new TranslationTextComponent("gui.staticpower.experience_hopper_tooltip").mergeStyle(TextFormatting.GREEN));
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.experience_hopper_description")).mergeStyle(TextFormatting.BLUE));
	}
}
