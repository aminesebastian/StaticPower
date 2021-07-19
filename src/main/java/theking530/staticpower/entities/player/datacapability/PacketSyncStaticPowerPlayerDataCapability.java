package theking530.staticpower.entities.player.datacapability;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketSyncStaticPowerPlayerDataCapability extends NetworkMessage {
	protected CompoundNBT playerCapabilityData;

	public PacketSyncStaticPowerPlayerDataCapability() {

	}

	public PacketSyncStaticPowerPlayerDataCapability(CompoundNBT playerCapabilityData) {
		this.playerCapabilityData = playerCapabilityData;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeCompoundTag(playerCapabilityData);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		playerCapabilityData = buffer.readCompoundTag();
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
