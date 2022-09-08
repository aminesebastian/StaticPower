package theking530.staticpower.blockentities.digistorenetwork.digistore;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class PacketLockDigistore extends NetworkMessage {
	private boolean isLocked;
	private BlockPos tePosition;

	public PacketLockDigistore() {
	}

	public PacketLockDigistore(boolean isLocked, BlockPos pos) {
		this.isLocked = isLocked;
		this.tePosition = pos;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		isLocked = buf.readBoolean();
		tePosition = buf.readBlockPos();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(isLocked);
		buf.writeBlockPos(tePosition);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			BlockEntity rawTileEntity = context.get().getSender().level.getBlockEntity(tePosition);

			if (rawTileEntity != null && rawTileEntity instanceof TileEntityDigistore) {
				TileEntityDigistore digistore = (TileEntityDigistore) rawTileEntity;
				digistore.setLocked(isLocked);
			}
		});
	}
}
