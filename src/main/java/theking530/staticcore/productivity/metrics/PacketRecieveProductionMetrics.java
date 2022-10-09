package theking530.staticcore.productivity.metrics;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.teams.TeamManager;

public class PacketRecieveProductionMetrics extends NetworkMessage {
	private SertializedBiDirectionalMetrics metrics;
	private ProductType<?> productType;

	public PacketRecieveProductionMetrics() {

	}

	public PacketRecieveProductionMetrics(ProductType<?> productType, SertializedBiDirectionalMetrics metrics) {
		this.metrics = metrics;
		this.productType = productType;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(productType.getRegistryName().toString());
		metrics.encode(buffer);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		productType = StaticPowerRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		metrics = SertializedBiDirectionalMetrics.decode(buffer);
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TeamManager.getLocalTeam().getProductionManager().setClientSyncedMetrics(productType, metrics);
		});
	}
}
