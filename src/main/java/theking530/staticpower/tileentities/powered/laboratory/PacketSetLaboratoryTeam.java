package theking530.staticpower.tileentities.powered.laboratory;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;

public class PacketSetLaboratoryTeam extends NetworkMessage {
	protected UUID teamId;
	protected BlockPos tileEntityPosition;

	public PacketSetLaboratoryTeam() {

	}

	public PacketSetLaboratoryTeam(TileEntityBase tileEntity, UUID teamId) {
		tileEntityPosition = tileEntity.getBlockPos();
		this.teamId = teamId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeUUID(teamId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		tileEntityPosition = buffer.readBlockPos();
		teamId = buffer.readUUID();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().level.isAreaLoaded(tileEntityPosition, 1)) {
				BlockEntity rawTileEntity = ctx.get().getSender().level.getBlockEntity(tileEntityPosition);
				if (rawTileEntity != null && rawTileEntity instanceof TileEntityLaboratory) {
					TileEntityLaboratory laboratory = (TileEntityLaboratory) rawTileEntity;
					laboratory.setTeam(teamId);
				}
			}
		});
	}
}