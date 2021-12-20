package theking530.staticpower.tileentities.components.power;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
	public void encode(PacketBuffer buffer) {
		buffer.writeLong(pos.toLong());
		buffer.writeCompoundTag(metrics.serializeNBT());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		pos = BlockPos.fromLong(buffer.readLong());
		metrics = new PowerTransferMetrics();
		metrics.deserializeNBT(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World world = Minecraft.getInstance().player.getEntityWorld();
			if (world.getTileEntity(pos) instanceof IPowerMetricsSyncConsumer) {
				IPowerMetricsSyncConsumer consumer = (IPowerMetricsSyncConsumer) world.getTileEntity(pos);
				consumer.recieveMetrics(metrics);
			}
		});
	}
}
