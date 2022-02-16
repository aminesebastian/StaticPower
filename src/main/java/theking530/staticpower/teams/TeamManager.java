package theking530.staticpower.teams;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import theking530.staticpower.data.StaticPowerGameData;

public class TeamManager extends StaticPowerGameData {
	private static final Logger LOGGER = LogManager.getLogger(TeamManager.class);
	private static TeamManager INSTANCE;

	private Map<String, Team> teams;

	public TeamManager() {
		super("teams");
		teams = new HashMap<>();

		Team testTeam = new Team("testTeam");
		testTeam.addPlayer("player1");
		testTeam.addPlayer("player2");
		testTeam.addPlayer("player3");
		teams.put(testTeam.getName(), testTeam);
	}

	@Overwrite
	public void load(CompoundTag tag) {
		
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
}
