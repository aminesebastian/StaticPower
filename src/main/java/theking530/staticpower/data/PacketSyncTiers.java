package theking530.staticpower.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketSyncTiers extends NetworkMessage {
	protected Collection<StaticPowerTier> tiers;

	public PacketSyncTiers() {

	}

	public PacketSyncTiers(Collection<StaticPowerTier> tiers) {
		this.tiers = tiers;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeByte(tiers.size());
		for (StaticPowerTier tier : tiers) {
			buffer.writeCompoundTag(tier.writeToNbt());
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		int count = buffer.readByte();
		tiers = new ArrayList<StaticPowerTier>();
		for (int i = 0; i < count; i++) {
			StaticPowerTier tier = StaticPowerTier.readFromNbt(buffer.readCompoundTag());
			tiers.add(tier);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TierReloadListener.updateFromServer(tiers);
		});
	}
}