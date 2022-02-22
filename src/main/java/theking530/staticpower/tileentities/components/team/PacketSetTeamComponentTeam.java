package theking530.staticpower.tileentities.components.team;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.tileentities.TileEntityBase;

public class PacketSetTeamComponentTeam extends NetworkMessage {
	protected String teamId;
	protected BlockPos tileEntityPosition;

	public PacketSetTeamComponentTeam() {

	}

	public PacketSetTeamComponentTeam(TileEntityBase tileEntity, String teamId) {
		tileEntityPosition = tileEntity.getBlockPos();
		this.teamId = teamId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(tileEntityPosition);
		buffer.writeUtf(teamId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		tileEntityPosition = buffer.readBlockPos();
		teamId = buffer.readUtf();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().level.isAreaLoaded(tileEntityPosition, 1)) {
				BlockEntity rawTileEntity = ctx.get().getSender().level.getBlockEntity(tileEntityPosition);
				if (rawTileEntity != null && rawTileEntity instanceof TileEntityBase) {
					TileEntityBase base = (TileEntityBase) rawTileEntity;
					base.getTeamComponent().setTeam(teamId);
				}
			}
		});
	}
}