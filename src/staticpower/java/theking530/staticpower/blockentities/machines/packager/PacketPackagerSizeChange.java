package theking530.staticpower.blockentities.machines.packager;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.world.WorldUtilities;

public class PacketPackagerSizeChange extends NetworkMessage {
	private BlockPos position;
	private int size;

	public PacketPackagerSizeChange() {
	}

	public PacketPackagerSizeChange(BlockEntityPackager packager, int size) {
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
			if (WorldUtilities.isBlockPosInLoadedChunk(context.get().getSender().level, position)) {
				BlockEntity rawTileEntity = context.get().getSender().getCommandSenderWorld().getBlockEntity(position);
				if (rawTileEntity instanceof BlockEntityPackager) {
					BlockEntityPackager packager = (BlockEntityPackager) rawTileEntity;
					packager.setRecipeSize(size);
				}
			}
		});
	}
}
