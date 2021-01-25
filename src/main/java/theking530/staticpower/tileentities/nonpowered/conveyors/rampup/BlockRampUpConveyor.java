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

	static {
		{
			float precision = 4f;
			int steps = (int) (16.0f / precision);
			ENTITY_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 0, 16);
			for (int i = 0; i < steps; i++) {
				ENTITY_SHAPE = VoxelShapes.or(ENTITY_SHAPE, Block.makeCuboidShape(0, i * precision, 0, 16, (i + 1) * precision, (steps - i) * precision));
			}
		}
		{
			float precision = 0.2f;
			int steps = (int) (16.0f / precision);
			INTERACTION_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 0, 16);
			for (int i = 0; i < steps; i++) {
				INTERACTION_SHAPE = VoxelShapes.or(INTERACTION_SHAPE, Block.makeCuboidShape(0, ((i - 1) * precision), 0, 16, (i) * precision, (steps - i) * precision));
			}
		}
	}

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
			INTERACTION_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 0, 0);

			float yStart = 0;
			float yEnd = 0;
			float forwardStart = 0;
			float forwardEnd = 0;

			float precision = 0.5f;
			float yStartOffset = -0.5f;
			float yEndOffset = -0.05f;
			float thickness = 3.5f;
			float angle = 36.9f;
			float angleOffset = (float) Math.tan(Math.toRadians(angle));
			int steps = (int) (16.0f / precision);
			for (int i = 0; i < steps; i++) {
				yStart = i * angleOffset * precision - yStartOffset;
				yEnd = (i + 1) * angleOffset * precision + thickness - yEndOffset;
				forwardStart = (steps - i - 1) * precision;
				forwardEnd = (steps - i) * precision;
				if (facingDirection == Direction.NORTH) {
					INTERACTION_SHAPE = VoxelShapes.combine(INTERACTION_SHAPE, Block.makeCuboidShape(0, yStart, forwardStart, 16, yEnd, forwardEnd), IBooleanFunction.OR);
				} else if (facingDirection == Direction.EAST) {
					INTERACTION_SHAPE = VoxelShapes.combine(INTERACTION_SHAPE, Block.makeCuboidShape(forwardStart, 16 - yStart, 0, forwardEnd, 16 - yEnd, 16), IBooleanFunction.OR);
				} else if (facingDirection == Direction.WEST) {
					INTERACTION_SHAPE = VoxelShapes.combine(INTERACTION_SHAPE, Block.makeCuboidShape(forwardStart, yStart, 0, forwardEnd, yEnd, 16), IBooleanFunction.OR);
				} else if (facingDirection == Direction.SOUTH) {
					INTERACTION_SHAPE = VoxelShapes.combine(INTERACTION_SHAPE, Block.makeCuboidShape(0, 16 - yStart, forwardStart, 16, 16 - yEnd, forwardEnd), IBooleanFunction.OR);
				}
			}
			return INTERACTION_SHAPE;
		}
		return ENTITY_SHAPE;
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return ENTITY_SHAPE;
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
