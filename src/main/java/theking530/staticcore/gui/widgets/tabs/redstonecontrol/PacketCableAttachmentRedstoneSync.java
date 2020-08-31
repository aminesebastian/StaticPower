package theking530.staticcore.gui.widgets.tabs.redstonecontrol;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.network.NetworkMessage;
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
	public void decode(PacketBuffer buf) {
		redstoneMode = RedstoneMode.values()[buf.readInt()];
		position = buf.readBlockPos();
		attachmentSide = Direction.values()[buf.readInt()];
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeInt(redstoneMode.ordinal());
		buf.writeBlockPos(position);
		buf.writeInt(attachmentSide.ordinal());
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(context.get().getSender().world, position);
	
			if (cableComponent != null) {
				if (!cableComponent.getAttachment(attachmentSide).isEmpty()) {
					AbstractCableAttachment attachmentItem = (AbstractCableAttachment) cableComponent.getAttachment(attachmentSide).getItem();
					attachmentItem.setRedstoneMode(cableComponent.getAttachment(attachmentSide), redstoneMode, cableComponent);
				}
			}
		});
	}
}
