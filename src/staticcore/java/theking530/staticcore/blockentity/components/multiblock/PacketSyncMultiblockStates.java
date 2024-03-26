package theking530.staticcore.blockentity.components.multiblock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.components.multiblock.manager.ClientMultiblockManager;
import theking530.staticcore.blockentity.components.multiblock.manager.MultiblockManager;
import theking530.staticcore.network.NetworkMessage;

public class PacketSyncMultiblockStates extends NetworkMessage {
	protected Collection<MultiblockState> states;

	public PacketSyncMultiblockStates() {

	}

	public PacketSyncMultiblockStates(Collection<MultiblockState> states) {
		this.states = states;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(states.size());
		for (MultiblockState state : states) {
			buffer.writeNbt(state.serialize());
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		int count = buffer.readInt();
		states = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			states.add(MultiblockState.deserialize(buffer.readNbt()));
		}
	}

	@SuppressWarnings({ "resource" })
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level level = Minecraft.getInstance().level;
			ClientMultiblockManager mbManager = (ClientMultiblockManager) MultiblockManager.get(level);
			mbManager.recieveSyncedStates(states);
		});
	}
}