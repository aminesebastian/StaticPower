package theking530.staticcore.blockentity.components.team;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.api.team.CapabilityTeamOwnable;
import theking530.api.team.ITeamOwnable;
import theking530.staticcore.StaticCore;
import theking530.staticcore.network.NetworkMessage;

public class PacketSetBlockEntityTeamFromServer extends NetworkMessage {
	protected String teamId;
	protected BlockPos blockEntityPos;

	public PacketSetBlockEntityTeamFromServer() {

	}

	public PacketSetBlockEntityTeamFromServer(BlockEntity tileEntity, String teamId) {
		blockEntityPos = tileEntity.getBlockPos();
		this.teamId = teamId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(blockEntityPos);
		buffer.writeUtf(teamId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		blockEntityPos = buffer.readBlockPos();
		teamId = buffer.readUtf();
	}

	@SuppressWarnings({ "deprecation", "resource" })
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().level.isAreaLoaded(blockEntityPos, 1)) {
				BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(blockEntityPos);
				ITeamOwnable ownable = blockEntity.getCapability(CapabilityTeamOwnable.TEAM_OWNABLE_CAPABILITY)
						.orElse(null);
				if (ownable != null) {
					ownable.setTeam(teamId);
					StaticCore.LOGGER.info(String.format("Team: %1$s now owns the block entity at position: %2$s.",
							teamId, blockEntityPos.toString()));
				}
			}
		});
	}
}