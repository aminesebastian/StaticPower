package theking530.staticpower.tileentities.nonpowered.conveyors.straight;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.client.rendering.blocks.ConveyorStraightModel;
import theking530.staticpower.utilities.RaytracingUtilities;

public class BlockStraightConveyor extends StaticPowerMachineBlock {
	/**
	 * This property indicates if the conveyor is on the top or bottom of a block
	 * space.
	 */
	public static final BooleanProperty IS_TOP = BooleanProperty.create("is_top");
	private static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 4, 16);
	private static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(0, 12, 0, 16, 16, 16);

	public BlockStraightConveyor(String name) {
		super(name);
		setDefaultState(stateContainer.getBaseState().with(IS_TOP, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(IS_TOP);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		// Perform a raytrace.
		BlockRayTraceResult rtResult = RaytracingUtilities.findPlayerRayTrace(world, placer, FluidMode.NONE);

		// Determine the local Y offset.
		float relativeY = (float) (rtResult.getHitVec().getY() - pos.getY());

		// If we hit the top half, then we are on the top.
		boolean isTop = relativeY > 0.5f;

		// Add the top property.
		BlockState modifiedState = state.with(IS_TOP, isTop);

		// Pass the modified state to the super.
		super.onBlockPlacedBy(world, pos, modifiedState, placer, stack);
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(IS_TOP)) {
			return TOP_SHAPE;
		}
		return BOTTOM_SHAPE;
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		if (state.get(IS_TOP)) {
			return TOP_SHAPE;
		}
		return BOTTOM_SHAPE;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new ConveyorStraightModel(existingModel);
	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityStraightConveyor.TYPE.create();
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
