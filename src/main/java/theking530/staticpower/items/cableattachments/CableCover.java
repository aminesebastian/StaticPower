package theking530.staticpower.items.cableattachments;

import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.EmptyBlockReader;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.ICustomModelSupplier;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.client.rendering.CoverBuilder;
import theking530.staticpower.client.rendering.items.CoverItemModelProvider;

public class CableCover extends Item implements ICustomModelSupplier {
	public static final String COVER_BLOCK_STATE_TAG = "target";
	public static final TranslationTextComponent COVER_TRANSLATION_COMPONENT = new TranslationTextComponent("item.staticpower.cover");

	public CableCover(String name) {
		super(new Item.Properties());
		setRegistryName(name);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (context.getWorld().getBlockState(context.getPos()).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(context.getWorld(), context.getPos());
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) context.getWorld().getBlockState(context.getPos()).getBlock();
				Direction hoveredDirection = block.CableBounds.getHoveredAttachmentOrCover(context.getPos(), context.getPlayer()).direction;
				if (hoveredDirection != null && cableComponent.attachCover(context.getItem(), hoveredDirection)) {
					if (!context.getWorld().isRemote) {
						context.getItem().setCount(context.getItem().getCount() - 1);
					} else {
						context.getWorld().playSound(context.getPlayer(), context.getPos(), SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	public ItemStack makeCoverForBlock(BlockState blockState) {
		ItemStack output = new ItemStack(this);
		output.setTag(new CompoundNBT());
		CompoundNBT blockStateTag = NBTUtil.writeBlockState(blockState);
		output.getTag().put(COVER_BLOCK_STATE_TAG, blockStateTag);
		output.setDisplayName(blockState.getBlock().getNameTextComponent().deepCopy().appendText(" ").appendSibling(COVER_TRANSLATION_COMPONENT));
		return output;
	}

	public static boolean isValidForCover(Block block) {
		BlockState defaultState = block.getDefaultState();

		// block.hasTileEntity(defaultState) will skip tile entites as needed. We only non normal cube blocks that are made of glass.
		if (defaultState.getRenderType() != BlockRenderType.MODEL || !defaultState.isNormalCube(EmptyBlockReader.INSTANCE, BlockPos.ZERO)) {
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
			return NBTUtil.readBlockState(coverStack.getTag().getCompound(COVER_BLOCK_STATE_TAG));
		}
		return null;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new CoverItemModelProvider(existingModel, new CoverBuilder());
	}
}
