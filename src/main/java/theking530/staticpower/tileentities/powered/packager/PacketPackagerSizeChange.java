package theking530.staticpower.tileentities.powered.packager;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;

public class PacketPackagerSizeChange extends NetworkMessage {
	private BlockPos position;
	private int size;

	public PacketPackagerSizeChange() {
	}

	public PacketPackagerSizeChange(TileEntityPackager packager, int size) {
		this.position = packager.getPos();
		this.size = size;
	}

	@Override
	public void decode(PacketBuffer buf) {
		position = buf.readBlockPos();
		size = buf.readInt();
	}

	@Override
	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(position);
		buf.writeInt(size);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().getEntityWorld().isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = context.get().getSender().getEntityWorld().getTileEntity(position);
				if (rawTileEntity instanceof TileEntityPackager) {
					TileEntityPackager packager = (TileEntityPackager) rawTileEntity;
					packager.setRecipeSize(size);
				}
			}
		});
	}
}
