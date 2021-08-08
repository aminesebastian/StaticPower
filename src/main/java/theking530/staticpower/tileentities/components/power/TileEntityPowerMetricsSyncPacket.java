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
	private TransferMetrics secondsMetrics;
	private TransferMetrics minuteMetrics;
	private TransferMetrics hourlyMetrics;

	public TileEntityPowerMetricsSyncPacket() {

	}

	/**
	 * @param secondsMetrics
	 * @param minuteMetrics
	 * @param hourlyMetrics
	 */
	public TileEntityPowerMetricsSyncPacket(BlockPos pos, TransferMetrics secondsMetrics, TransferMetrics minuteMetrics, TransferMetrics hourlyMetrics) {
		this.pos = pos;
		this.secondsMetrics = secondsMetrics;
		this.minuteMetrics = minuteMetrics;
		this.hourlyMetrics = hourlyMetrics;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeLong(pos.toLong());
		buffer.writeCompoundTag(secondsMetrics.serializeNBT());
		buffer.writeCompoundTag(minuteMetrics.serializeNBT());
		buffer.writeCompoundTag(hourlyMetrics.serializeNBT());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		pos = BlockPos.fromLong(buffer.readLong());
		secondsMetrics = new TransferMetrics();
		secondsMetrics.deserializeNBT(buffer.readCompoundTag());

		minuteMetrics = new TransferMetrics();
		minuteMetrics.deserializeNBT(buffer.readCompoundTag());

		hourlyMetrics = new TransferMetrics();
		hourlyMetrics.deserializeNBT(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World world = Minecraft.getInstance().player.getEntityWorld();
			if (world.getTileEntity(pos) instanceof IPowerMetricsSyncConsumer) {
				IPowerMetricsSyncConsumer powerCableContainer = (IPowerMetricsSyncConsumer) world.getTileEntity(pos);
				powerCableContainer.recieveMetrics(secondsMetrics, minuteMetrics, hourlyMetrics);
			}
		});
	}
}
