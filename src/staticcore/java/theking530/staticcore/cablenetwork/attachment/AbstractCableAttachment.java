package theking530.staticcore.cablenetwork.attachment;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import theking530.api.IUpgradeItem;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.blockentity.components.control.redstonecontrol.RedstoneMode;
import theking530.staticcore.cablenetwork.AbstractCableBlock;
import theking530.staticcore.cablenetwork.CableBoundsHoverResult;
import theking530.staticcore.cablenetwork.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.item.StaticCoreItem;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.utilities.math.Vector3D;

public abstract class AbstractCableAttachment extends StaticCoreItem {
	public static final String ATTACHMENT_TAG = "attachment_tag";
	public static final String REDSTONE_MODE_TAG = "redstone_mode";
	private static final Vector3D DEFAULT_BOUNDS = new Vector3D(3.0f, 3.0f, 3.0f);

	public AbstractCableAttachment(Item.Properties properties) {
		super(properties);
	}

	public AbstractCableAttachment(CreativeModeTab tab) {
		super(new Item.Properties().tab(tab));
	}

	@Override
	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!world.isClientSide() && world.getBlockState(pos).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) world.getBlockState(pos).getBlock();
				CableBoundsHoverResult hoverResult = block.cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
				if (!hoverResult.isEmpty() && hoverResult.type == CableBoundsHoverType.HELD_ATTACHMENT && cableComponent.attachAttachment(item, hoverResult.direction)) {
					cableComponent.setSideDisabledState(hoverResult.direction, false);
					item.setCount(item.getCount() - 1);
					world.playSound(null, pos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
					return InteractionResult.SUCCESS;
				}
			}
		}
		// Always return success here so we consume the right click. If we don't, when
		// applying the attachment we will get shot into the UI on placement.
		return InteractionResult.SUCCESS;
	}

	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Allocate the redstone mode if neeed.
		attachment.getOrCreateTag().put(ATTACHMENT_TAG, new CompoundTag());
		getAttachmentTag(attachment).putInt(REDSTONE_MODE_TAG, RedstoneMode.High.ordinal());
	}

	public void onRemovedFromCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		IItemHandler upgradeInv = getUpgradeInventory(attachment);
		InventoryUtilities.clearInventory(upgradeInv);
		attachment.getTag().remove(ATTACHMENT_TAG);
	}

	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {

	}

	protected static CompoundTag getAttachmentTag(ItemStack attachment) {
		if (attachment.hasTag()) {
			return attachment.getTag().getCompound(ATTACHMENT_TAG);
		}
		throw new RuntimeException("Attempted to access attachment tag when attacment is not attached to a cable!");
	}

	public void setRedstoneMode(ItemStack attachment, RedstoneMode mode, AbstractCableProviderComponent cable) {
		getAttachmentTag(attachment).putInt(REDSTONE_MODE_TAG, mode.ordinal());
		cable.getBlockEntity().setChanged();
		if (cable.getCable().isPresent()) {
			cable.getCable().get().synchronizeServerState();
		}
	}

	public void getAdditionalDrops(ItemStack attachment, AbstractCableProviderComponent cable, List<ItemStack> drops) {
		IItemHandler upgradeInv = getUpgradeInventory(attachment);
		if (upgradeInv != null) {
			for (int i = 0; i < upgradeInv.getSlots(); i++) {
				ItemStack upgrade = upgradeInv.getStackInSlot(i);
				if (!upgrade.isEmpty()) {
					drops.add(upgrade);
				}
			}
		}
	}

	public RedstoneMode getRedstoneMode(ItemStack attachment) {
		if (getAttachmentTag(attachment).contains(REDSTONE_MODE_TAG)) {
			return RedstoneMode.values()[getAttachmentTag(attachment).getInt(REDSTONE_MODE_TAG)];
		}
		return RedstoneMode.High;
	}

	public @Nullable AbstractCableAttachmentContainerProvider getUIContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return null;
	}

	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	public Vector3D getBounds() {
		return DEFAULT_BOUNDS;
	}

	public abstract ResourceLocation getModel(ItemStack attachment, BlockAndTintGetter level, BlockPos pos);

	public boolean shouldAppearOnPickBlock(ItemStack attachment) {
		return true;
	}

	public boolean shouldDropOnOwningCableBreak(ItemStack attachment) {
		return true;
	}

	protected IItemHandler getUpgradeInventory(ItemStack attachment) {
		return attachment.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).orElse(null);
	}

	/**
	 * Determines if this upgrade inventory has an upgrade of the provided class.
	 * 
	 * @param upgradeClass The class to check for.
	 * @return True if an upgrade of the provided type was found, false otherwise.
	 */
	public <T extends IUpgradeItem> boolean hasUpgradeOfClass(ItemStack attachment, Class<T> upgradeClass) {
		IItemHandler upgradeInv = getUpgradeInventory(attachment);
		for (int i = 0; i < upgradeInv.getSlots(); i++) {
			ItemStack stack = upgradeInv.getStackInSlot(i);
			if (upgradeClass.isInstance(stack.getItem())) {
				return true;
			}
		}
		return false;
	}

	public <T extends IUpgradeItem> int getUpgradeCount(ItemStack attachment, Class<T> upgradeClass) {
		// Allocate the count.
		int count = 0;

		// Get the upgrade inventory.
		IItemHandler upgradeInv = getUpgradeInventory(attachment);

		// Check for the ugprade. If we find one, increment the count by the stack size.
		// If the stack size surpasses the max stack size, return the max stack size.
		for (int i = 0; i < upgradeInv.getSlots(); i++) {
			ItemStack stack = upgradeInv.getStackInSlot(i);
			if (upgradeClass.isInstance(stack.getItem())) {
				count += stack.getCount();
				if (count > stack.getMaxStackSize()) {
					return stack.getMaxStackSize();
				}
			}
		}
		return count;
	}

	public abstract class AbstractCableAttachmentContainerProvider implements MenuProvider {
		public final ItemStack targetItemStack;
		public final Direction attachmentSide;
		public final AbstractCableProviderComponent cable;

		public AbstractCableAttachmentContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			targetItemStack = stack;
			this.cable = cable;
			this.attachmentSide = attachmentSide;
		}

		@Override
		public Component getDisplayName() {
			return targetItemStack.getHoverName();
		}
	}
}
