package theking530.staticpower.tileentities.nonpowered.cauldron;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;

public class BlockCauldron extends StaticPowerTileEntityBlock {
	private static final VoxelShape INSIDE = makeCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.or(makeCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D),
			makeCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), IBooleanFunction.ONLY_FIRST);
	private final boolean isClean;

	public BlockCauldron(String name, boolean isClean) {
		super(name);
		this.isClean = isClean;
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return INSIDE;
	}

	@Override
	public HasGuiType hasGuiScreen(TileEntity tileEntity, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getSolid();
	}

	@Override
	public ActionResultType onStaticPowerBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		// Check if the player is holding anything.
		if (!player.getHeldItem(hand).isEmpty()) {
			// Get the held item and a pointer to the cauldron.
			ItemStack heldItem = player.getHeldItem(hand);
			TileEntityCauldron cauldron = (TileEntityCauldron) world.getTileEntity(pos);
			FluidActionResult actionResult = null;
			boolean shouldManuallyReplace = heldItem.getCount() == 1;

			// If the cauldron already has contents, attempt to fill the held item,
			// otherwise, fill the cauldron.
			if (cauldron.internalTank.getFluidAmount() > 0) {
				actionResult = FluidUtil.tryFillContainerAndStow(heldItem, cauldron.internalTank, player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null), 1000,
						player, true);
			} else {
				actionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, cauldron.internalTank, player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null), 1000,
						player, true);
			}

			// Check what happened.
			if (actionResult.isSuccess()) {
				if (shouldManuallyReplace) {
					player.setHeldItem(hand, actionResult.getResult());
				}
				return ActionResultType.SUCCESS;
			}
		}
		return super.onStaticPowerBlockActivated(state, world, pos, player, hand, hit);

	}

	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		if (isClean) {
			return TileEntityCauldron.CLEAN.create();
		} else {
			return TileEntityCauldron.RUSTY.create();
		}
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			if (isClean) {
				tooltip.add(new TranslationTextComponent("gui.staticpower.clean_cauldron_tooltip").mergeStyle(TextFormatting.AQUA));
			} else {
				tooltip.add(new TranslationTextComponent("gui.staticpower.rusty_cauldron_tooltip").mergeStyle(TextFormatting.GOLD));
			}
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		if (isClean) {
			tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.clean_cauldron_description")).mergeStyle(TextFormatting.YELLOW));
		} else {
			tooltip.add(new StringTextComponent("• ").append(new TranslationTextComponent("gui.staticpower.rusty_cauldron_description")).mergeStyle(TextFormatting.BLUE));
		}
	}
}
