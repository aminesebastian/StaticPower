package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketPatternEncoderClearRecipe extends NetworkMessage {
	protected int windowId;

	public PacketPatternEncoderClearRecipe() {

	}

	public PacketPatternEncoderClearRecipe(int windowId) {
		this.windowId = windowId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			AbstractContainerMenu openContainer = ctx.get().getSender().containerMenu;
			if (openContainer != null && openContainer instanceof ContainerDigistorePatternEncoder && openContainer.containerId == windowId) {
				ContainerDigistorePatternEncoder encoderContainer = (ContainerDigistorePatternEncoder) openContainer;
				encoderContainer.clearRecipe();
			}
		});
	}
}
