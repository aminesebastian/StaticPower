package theking530.staticpower.cables.attachments;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.IUpgradeItem;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.blockentities.components.control.redstonecontrol.RedstoneMode;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableBoundsHoverResult;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.ServerAttachmentDataContainer;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.InventoryUtilities;

public abstract class AbstractCableAttachment extends StaticPowerItem {
	public static final String REDSTONE_MODE_TAG = "redstone_mode";
	private static final Vector3D DEFAULT_BOUNDS = new Vector3D(3.0f, 3.0f, 3.0f);

	public AbstractCableAttachment() {
		super();
	}

	@Override
	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (world.getBlockState(pos).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) world.getBlockState(pos).getBlock();
				CableBoundsHoverResult hoverResult = block.cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
				if (!hoverResult.isEmpty() && hoverResult.type == CableBoundsHoverType.HELD_ATTACHMENT && cableComponent.attachAttachment(item, hoverResult.direction)) {
					if (!world.isClientSide) {
						cableComponent.setSideDisabledState(hoverResult.direction, false);
						item.setCount(item.getCount() - 1);
					} else {
						world.playSound(player, pos, SoundEvents.CHICKEN_EGG, SoundSource.BLOCKS, 0.15F, (float) (0.5F + Math.random() * 2.0));
					}
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	public void onAddedToCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		// Allocate the redstone mode if neeed.
		attachment.getOrCreateTag().putInt(REDSTONE_MODE_TAG, RedstoneMode.High.ordinal());
	}

	public void onRemovedFromCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		IItemHandler upgradeInv = getUpgradeInventory(attachment);
		InventoryUtilities.clearInventory(upgradeInv);
		attachment.getTag().remove(REDSTONE_MODE_TAG);
	}

	public void initializeServerDataContainer(ItemStack attachment, Direction side, AbstractCableProviderComponent cable, ServerAttachmentDataContainer dataContainer) {
	}

	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {

	}

	public void setRedstoneMode(ItemStack attachment, RedstoneMode mode, AbstractCableProviderComponent cable) {
		attachment.getTag().putInt(REDSTONE_MODE_TAG, mode.ordinal());
		cable.getTileEntity().setChanged();
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
		if (attachment.getTag().contains(REDSTONE_MODE_TAG)) {
			return RedstoneMode.values()[attachment.getTag().getInt(REDSTONE_MODE_TAG)];
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

	public abstract ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent);

	public boolean shouldAppearOnPickBlock(ItemStack attachment) {
		return true;
	}

	public boolean shouldDropOnOwningCableBreak(ItemStack attachment) {
		return true;
	}

	protected IItemHandler getUpgradeInventory(ItemStack attachment) {
		return attachment.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).orElse(null);
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
