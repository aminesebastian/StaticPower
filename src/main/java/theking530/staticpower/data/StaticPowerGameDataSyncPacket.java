package theking530.staticpower.data;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.StaticPowerRegistry;
import theking530.staticpower.network.NetworkMessage;

public class StaticPowerGameDataSyncPacket extends NetworkMessage {
	protected String name;
	protected CompoundTag serializedData;

	public StaticPowerGameDataSyncPacket() {

	}

	public StaticPowerGameDataSyncPacket(StaticPowerGameData data) {
		name = data.getName();
		serializedData = data.serialize(new CompoundTag());
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(name);
		buffer.writeNbt(serializedData);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		name = buffer.readUtf();
		serializedData = buffer.readNbt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		StaticPowerGameData gameData = StaticPowerRegistry.getGameDataByName(name);
		gameData.load(serializedData);
		gameData.onSyncedFromServer();
	}
}
