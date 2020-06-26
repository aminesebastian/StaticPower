package theking530.api.gui.widgets.tabs.redstonecontrol;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.items.cableattachments.AbstractCableAttachment;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.utilities.RedstoneMode;

public class GuiCableAttachmentRedstoneTab extends AbstractGuiRedstoneTab {
	private final ItemStack attachment;
	private final AbstractCableProviderComponent owningCableComponent;
	private final Direction attachmentSide;

	public GuiCableAttachmentRedstoneTab(ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent owningComponent) {
		super(((AbstractCableAttachment) attachment.getItem()).getRedstoneMode(attachment));
		this.attachment = attachment;
		this.owningCableComponent = owningComponent;
		this.attachmentSide = attachmentSide;
	}

	@Override
	protected void synchronizeRedstoneMode(RedstoneMode mode) {
		// Set the client's redstone mode.
		AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
		attachmentItem.setRedstoneMode(attachment, mode, owningCableComponent);

		// Create the packet.
		NetworkMessage msg = new PacketCableAttachmentRedstoneSync(mode, owningCableComponent.getPos(), attachmentSide);

		// Send a packet to the server with the updated values.
		StaticPowerMessageHandler.MAIN_PACKET_CHANNEL.sendToServer(msg);
	}

	@Override
	protected RedstoneMode getCurrentMode() {
		AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
		return attachmentItem.getRedstoneMode(attachment);
	}
}
