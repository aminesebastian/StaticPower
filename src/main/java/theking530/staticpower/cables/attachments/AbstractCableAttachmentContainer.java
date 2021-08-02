package theking530.staticpower.cables.attachments;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.container.StaticPowerContainer;

public class AbstractCableAttachmentContainer<T extends Item> extends StaticPowerContainer {
	private final Direction attachmentSide;
	private final AbstractCableProviderComponent cableComponent;
	private final ItemStack attachment;

	protected AbstractCableAttachmentContainer(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int id, PlayerInventory inv, ItemStack attachment,
			Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(allocator, id, inv);
		this.attachment = attachment;
		this.attachmentSide = attachmentSide;
		this.cableComponent = cableComponent;
		initializeContainer(); // This has to be called here and not in the super.
	}

	@Override
	public void initializeContainer() {
		this.addPlayerInventory(getPlayerInventory(), 8, 69);
		this.addPlayerHotbar(getPlayerInventory(), 8, 127);
	}

	@Override
	public boolean canDragIntoSlot(Slot slot) {
		return true;
	}

	public Direction getAttachmentSide() {
		return attachmentSide;
	}

	public AbstractCableProviderComponent getCableComponent() {
		return cableComponent;
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
//		boolean alreadyExists = false;
//		int firstEmptySlot = 0;
//
//		if (!alreadyExists && !mergeItemStack(stack, firstEmptySlot, firstEmptySlot + 1, false)) {
//			return true;
//		}
		return false;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return true;
	}

	@Override
	public PacketBuffer getRevertDataPacket() {
		PacketBuffer extraData = new PacketBuffer(Unpooled.buffer());
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
