package theking530.staticpower.tileentities.powered.battery;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class BatteryControlSyncPacket extends NetworkMessage {
	private int inputPerTick;
	private int outputPerTick;
	private BlockPos position;

	public BatteryControlSyncPacket() {
	}

	public BatteryControlSyncPacket(int inputPerTick, int outputPerTick, BlockPos pos) {
		this.inputPerTick = inputPerTick;
		this.outputPerTick = outputPerTick;
		this.position = pos;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(inputPerTick);
		buffer.writeInt(outputPerTick);
		buffer.writeBlockPos(position);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		inputPerTick = buffer.readInt();
		outputPerTick = buffer.readInt();
		position = buffer.readBlockPos();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().getServerWorld().isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = ctx.get().getSender().getServerWorld().getTileEntity(position);
				if (rawTileEntity != null && rawTileEntity instanceof TileEntityBattery) {
					TileEntityBattery battery = (TileEntityBattery) rawTileEntity;
					battery.setInputLimit(inputPerTick);
					battery.setOutputLimit(outputPerTick);
					System.out.println("Input: " + inputPerTick);
					System.out.println("Output: " + outputPerTick);
				}
			}
		});
	}
}
