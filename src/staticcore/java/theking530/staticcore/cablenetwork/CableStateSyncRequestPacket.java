package theking530.staticcore.cablenetwork;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.manager.CableNetworkManager;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.StaticPower;

public class CableStateSyncRequestPacket extends NetworkMessage {
	protected BlockPos cablePos;

	public CableStateSyncRequestPacket() {

	}

	public CableStateSyncRequestPacket(BlockPos cablePos) {
		this.cablePos = cablePos;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(cablePos);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		cablePos = buffer.readBlockPos();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			CableNetworkManager manager = CableNetworkAccessor.get(ctx.get().getSender().getLevel());
			if (manager.isTrackingCable(cablePos)) {
				manager.getCable(cablePos).synchronizeServerState();
			} else {
				StaticPower.LOGGER.warn(String.format("The server requested a cable sync request for a cable that does not exist (cable position: %1$s).", cablePos.toString()));
			}
		});
	}
}
