package theking530.staticcore.container;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class PacketRevertToParentContainer extends NetworkMessage {
	protected int windowId;

	public PacketRevertToParentContainer() {

	}

	public PacketRevertToParentContainer(int windowId) {
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
			AbstractContainerMenu container = ctx.get().getSender().containerMenu;
			if (container instanceof StaticCoreContainerMenu && container.containerId == windowId) {
				StaticCoreContainerMenu cont = (StaticCoreContainerMenu) container;
				cont.revertToParent();
			}
		});
	}
}
