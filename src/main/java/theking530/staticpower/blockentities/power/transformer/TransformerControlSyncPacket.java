package theking530.staticpower.blockentities.power.transformer;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.network.NetworkMessage;

public class TransformerControlSyncPacket extends NetworkMessage {
	private BlockPos position;
	private StaticPowerVoltage voltage;
	private double powerDelta;

	public TransformerControlSyncPacket() {
	}

	public TransformerControlSyncPacket(BlockPos pos, StaticPowerVoltage voltage, double powerDelta) {
		this.position = pos;
		this.voltage = voltage;
		this.powerDelta = powerDelta;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(position);
		buffer.writeByte(voltage.ordinal());
		buffer.writeDouble(powerDelta);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		position = buffer.readBlockPos();
		voltage = StaticPowerVoltage.values()[buffer.readByte()];
		powerDelta = buffer.readDouble();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().getLevel().isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = ctx.get().getSender().getLevel().getBlockEntity(position);
				if (rawTileEntity != null && rawTileEntity instanceof BlockEntityTransformer) {
					BlockEntityTransformer battery = (BlockEntityTransformer) rawTileEntity;
					battery.setOutputVoltage(voltage);
					battery.addMaximumOutputPowerDelta(powerDelta);
				}
			}
		});
	}
}
