package theking530.staticcore.data;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class StaticPowerGameDataSyncPacket extends NetworkMessage {
	protected ResourceLocation id;
	protected CompoundTag serializedData;

	public StaticPowerGameDataSyncPacket() {

	}

	public StaticPowerGameDataSyncPacket(StaticPowerGameData data) {
		id = data.getId();
		serializedData = data.serialize(new CompoundTag());
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(id.toString());
		buffer.writeNbt(serializedData);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		id = new ResourceLocation(buffer.readUtf());
		serializedData = buffer.readNbt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		StaticPowerGameData gameData = StaticPowerGameDataManager.getOrCreateaGameData(id);
		gameData.deserialize(serializedData);
		gameData.onSyncedFromServer(serializedData);
	}
}