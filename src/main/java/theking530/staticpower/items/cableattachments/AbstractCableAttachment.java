package theking530.staticpower.items.cableattachments;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.tileentities.utilities.RedstoneMode;

public abstract class AbstractCableAttachment extends StaticPowerItem {

	public AbstractCableAttachment(String name) {
		super(name);
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (world.getBlockState(pos).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) world.getBlockState(pos).getBlock();
				Direction hoveredDirection = block.CableBounds.getHoveredAttachmentDirection(pos, player);
				if (hoveredDirection != null && cableComponent.attachAttachment(item, hoveredDirection)) {
					if (!world.isRemote) {
						item.setCount(item.getCount() - 1);
					} else {
						world.playSound(player, pos, SoundEvents.ENTITY_CHICKEN_EGG, SoundCategory.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
					}
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		if (!attachment.hasTag()) {
			attachment.setTag(new CompoundNBT());
		}
		attachment.getTag().putInt("redstone_mode", RedstoneMode.High.ordinal());
	}

	public void onRemovedFromCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		attachment.setTag(null);
	}

	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {

	}

	public void setRedstoneMode(ItemStack attachment, RedstoneMode mode, AbstractCableProviderComponent cable) {
		attachment.getTag().putInt("redstone_mode", mode.ordinal());
		cable.getTileEntity().markDirty();
	}

	public RedstoneMode getRedstoneMode(ItemStack attachment) {
		if (attachment.getTag().contains("redstone_mode")) {
			return RedstoneMode.values()[attachment.getTag().getInt("redstone_mode")];
		}
		return RedstoneMode.Ignore;
	}

	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment) {
		return null;
	}

	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	public abstract ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent);

	protected abstract class AbstractCableAttachmentContainerProvider implements INamedContainerProvider {
		public final ItemStack targetItemStack;

		public AbstractCableAttachmentContainerProvider(ItemStack stack) {
			targetItemStack = stack;
		}

		@Override
		public ITextComponent getDisplayName() {
			return targetItemStack.getDisplayName();
		}
	}
}
