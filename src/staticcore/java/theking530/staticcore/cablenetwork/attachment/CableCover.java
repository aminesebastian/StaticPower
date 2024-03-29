package theking530.staticcore.cablenetwork.attachment;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.client.CoverBuilder;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.client.models.CoverItemModelProvider;

public class CableCover extends Item implements ICustomModelProvider {
	public static final String COVER_BLOCK_STATE_TAG = "target";
	public static final MutableComponent COVER_TRANSLATION_COMPONENT = Component.translatable("item.staticpower.cover");

	public CableCover() {
		super(new Item.Properties());
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!context.getLevel().isClientSide()) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(context.getLevel(), context.getClickedPos());
			if (cableComponent != null && context.getLevel().getBlockState(context.getClickedPos()).getBlock() instanceof AbstractCableBlock) {
				AbstractCableBlock block = (AbstractCableBlock) context.getLevel().getBlockState(context.getClickedPos()).getBlock();
				Direction hoveredDirection = block.cableBoundsCache.getHoveredAttachmentOrCover(context.getClickedPos(), context.getPlayer()).direction;
				if (hoveredDirection != null && cableComponent.attachCover(context.getItemInHand(), hoveredDirection)) {
					context.getItemInHand().setCount(context.getItemInHand().getCount() - 1);
					context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
				}
			}
		}

		// Always return success so we consume the use. Safer this way.
		return InteractionResult.SUCCESS;
	}

	public ItemStack makeCoverForBlock(BlockState blockState) {
		ItemStack output = new ItemStack(this);
		output.setTag(new CompoundTag());
		CompoundTag blockStateTag = NbtUtils.writeBlockState(blockState);
		output.getTag().put(COVER_BLOCK_STATE_TAG, blockStateTag);
		output.setHoverName(Component.translatable(blockState.getBlock().getDescriptionId()).append(" ").append(COVER_TRANSLATION_COMPONENT));
		return output;
	}

	public static boolean isValidForCover(Block block) {
		BlockState defaultState = block.defaultBlockState();

		// block.hasTileEntity(defaultState) will skip tile entites as needed. We only
		// non normal cube blocks that are made of glass.
		if (defaultState.getRenderShape() != RenderShape.MODEL || !defaultState.isRedstoneConductor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO)) {
			return block instanceof AbstractGlassBlock;
		}
		return true;
	}

	public static Item getCoverItemBlock(ItemStack coverStack) {
		BlockState state = getBlockStateForCover(coverStack);
		if (state == null) {
			return null;
		}
		return state.getBlock().asItem();
	}

	public static BlockState getBlockStateForCover(ItemStack coverStack) {
		if (coverStack.getItem() instanceof CableCover && coverStack.hasTag()) {
			return NbtUtils.readBlockState(coverStack.getTag().getCompound(COVER_BLOCK_STATE_TAG));
		}
		return null;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new CoverItemModelProvider(existingModel, new CoverBuilder());
	}
}
