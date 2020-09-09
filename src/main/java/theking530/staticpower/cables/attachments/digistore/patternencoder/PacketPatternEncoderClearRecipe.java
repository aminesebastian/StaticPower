package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.function.Supplier;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketPatternEncoderClearRecipe extends NetworkMessage {
	protected int windowId;

	public PacketPatternEncoderClearRecipe() {

	}

	public PacketPatternEncoderClearRecipe(int windowId) {
		this.windowId = windowId;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container openContainer = ctx.get().getSender().openContainer;
			if (openContainer != null && openContainer instanceof ContainerDigistorePatternEncoder && openContainer.windowId == windowId) {
				ContainerDigistorePatternEncoder encoderContainer = (ContainerDigistorePatternEncoder) openContainer;
				encoderContainer.clearRecipe();
			}
		});
	}
}
