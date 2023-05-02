package theking530.staticcore.blockentity.components.team;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.team.CapabilityTeamOwnable;
import theking530.api.team.ITeamOwnable;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.teams.ITeam;
import theking530.staticcore.teams.TeamManager;

public class TeamComponent extends AbstractBlockEntityComponent implements ITeamOwnable {
	@UpdateSerialize()
	private String teamId;

	public TeamComponent(String name) {
		super(name);
		teamId = null;
	}

	@Override
	public void onOwningBlockEntityFirstPlaced(BlockPlaceContext context, BlockState state,
			@Nullable LivingEntity placer, ItemStack stack) {
	}

	public void process() {
	}

	@Override
	public void setTeam(String teamId) {
		this.teamId = teamId;
	}

	@Override
	public ITeam getOwningTeam() {
		if (!hasTeam()) {
			return null;
		}
		return TeamManager.get(getLevel()).getTeamById(teamId);
	}

	@Override
	public boolean hasTeam() {
		return teamId != null;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityTeamOwnable.TEAM_OWNABLE_CAPABILITY) {
			return LazyOptional.of(() -> this).cast();
		}
		return LazyOptional.empty();
	}
}
