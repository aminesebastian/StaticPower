package theking530.staticpower.blockentities.power.transformer;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class TransformerControlSyncPacket extends NetworkMessage {
	private BlockPos position;
	private boolean isVoltage;
	private double delta;

	public TransformerControlSyncPacket() {
	}

	public TransformerControlSyncPacket(BlockPos pos, boolean voltage, double delta) {
		this.position = pos;
		this.isVoltage = voltage;
		this.delta = delta;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(position);
		buffer.writeBoolean(isVoltage);
		buffer.writeDouble(delta);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		position = buffer.readBlockPos();
		isVoltage = buffer.readBoolean();
		delta = buffer.readDouble();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().getLevel().isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = ctx.get().getSender().getLevel().getBlockEntity(position);
				if (rawTileEntity != null && rawTileEntity instanceof BlockEntityTransformer) {
					BlockEntityTransformer battery = (BlockEntityTransformer) rawTileEntity;
					if (isVoltage) {
						battery.addOutputVoltageDelta(delta);
					} else {
						battery.addMaximumOutputCurrentDelta(delta);
					}
				}
			}
		});
	}
}
