package theking530.staticcore.blockentity.components.team;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.teams.ITeam;

public class TeamUtilities {
	public boolean isBlockOwnedByTeam(Level level, BlockPos pos) {
		return getTeamOwningBlock(level, pos) != null;
	}

	public ITeam getTeamOwningBlock(Level level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) {
			return null;
		}

		TeamComponent teamComp = ComponentUtilities.getComponent(TeamComponent.class, be).orElse(null);
		if (teamComp != null) {
			return teamComp.getOwningTeam();
		}

		
		
		
		return null;

	}
}
