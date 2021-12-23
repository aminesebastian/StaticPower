package theking530.staticpower.entities.player.datacapability;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketSyncStaticPowerPlayerDataCapability extends NetworkMessage {
	protected CompoundTag playerCapabilityData;

	public PacketSyncStaticPowerPlayerDataCapability() {

	}

	public PacketSyncStaticPowerPlayerDataCapability(CompoundTag playerCapabilityData) {
		this.playerCapabilityData = playerCapabilityData;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(playerCapabilityData);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		playerCapabilityData = buffer.readNbt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft.getInstance().player.getCapability(CapabilityStaticPowerPlayerData.PLAYER_CAPABILITY).ifPresent((data) -> {
				((StaticPowerPlayerData) data).deserializeNBT(playerCapabilityData);
			});
		});
	}
}
