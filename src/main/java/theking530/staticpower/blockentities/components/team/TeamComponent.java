package theking530.staticpower.blockentities.components.team;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.components.AbstractTileEntityComponent;
import theking530.staticpower.blockentities.components.serialization.UpdateSerialize;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.teams.Team;
import theking530.staticpower.teams.TeamManager;

public class TeamComponent extends AbstractTileEntityComponent {
	@UpdateSerialize()
	private String teamId;

	public TeamComponent(String name) {
		super(name);
		teamId = "missing";
	}

	@Override
	public void onPlaced(BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player) {
			Team placerTeam = TeamManager.get().getTeamForPlayer((Player) placer);
			if (placerTeam != null) {
				teamId = placerTeam.getId().toString();
			}
		}
	}

	public void setTeam(String teamId) {
		if (getLevel().isClientSide()) {
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, new PacketSetTeamComponentTeam(getTileEntity(), teamId));
		} else {
			this.teamId = teamId;
		}
	}

	public Team getOwningTeam() {
		if (teamId == "missing") {
			return null;
		}
		return TeamManager.get().getTeamById(teamId);
	}
}
