package theking530.staticpower.tileentities.nonpowered.conveyors.hopper;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import theking530.staticpower.entities.conveyorbeltentity.ConveyorBeltEntity;
import theking530.staticpower.tileentities.nonpowered.conveyors.AbstractConveyorBlock;

public class BlockConveyorHopper extends AbstractConveyorBlock {
	protected static VoxelShape PASSED_FILTER_SHAPE;
	protected static final VoxelShape NOT_PASSED_FILTER_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 8, 16);
	protected final boolean filtered;

	static {
		PASSED_FILTER_SHAPE = VoxelShapes.combine(Block.makeCuboidShape(0, 0, 0, 4, 8, 16), Block.makeCuboidShape(12, 0, 0, 16, 8, 16), IBooleanFunction.OR);
		PASSED_FILTER_SHAPE = VoxelShapes.combine(PASSED_FILTER_SHAPE, Block.makeCuboidShape(0, 0, 0, 16, 8, 4), IBooleanFunction.OR);
		PASSED_FILTER_SHAPE = VoxelShapes.combine(PASSED_FILTER_SHAPE, Block.makeCuboidShape(0, 0, 12, 16, 8, 16), IBooleanFunction.OR);
	}

	public BlockConveyorHopper(String name, boolean filtered) {
		super(name);
		this.filtered = filtered;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return filtered ? HasGuiType.ALWAYS : HasGuiType.NEVER;
	}

	@Override
	public void cacheVoxelShapes() {
		for (Direction dir : Direction.values()) {
			// Put the passed shapes in as default.
			ENTITY_SHAPES.put(dir, PASSED_FILTER_SHAPE);
			INTERACTION_SHAPES.put(dir, PASSED_FILTER_SHAPE);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		// If not filtered, just return the super call.
		return super.getShape(state, worldIn, pos, context);
	}

	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		// If we're a filtering hopper and the entity is an item.
		if (filtered && context.getEntity() instanceof ConveyorBeltEntity) {
			// If the context is null (then an item entity), and the hopper tile entity is
			// still there.
			if (worldIn.getTileEntity(pos) != null) {
				// Get the hopper and the item entity.
				TileEntityConveyorHopper conveyor = (TileEntityConveyorHopper) worldIn.getTileEntity(pos);

				// Get the item over the hole.
				ConveyorBeltEntity entity = (ConveyorBeltEntity) context.getEntity();

				// For the item, check if the item passes the filter, if true, use the regular
				// shape, if not, cover the hole.
				return conveyor.filterItem(entity) ? super.getShape(state, worldIn, pos, context) : NOT_PASSED_FILTER_SHAPE;
			}
		}

		return super.getCollisionShape(state, worldIn, pos, context);
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return filtered ? TileEntityConveyorHopper.FILTERED_TYPE.create() : TileEntityConveyorHopper.TYPE.create();
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
