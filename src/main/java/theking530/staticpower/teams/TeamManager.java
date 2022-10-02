package theking530.staticpower.teams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticcore.data.StaticPowerGameData;
import theking530.staticcore.data.StaticPowerGameDataManager;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModResearch;

public class TeamManager extends StaticPowerGameData {
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "teams");
	private Map<String, Team> teams;

	public TeamManager() {
		super(ID);
		teams = new HashMap<String, Team>();
	}

	@Override
	public void tick(Level level) {
		boolean isDirty = false;
		for (Team team : teams.values()) {
			team.tick(level);
			if (team.isDirty()) {
				isDirty = true;
				team.markDirty(false);
			}
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
	public Team getTeamForPlayer(Player player) {
		for (Team team : teams.values()) {
			if (team.hasPlayer(player)) {
				return team;
			}
		}
		return null;
	}

	/**
	 * Gets the team with the provided id, or empty if not found.
	 * 
	 * @param teamId
	 * @return
	 */
	public Team getTeamById(String teamId) {
		if (teams.containsKey(teamId)) {
			return teams.get(teamId);
		}
		return null;
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
			Team existingTeam = getTeamForPlayer(players[0]);
			if (existingTeam != null) {
				existingTeam.removePlayer(player);
			}
			// Then add them to the new team.
			newTeam.addPlayer(player);
		}

		// Set the initial research.
		newTeam.getResearchManager().setSelectedResearch(ModResearch.BASIC_RESEARCH);

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
	public void loadFromDisk(CompoundTag tag) {
		teams.clear();
		ListTag teamsTag = tag.getList("teams", Tag.TAG_COMPOUND);
		for (Tag teamTag : teamsTag) {
			CompoundTag teamTagCompound = (CompoundTag) teamTag;
			Team team = Team.fromTag(teamTagCompound);
			teams.put(team.getId(), team);
		}
	}

	@Override
	public void deserialize(CompoundTag tag) {
		ListTag teamsTag = tag.getList("teams", Tag.TAG_COMPOUND);
		for (Tag teamTag : teamsTag) {
			CompoundTag teamTagCompound = (CompoundTag) teamTag;
			String teamId = teamTagCompound.getString("id");
			if(teams.containsKey(teamId)) {
				teams.get(teamId).deserialize(teamTagCompound);
			}else {
				teams.put(teamId, Team.fromTag(teamTagCompound));
			}
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
		return StaticPowerGameDataManager.getOrCreateaGameData(ID);
	}

	@SuppressWarnings("resource")
	@OnlyIn(Dist.CLIENT)
	public static Team getLocalTeam() {
		return TeamManager.get().getTeamForPlayer(Minecraft.getInstance().player);
	}

	@Override
	public String toString() {
		return "TeamManager [teams=" + teams + "]";
	}
}
