package theking530.staticpower.blockentities.power.transformer;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class TransformerRatioPacket extends NetworkMessage {
	private BlockPos position;
	private byte ratio;

	public TransformerRatioPacket() {
	}

	public TransformerRatioPacket(BlockPos pos, byte ratio) {
		this.position = pos;
		this.ratio = ratio;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(position);
		buffer.writeByte(ratio);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		position = buffer.readBlockPos();
		ratio = buffer.readByte();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().getLevel().isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = ctx.get().getSender().getLevel().getBlockEntity(position);
				if (rawTileEntity != null && rawTileEntity instanceof BlockEntityTransformer) {
					BlockEntityTransformer battery = (BlockEntityTransformer) rawTileEntity;
					battery.setTransformerRatio(ratio);
				}
			}
		});
	}
}
