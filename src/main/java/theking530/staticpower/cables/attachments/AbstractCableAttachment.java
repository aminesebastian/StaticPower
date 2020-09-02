package theking530.staticpower.cables.attachments;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticcore.utilities.Vector3D;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableBoundsHoverResult;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

public abstract class AbstractCableAttachment extends StaticPowerItem {
	private static final Vector3D DEFAULT_BOUNDS = new Vector3D(3.0f, 3.0f, 3.0f);
	public static final String UPGRADE_INVENTORY_TAG = "cable_attachment_upgrade_inventory";
	public final int upgradeSlotCount;

	public AbstractCableAttachment(String name) {
		this(name, 0);
	}

	public AbstractCableAttachment(String name, int upgradeSlotCount) {
		super(name);
		this.upgradeSlotCount = upgradeSlotCount;
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos, Direction face, PlayerEntity player, ItemStack item) {
		if (world.getBlockState(pos).getBlock() instanceof AbstractCableBlock) {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
			if (cableComponent != null) {
				AbstractCableBlock block = (AbstractCableBlock) world.getBlockState(pos).getBlock();
				CableBoundsHoverResult hoverResult = block.CableBounds.getHoveredAttachmentOrCover(pos, player);
				if (!hoverResult.isEmpty() && hoverResult.type == CableBoundsHoverType.HELD_ATTACHMENT && cableComponent.attachAttachment(item, hoverResult.direction)) {
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

		// Allocate the redstone mode if neeed.
		attachment.getTag().putInt("redstone_mode", RedstoneMode.High.ordinal());

		// Allocate the upgrade inventory.
		ListNBT upgradeItems = new ListNBT();
		for (int i = 0; i < upgradeSlotCount; i++) {
			CompoundNBT itemNBT = new CompoundNBT();
			ItemStack.EMPTY.write(itemNBT);
			upgradeItems.add(itemNBT);
		}
		attachment.getTag().put(UPGRADE_INVENTORY_TAG, upgradeItems);

		// Allocate the covers.
		for (int i = 0; i < 6; i++) {
			attachment.getTag().putString("cover_" + i, "");
		}

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

	protected AttachmentUpgradeInventory getUpgradeInventory(ItemStack attachment) {
		return new AttachmentUpgradeInventory(attachment);
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
