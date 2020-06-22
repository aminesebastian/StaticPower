package theking530.staticpower.tileentities.nonpowered.digistorenetwork.digistore;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

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
	public void decode(PacketBuffer buf) {
		isLocked = buf.readBoolean();
		tePosition = buf.readBlockPos();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBoolean(isLocked);
		buf.writeBlockPos(tePosition);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			TileEntity rawTileEntity = context.get().getSender().world.getTileEntity(tePosition);

			if (rawTileEntity != null && rawTileEntity instanceof TileEntityDigistore) {
				TileEntityDigistore digistore = (TileEntityDigistore) rawTileEntity;
				digistore.setLocked(isLocked);
			}
		});
	}
}
