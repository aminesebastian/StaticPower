package theking530.staticpower.container;

import java.util.function.Supplier;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketRevertToParentContainer extends NetworkMessage {
	protected int windowId;

	public PacketRevertToParentContainer() {

	}

	public PacketRevertToParentContainer(int windowId) {
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
			Container container = ctx.get().getSender().openContainer;
			if (container instanceof StaticPowerContainer && container.windowId == windowId) {
				StaticPowerContainer cont = (StaticPowerContainer) container;
				cont.revertToParent();
			}
		});
	}
}
