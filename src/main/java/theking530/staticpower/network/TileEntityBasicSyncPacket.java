package theking530.staticpower.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;
import theking530.staticpower.StaticPower;
import theking530.staticpower.tileentities.TileEntityBase;

/**
 * Packet used to synchronize a {@link TileEntityBase} ad hoc. This should
 * usually only be sent to a specific player looking inside a GUI. But if it
 * needs to be sent to multiple players, be sure to get players only in the same
 * dimension of the TileEntity and within a certain radius.
 * 
 * @author Amine Sebastian
 *
 */
public class TileEntityBasicSyncPacket extends NetworkMessage {
	protected CompoundTag machineUpdateTag;
	protected BlockPos tileEntityPosition;
	protected boolean shouldReRender;

	public TileEntityBasicSyncPacket() {

	}

	public TileEntityBasicSyncPacket(TileEntityBase tileEntity, boolean shouldReRender) {
		tileEntityPosition = tileEntity.getBlockPos();
		this.shouldReRender = shouldReRender;
		machineUpdateTag = new CompoundTag();
		tileEntity.serializeUpdateNbt(machineUpdateTag, true);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeBoolean(shouldReRender);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			NbtIo.writeCompressed(machineUpdateTag, out);
			buffer.writeByteArray(out.toByteArray());
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to serialize a tile entity's data.", e);
		}
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		tileEntityPosition = buffer.readBlockPos();
		shouldReRender = buffer.readBoolean();
		byte[] compressedData = buffer.readByteArray();

		try {
			machineUpdateTag = NbtIo.readCompressed(new ByteArrayInputStream(compressedData));
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to deserialize a tile entity's data.", e);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.level.isAreaLoaded(tileEntityPosition, 1)) {
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(tileEntityPosition);
				if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
					TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
					tileEntity.deserializeUpdateNbt(machineUpdateTag, true);

					// If a render update was requested, perform that too.
					if (shouldReRender) {
						tileEntity.addRenderingUpdateRequest();
					}
				}
			}
		});
	}
}
