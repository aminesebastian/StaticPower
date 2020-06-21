package theking530.staticpower.items.cableattachments;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticpower.client.container.StaticPowerItemContainer;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.CableUtilities;

public class AbstractCableAttachmentContainer<T extends AbstractCableAttachment> extends StaticPowerItemContainer<T> {
	private final Direction attachmentSide;
	private final AbstractCableProviderComponent cableComponent;

	protected AbstractCableAttachmentContainer(ContainerType<?> type, int id, PlayerInventory inv, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(type, id, inv, attachment);
		this.attachmentSide = attachmentSide;
		this.cableComponent = cableComponent;
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 69);
		this.addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return false;
	}

	public Direction getAttachmentSide() {
		return attachmentSide;
	}

	public AbstractCableProviderComponent getCableComponent() {
		return cableComponent;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		boolean alreadyExists = false;
		int firstEmptySlot = -1;

		if (!alreadyExists && !mergeItemStack(stack, firstEmptySlot, firstEmptySlot + 1, false)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}

	protected static ItemStack getAttachmentItemStack(PlayerInventory inv, PacketBuffer data) {
		data.resetReaderIndex();
		Direction attachmentSide = Direction.values()[data.readInt()];
		BlockPos cablePosition = data.readBlockPos();
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(inv.player.world, cablePosition);
		return cableComponent.getAttachment(attachmentSide);
	}

	protected static Direction getAttachmentSide(PacketBuffer data) {
		data.resetReaderIndex();
		return Direction.values()[data.readInt()];
	}

	protected static AbstractCableProviderComponent getCableComponent(PlayerInventory inv, PacketBuffer data) {
		data.resetReaderIndex();
		// Skip the direction.
		data.readInt();
		BlockPos cablePosition = data.readBlockPos();
		return CableUtilities.getCableWrapperComponent(inv.player.world, cablePosition);
	}
}
