package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class TileEntityPowerMetricsSyncPacket extends NetworkMessage {
	private BlockPos pos;
	private PowerTransferMetrics metrics;

	public TileEntityPowerMetricsSyncPacket() {

	}

	public TileEntityPowerMetricsSyncPacket(BlockPos pos, PowerTransferMetrics metrics) {
		this.pos = pos;
		this.metrics = metrics;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
		buffer.writeNbt(metrics.serializeNBT());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		pos = BlockPos.of(buffer.readLong());
		metrics = new PowerTransferMetrics();
		metrics.deserializeNBT(buffer.readNbt());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level world = Minecraft.getInstance().player.getCommandSenderWorld();
			if (world.getBlockEntity(pos) instanceof IPowerMetricsSyncConsumer) {
				IPowerMetricsSyncConsumer consumer = (IPowerMetricsSyncConsumer) world.getBlockEntity(pos);
				consumer.recieveMetrics(metrics);
			}
		});
	}
}
