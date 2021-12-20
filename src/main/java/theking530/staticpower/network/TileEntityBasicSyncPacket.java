package theking530.staticpower.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
	protected CompoundNBT machineUpdateTag;
	protected BlockPos tileEntityPosition;
	protected boolean shouldReRender;

	public TileEntityBasicSyncPacket() {

	}

	public TileEntityBasicSyncPacket(TileEntityBase tileEntity, boolean shouldReRender) {
		tileEntityPosition = tileEntity.getPos();
		this.shouldReRender = shouldReRender;
		machineUpdateTag = new CompoundNBT();
		tileEntity.serializeUpdateNbt(machineUpdateTag, true);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeBoolean(shouldReRender);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			CompressedStreamTools.writeCompressed(machineUpdateTag, out);
			buffer.writeByteArray(out.toByteArray());
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to serialize a tile entity's data.", e);
		}
	}

	@Override
	public void decode(PacketBuffer buffer) {
		tileEntityPosition = buffer.readBlockPos();
		shouldReRender = buffer.readBoolean();
		byte[] compressedData = buffer.readByteArray();

		try {
			machineUpdateTag = CompressedStreamTools.readCompressed(new ByteArrayInputStream(compressedData));
		} catch (Exception e) {
			StaticPower.LOGGER.error("An error occured when attempting to deserialize a tile entity's data.", e);
		}
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.world.isAreaLoaded(tileEntityPosition, 1)) {
				TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(tileEntityPosition);
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
