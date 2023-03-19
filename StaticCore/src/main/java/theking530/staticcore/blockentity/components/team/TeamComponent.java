package theking530.staticcore.blockentity.components.team;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.teams.Team;
import theking530.staticcore.teams.TeamManager;

public class TeamComponent extends AbstractBlockEntityComponent {
	@UpdateSerialize()
	private String teamId;

	public TeamComponent(String name) {
		super(name);
		teamId = "missing";
	}

	@Override
	public void onOwningBlockEntityFirstPlaced(BlockPlaceContext context, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player) {
			Team placerTeam = TeamManager.get(context.getLevel()).getTeamForPlayer((Player) placer);
			if (placerTeam != null) {
				teamId = placerTeam.getId().toString();
			}
		}
	}

	public void setTeam(String teamId) {
		if (getLevel().isClientSide()) {
			StaticCoreMessageHandler.sendToServer(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, new PacketSetTeamComponentTeam(getBlockEntity(), teamId));
		} else {
			this.teamId = teamId;
		}
	}

	public Team getOwningTeam() {
		if (teamId == "missing") {
			return null;
		}
		return TeamManager.get(getLevel()).getTeamById(teamId);
	}
}
