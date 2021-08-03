package theking530.staticpower.tileentities.powered.powermonitor;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketPowerMonitorSync extends NetworkMessage {
	private long inputPerTick;
	private long outputPerTick;
	private BlockPos position;

	public PacketPowerMonitorSync() {
	}

	public PacketPowerMonitorSync(long inputPerTick, long outputPerTick, BlockPos pos) {
		this.inputPerTick = inputPerTick;
		this.outputPerTick = outputPerTick;
		this.position = pos;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeLong(inputPerTick);
		buffer.writeLong(outputPerTick);
		buffer.writeBlockPos(position);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		inputPerTick = buffer.readLong();
		outputPerTick = buffer.readLong();
		position = buffer.readBlockPos();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().getServerWorld().isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = ctx.get().getSender().getServerWorld().getTileEntity(position);
				if (rawTileEntity != null && rawTileEntity instanceof TileEntityPowerMonitor) {
					TileEntityPowerMonitor battery = (TileEntityPowerMonitor) rawTileEntity;
					battery.setInputLimit(inputPerTick);
					battery.setOutputLimit(outputPerTick);
				}
			}
		});
	}
}
