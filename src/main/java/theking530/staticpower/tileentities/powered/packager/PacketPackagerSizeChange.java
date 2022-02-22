package theking530.staticpower.tileentities.powered.packager;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class PacketPackagerSizeChange extends NetworkMessage {
	private BlockPos position;
	private int size;

	public PacketPackagerSizeChange() {
	}

	public PacketPackagerSizeChange(TileEntityPackager packager, int size) {
		this.position = packager.getBlockPos();
		this.size = size;
	}

	@Override
	public void decode(FriendlyByteBuf buf) {
		position = buf.readBlockPos();
		size = buf.readInt();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(position);
		buf.writeInt(size);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			if (context.get().getSender().getCommandSenderWorld().isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = context.get().getSender().getCommandSenderWorld().getBlockEntity(position);
				if (rawTileEntity instanceof TileEntityPackager) {
					TileEntityPackager packager = (TileEntityPackager) rawTileEntity;
					packager.setRecipeSize(size);
				}
			}
		});
	}
}
