package theking530.staticpower.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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

	public TileEntityBasicSyncPacket() {

	}

	public TileEntityBasicSyncPacket(TileEntityBase tileEntity) {
		tileEntityPosition = tileEntity.getPos();
		machineUpdateTag = new CompoundNBT();
		tileEntity.serializeUpdateNbt(machineUpdateTag, true);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeCompoundTag(machineUpdateTag);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		tileEntityPosition = buffer.readBlockPos();
		machineUpdateTag = buffer.readCompoundTag();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(tileEntityPosition);
		if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
			TileEntityBase tileEntity = (TileEntityBase) rawTileEntity;
			tileEntity.deserializeUpdateNbt(machineUpdateTag, true);
		}
	}

}
