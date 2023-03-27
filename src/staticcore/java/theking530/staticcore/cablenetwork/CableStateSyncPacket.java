package theking530.staticcore.cablenetwork;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class CableStateSyncPacket extends NetworkMessage {
	protected CompoundTag tag;
	protected BlockPos cablePos;

	public CableStateSyncPacket() {

	}

	public CableStateSyncPacket(BlockPos cablePos, CompoundTag tag) {
		this.cablePos = cablePos;
		this.tag = tag;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(cablePos);
		buffer.writeNbt(tag);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		cablePos = buffer.readBlockPos();
		tag = buffer.readNbt();
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ICableStateSyncTarget target = (ICableStateSyncTarget) Minecraft.getInstance().player.level.getExistingBlockEntity(cablePos);
			if (target != null) {
				target.recieveCableSyncState(tag);
			}
		});
	}
}
