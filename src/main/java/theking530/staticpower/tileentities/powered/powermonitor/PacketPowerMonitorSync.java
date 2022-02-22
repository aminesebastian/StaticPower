package theking530.staticpower.tileentities.powered.powermonitor;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

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
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(inputPerTick);
		buffer.writeLong(outputPerTick);
		buffer.writeBlockPos(position);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		inputPerTick = buffer.readLong();
		outputPerTick = buffer.readLong();
		position = buffer.readBlockPos();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().getLevel().isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = ctx.get().getSender().getLevel().getBlockEntity(position);
				if (rawTileEntity != null && rawTileEntity instanceof TileEntityPowerMonitor) {
					TileEntityPowerMonitor battery = (TileEntityPowerMonitor) rawTileEntity;
					battery.setInputLimit(inputPerTick);
					battery.setOutputLimit(outputPerTick);
				}
			}
		});
	}
}
