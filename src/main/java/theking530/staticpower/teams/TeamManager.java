package theking530.staticpower.teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import theking530.staticpower.data.StaticPowerGameData;

public class TeamManager extends StaticPowerGameData {
	private static TeamManager INSTANCE;

	private List<Team> teams;

	public TeamManager() {
		super("teams");
		teams = new ArrayList<>();
	}

	/**
	 * Gets the team for the provided player, or empty if no team exists.
	 * 
	 * @param player
	 * @return
	 */
	public Optional<Team> getTeamForPlayer(Player player) {
		for (Team team : teams) {
			if (team.hasPlayer(player)) {
				return Optional.of(team);
			}
		}
		return Optional.empty();
	}

	/**
	 * Creates a team with the provided players. The players array must have at
	 * least one entry, otherwise this is a noop.
	 * 
	 * @param players
	 */
	public void createTeam(Player... players) {
		if (players.length == 0) {
			return;
		}

		// Name the team after the first player in the list and create it.
		String name = String.format("%1$s's Team", players[0].getDisplayName().getString());
		Team newTeam = new Team(name);

		// For all the players, if they're also on another team, remove them from that
		// team.
		for (Player player : players) {
			Team existingTeam = getTeamForPlayer(players[0]).orElse(null);
			if (existingTeam != null) {
				existingTeam.removePlayer(player);
			}
			// Then add them to the new team.
			newTeam.addPlayer(player);
		}

		// Set the initial research.
		newTeam.setCurrentResearch(new ResourceLocation("staticpower:research/your_first_research"));

		// Add the new team to the teams array.
		teams.add(newTeam);
	}

	/**
	 * Gets all the teams on the server.
	 * 
	 * @return
	 */
	public List<Team> getTeams() {
		return Collections.unmodifiableList(teams);
	}

	@Overwrite
	public void load(CompoundTag tag) {
		teams.clear();
		ListTag teamsTag = tag.getList("teams", Tag.TAG_COMPOUND);
		for (Tag teamTag : teamsTag) {
			CompoundTag teamTagCompound = (CompoundTag) teamTag;
			Team team = Team.deserialize(teamTagCompound);
			teams.add(team);
		}
	}

	@Overwrite
	public CompoundTag serialize(CompoundTag tag) {
		ListTag teamsTag = new ListTag();
		teams.forEach(team -> {
			teamsTag.add(team.serialize());
		});
		tag.put("teams", teamsTag);
		return tag;
	}

	public static TeamManager get() {
		if (INSTANCE == null) {
			INSTANCE = new TeamManager();
		}
		return INSTANCE;
	}
}
