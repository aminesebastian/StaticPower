package theking530.staticcore.productivity.metrics;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.teams.productivity.GuiProductionMenu;

public class PacketRecieveProductionTimeline extends NetworkMessage {
	private ProductType<?> productType;
	private int productHashCode;
	private MetricPeriod period;
	private ProductivityTimeline timeline;

	public PacketRecieveProductionTimeline() {

	}

	public PacketRecieveProductionTimeline(ProductType<?> productType, int productHashCode, MetricPeriod period, ProductivityTimeline timeline) {
		this.productType = productType;
		this.productHashCode = productHashCode;
		this.period = period;
		this.timeline = timeline;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		timeline.encode(buffer);
		buffer.writeUtf(StaticPowerRegistries.ProductRegistry().getKey(productType).toString());
		buffer.writeInt(productHashCode);
		buffer.writeByte(period.ordinal());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		timeline = ProductivityTimeline.decode(buffer);
		productType = StaticPowerRegistries.ProductRegistry().getValue(new ResourceLocation(buffer.readUtf()));
		productHashCode = buffer.readInt();
		period = MetricPeriod.values()[buffer.readByte()];
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			GuiProductionMenu productionMenu = (GuiProductionMenu) Minecraft.getInstance().screen;
			if (productionMenu != null) {
				productionMenu.recieveTimelineData(productType, productHashCode, period, timeline);
			}
		});
	}
}
