package theking530.staticcore.teams;

import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.api.team.CapabilityTeamOwnable;
import theking530.staticcore.StaticCore;
import theking530.staticcore.blockentity.components.team.PacketSetBlockEntityTeamFromClient;
import theking530.staticcore.blockentity.components.team.PacketSetBlockEntityTeamFromServer;
import theking530.staticcore.network.StaticCoreMessageHandler;

public class TeamUtilities {
	public static void setOwningTeam(BlockEntity blockEntity, ITeam team) {
		if (blockEntity != null && team != null) {
			blockEntity.getCapability(CapabilityTeamOwnable.TEAM_OWNABLE_CAPABILITY).ifPresent((teamOwnable) -> {
				if (team != null) {
					teamOwnable.setTeam(team.getId());
					StaticCore.LOGGER.info(String.format("Team: %1$s now owns the block entity at position: %2$s.",
							team.getId(), blockEntity.getBlockPos().toString()));

					if (blockEntity.getLevel().isClientSide()) {
						StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
								new PacketSetBlockEntityTeamFromClient(blockEntity, team.getId()));
					} else {
						StaticCoreMessageHandler.sendToAllPlayers(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
								new PacketSetBlockEntityTeamFromServer(blockEntity, team.getId()));
					}
				}
			});
		}
	}
}
