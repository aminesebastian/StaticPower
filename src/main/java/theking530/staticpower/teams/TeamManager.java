package theking530.staticpower.teams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import theking530.staticpower.data.StaticPowerGameData;

public class TeamManager extends StaticPowerGameData {
	private static TeamManager INSTANCE;
	private Map<UUID, Team> teams;

	public TeamManager() {
		super("teams");
		teams = new HashMap<>();
	}

	@Override
	public void tick() {
		boolean isDirty = false;
		for (Team team : teams.values()) {
			isDirty &= team.isDirty();
			team.markDirty(false);
		}
		if (isDirty) {
			syncToClients();
		}
	}

	/**
	 * Gets the team for the provided player, or empty if no team exists.
	 * 
	 * @param player
	 * @return
	 */
	public Optional<Team> getTeamForPlayer(Player player) {
		for (Team team : teams.values()) {
			if (team.hasPlayer(player)) {
				return Optional.of(team);
			}
		}
		return Optional.empty();
	}

	/**
	 * Gets the team with the provided id, or empty if not found.
	 * 
	 * @param teamId
	 * @return
	 */
	public Optional<Team> getTeamById(UUID teamId) {
		if (teams.containsKey(teamId)) {
			return Optional.of(teams.get(teamId));
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
		newTeam.getResearchManager().setSelectedResearch(new ResourceLocation("staticpower:research/basic_research"));

		// Add the new team to the teams array.
		teams.put(newTeam.getId(), newTeam);
	}

	/**
	 * Gets all the teams on the server.
	 * 
	 * @return
	 */
	public List<Team> getTeams() {
		return List.copyOf(teams.values());
	}

	@Overwrite
	public void load(CompoundTag tag) {
		teams.clear();
		ListTag teamsTag = tag.getList("teams", Tag.TAG_COMPOUND);
		for (Tag teamTag : teamsTag) {
			CompoundTag teamTagCompound = (CompoundTag) teamTag;
			Team team = Team.deserialize(teamTagCompound);
			teams.put(team.getId(), team);
		}
	}

	@Overwrite
	public CompoundTag serialize(CompoundTag tag) {
		ListTag teamsTag = new ListTag();
		teams.values().forEach(team -> {
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

	@SuppressWarnings("resource")
	public static Team getLocalTeam() {
		return TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player).orElse(null);
	}

	@Override
	public String toString() {
		return "TeamManager [teams=" + teams + "]";
	}
}
