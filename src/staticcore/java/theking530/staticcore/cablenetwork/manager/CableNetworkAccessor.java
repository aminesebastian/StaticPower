package theking530.staticcore.cablenetwork.manager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;

public class CableNetworkAccessor {
	private static final String PREFIX = StaticCore.MOD_ID + "_cable_network";

	public static CableNetworkManager get(Level level) {
		return get((ServerLevel) level);
	}

	public static CableNetworkManager get(ServerLevel level) {
		String name = getSaveNameForWorld(level);
		return level.getDataStorage().computeIfAbsent((tag) -> CableNetworkManager.load(tag, name, level),
				() -> new CableNetworkManager(name, level), name);
	}

	private static String getSaveNameForWorld(Level level) {
		return PREFIX + "_" + level.dimension().location().getNamespace() + "_"
				+ level.dimension().location().getPath();
	}
}
