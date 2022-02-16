package theking530.staticpower.teams;

import java.util.HashSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class Team {
	private String name;
	private final HashSet<String> players;

	public Team(String name) {
		players = new HashSet<String>();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashSet<String> getPlayers() {
		return players;
	}

	public void addPlayer(String playerId) {
		this.players.add(playerId);
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		output.putString("name", name);

		ListTag playersTag = new ListTag();
		players.forEach(player -> {
			CompoundTag playerTag = new CompoundTag();
			playerTag.putString("id", player);
			playersTag.add(playerTag);
		});
		output.put("players", playersTag);

		return output;
	}
}
