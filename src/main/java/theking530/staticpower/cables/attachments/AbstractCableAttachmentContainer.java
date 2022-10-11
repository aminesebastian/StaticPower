package theking530.staticpower.cables.attachments;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.cablenetwork.CableUtilities;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.container.StaticPowerContainer;

public class AbstractCableAttachmentContainer<T extends Item> extends StaticPowerContainer {
	private final Direction attachmentSide;
	private final AbstractCableProviderComponent cableComponent;
	private final ItemStack attachment;

	protected AbstractCableAttachmentContainer(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int id, Inventory inv, ItemStack attachment,
			Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(allocator, id, inv);
		this.attachment = attachment;
		this.attachmentSide = attachmentSide;
		this.cableComponent = cableComponent;
		initializeContainer(); // This has to be called here and not in the super.
	}

	@Override
	public void initializeContainer() {
		addAllPlayerSlots();
	}

	@Override
	public boolean canDragTo(Slot slot) {
		return true;
	}

	public Direction getAttachmentSide() {
		return attachmentSide;
	}

	public AbstractCableProviderComponent getCableComponent() {
		return cableComponent;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
//		boolean alreadyExists = false;
//		int firstEmptySlot = 0;
//
//		if (!alreadyExists && !mergeItemStack(stack, firstEmptySlot, firstEmptySlot + 1, false)) {
//			return true;
//		}
		return false;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public FriendlyByteBuf getRevertDataPacket() {
		FriendlyByteBuf extraData = new FriendlyByteBuf(Unpooled.buffer());
		extraData.writeInt(attachmentSide.ordinal());
		extraData.writeBlockPos(cableComponent.getPos());
		extraData.readerIndex(0);
		return extraData;
	}

	/**
	 * Gets the {@link ItemStack} that triggered the opening of this container.
	 * 
	 * @return The {@link ItemStack} that opened this container.
	 */
	public ItemStack getAttachment() {
		return attachment;
	}

	protected static ItemStack getAttachmentItemStack(Inventory inv, FriendlyByteBuf data) {
		data.resetReaderIndex();
		Direction attachmentSide = Direction.values()[data.readInt()];
		BlockPos cablePosition = data.readBlockPos();
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(inv.player.level, cablePosition);
		return cableComponent.getAttachment(attachmentSide);
	}

	protected static Direction getAttachmentSide(FriendlyByteBuf data) {
		data.resetReaderIndex();
		return Direction.values()[data.readInt()];
	}

	protected static AbstractCableProviderComponent getCableComponent(Inventory inv, FriendlyByteBuf data) {
		data.resetReaderIndex();
		// Skip the direction.
		data.readInt();
		BlockPos cablePosition = data.readBlockPos();
		return CableUtilities.getCableWrapperComponent(inv.player.level, cablePosition);
	}
}
