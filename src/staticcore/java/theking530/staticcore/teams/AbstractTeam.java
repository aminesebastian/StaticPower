package theking530.staticcore.teams;

import java.util.HashSet;
import java.util.LinkedHashSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import theking530.staticcore.utilities.NBTUtilities;

public abstract class AbstractTeam implements ITeam {
	private final HashSet<String> players;
	private String id;
	private String name;
	private boolean dirty;

	public AbstractTeam(String name, String id) {
		this.id = id.replace("-", "");
		players = new LinkedHashSet<String>();

		this.name = name;
	}

	@Override
	public void tick(Level level) {
		level.getProfiler().push("ProductionManager.Tick");
		getProductionManager().tick(level.getGameTime());
		level.getProfiler().pop();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public HashSet<String> getPlayers() {
		return players;
	}

	@Override
	public boolean hasPlayer(Player player) {
		return players.contains(player.getStringUUID());
	}

	@Override
	public void addPlayer(Player player) {
		this.players.add(player.getStringUUID());
	}

	@Override
	public boolean removePlayer(Player player) {
		return this.players.remove(player.getStringUUID());
	}

	@Override
	public void markDirty(boolean dirty) {
		this.dirty = dirty;

	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void deserializeNBT(CompoundTag tag) {
		name = tag.getString("name");
		id = tag.getString("id");

		ListTag playersTag = tag.getList("players", Tag.TAG_COMPOUND);
		players.addAll(NBTUtilities.deserialize(playersTag, (playerTag) -> {
			return ((CompoundTag) playerTag).getString("id");
		}));

		getResearchManager().deserialize(tag.getCompound("research"), this);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putString("name", name);
		output.putString("id", id.toString());
		output.put("players", NBTUtilities.serialize(players, (player, tag) -> {
			tag.putString("id", player);
		}));
		output.put("research", getResearchManager().serialize());

		return output;
	}

	@Override
	public String toString() {
		return "Team [id=" + id + ", name=" + name + ", players=" + players + ", researchManager="
				+ getResearchManager() + ", productionManager=" + getProductionManager() + "]";
	}
}
