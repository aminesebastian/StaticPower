package theking530.staticpower.blockentities.nonpowered.cauldron;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.CapabilityItemHandler;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;

public class BlockCauldron extends StaticPowerBlockEntityBlock {
	private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	protected static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D),
			box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);
	private final boolean isClean;

	public BlockCauldron(boolean isClean) {
		this.isClean = isClean;
	}

	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return INSIDE;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.solid();
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// Check if the player is holding anything.
		if (!player.getItemInHand(hand).isEmpty()) {
			// Get the held item and a pointer to the cauldron.
			ItemStack heldItem = player.getItemInHand(hand);
			BlockEntityCauldron cauldron = (BlockEntityCauldron) world.getBlockEntity(pos);
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
					player.setItemInHand(hand, actionResult.getResult());
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.onStaticPowerBlockActivated(state, world, pos, player, hand, hit);

	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (isClean) {
			return BlockEntityCauldron.CLEAN.create(pos, state);
		} else {
			return BlockEntityCauldron.RUSTY.create(pos, state);
		}
	}

	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		if (!isShowingAdvanced) {
			if (isClean) {
				tooltip.add(new TranslatableComponent("gui.staticpower.clean_cauldron_tooltip").withStyle(ChatFormatting.AQUA));
			} else {
				tooltip.add(new TranslatableComponent("gui.staticpower.rusty_cauldron_tooltip").withStyle(ChatFormatting.GOLD));
			}
		}
	}

	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		if (isClean) {
			tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.clean_cauldron_description")).withStyle(ChatFormatting.YELLOW));
		} else {
			tooltip.add(new TextComponent("� ").append(new TranslatableComponent("gui.staticpower.rusty_cauldron_description")).withStyle(ChatFormatting.BLUE));
		}
	}
}
