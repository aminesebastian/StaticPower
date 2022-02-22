package theking530.staticcore.gui.widgets.tabs.redstonecontrol;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.tileentities.components.control.redstonecontrol.RedstoneMode;

public class PacketCableAttachmentRedstoneSync extends NetworkMessage {
	private RedstoneMode redstoneMode;
	private BlockPos position;
	private Direction attachmentSide;

	public PacketCableAttachmentRedstoneSync() {
	}

	public PacketCableAttachmentRedstoneSync(RedstoneMode redstoneMode, BlockPos position, Direction attachmentSide) {
		this.redstoneMode = redstoneMode;
		this.position = position;
		this.attachmentSide = attachmentSide;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		redstoneMode = RedstoneMode.values()[buf.readInt()];
		position = buf.readBlockPos();
		attachmentSide = Direction.values()[buf.readInt()];
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(redstoneMode.ordinal());
		buf.writeBlockPos(position);
		buf.writeInt(attachmentSide.ordinal());
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(context.get().getSender().level, position);
	
			if (cableComponent != null) {
				if (!cableComponent.getAttachment(attachmentSide).isEmpty()) {
					AbstractCableAttachment attachmentItem = (AbstractCableAttachment) cableComponent.getAttachment(attachmentSide).getItem();
					attachmentItem.setRedstoneMode(cableComponent.getAttachment(attachmentSide), redstoneMode, cableComponent);
				}
			}
		});
	}
}
