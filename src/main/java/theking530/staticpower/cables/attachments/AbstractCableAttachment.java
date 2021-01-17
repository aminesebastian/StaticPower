package theking530.staticpower.cables.attachments;

import java.util.List;

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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.api.IUpgradeItem;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableBoundsHoverResult;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;
import theking530.staticpower.utilities.InventoryUtilities;

public abstract class AbstractCableAttachment extends StaticPowerItem {
	private static final Vector3D DEFAULT_BOUNDS = new Vector3D(3.0f, 3.0f, 3.0f);

	public AbstractCableAttachment(String name) {
		super(name);
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (world.getBlockState(pos).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) world.getBlockState(pos).getBlock();
				CableBoundsHoverResult hoverResult = block.cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
				if (!hoverResult.isEmpty() && hoverResult.type == CableBoundsHoverType.HELD_ATTACHMENT && cableComponent.attachAttachment(item, hoverResult.direction)) {
					if (!world.isRemote) {
						cableComponent.setSideDisabledState(hoverResult.direction, false);
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

		// Allocate the redstone mode if neeed.
		attachment.getTag().putInt("redstone_mode", RedstoneMode.High.ordinal());

		// Allocate the covers.
		for (int i = 0; i < 6; i++) {
			attachment.getTag().putString("cover_" + i, "");
		}

	}

	public void onRemovedFromCable(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {
		IItemHandler upgradeInv = getUpgradeInventory(attachment);
		InventoryUtilities.clearInventory(upgradeInv);
	}

	public void attachmentTick(ItemStack attachment, Direction side, AbstractCableProviderComponent cable) {

	}

	public void setRedstoneMode(ItemStack attachment, RedstoneMode mode, AbstractCableProviderComponent cable) {
		attachment.getTag().putInt("redstone_mode", mode.ordinal());
		cable.getTileEntity().markDirty();
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
		if (attachment.getTag().contains("redstone_mode")) {
			return RedstoneMode.values()[attachment.getTag().getInt("redstone_mode")];
		}
		return RedstoneMode.High;
	}

	public @Nullable AbstractCableAttachmentContainerProvider getContainerProvider(ItemStack attachment, AbstractCableProviderComponent cable, Direction attachmentSide) {
		return null;
	}

	public boolean hasGui(ItemStack attachment) {
		return false;
	}

	public Vector3D getBounds() {
		return DEFAULT_BOUNDS;
	}

	public abstract ResourceLocation getModel(ItemStack attachment, AbstractCableProviderComponent cableComponent);

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

	protected abstract class AbstractCableAttachmentContainerProvider implements INamedContainerProvider {
		public final ItemStack targetItemStack;
		public final Direction attachmentSide;
		public final AbstractCableProviderComponent cable;

		public AbstractCableAttachmentContainerProvider(ItemStack stack, AbstractCableProviderComponent cable, Direction attachmentSide) {
			targetItemStack = stack;
			this.cable = cable;
			this.attachmentSide = attachmentSide;
		}

		@Override
		public ITextComponent getDisplayName() {
			return targetItemStack.getDisplayName();
		}
	}
}
