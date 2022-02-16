package theking530.staticpower.teams.research;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import theking530.staticpower.StaticPower;

public class ResearchManager extends SavedData {
	private static final Logger LOGGER = LogManager.getLogger(ResearchManager.class);
	private static final String PREFIX = StaticPower.MOD_ID + "_research_manager";

	private final Level world;
	private final String name;

	public ResearchManager(String name, Level world) {
		this.name = name;
		this.world = world;
	}

	public void tick() {
	}

	public static ResearchManager load(CompoundTag tag, String name, Level world) {
		ResearchManager output = new ResearchManager(name, world);
		return output;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		return tag;
	}

	public static ResearchManager get(Level world) {
		return get((ServerLevel) world);
	}

	public static ResearchManager get(ServerLevel world) {
		String name = PREFIX + "_" + world.dimension().location().getNamespace();
		return world.getDataStorage().computeIfAbsent((tag) -> ResearchManager.load(tag, name, world), () -> new ResearchManager(name, world), name);
	}
}
