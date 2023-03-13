package theking530.staticcore.cablenetwork.manager;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import theking530.staticpower.StaticPower;

public class CableNetworkAccessor {
	private static final String PREFIX = StaticPower.MOD_ID + "_cable_network";
	private static final Map<String, ClientCableNetworkManager> CLIENT_MANAGERS = new HashMap<>();

	public static CableNetworkManager get(Level level) {
		return get((ServerLevel) level);
	}

	public static CableNetworkManager get(ServerLevel level) {
		String name = getSaveNameForWorld(level);
		return level.getDataStorage().computeIfAbsent((tag) -> CableNetworkManager.load(tag, name, level), () -> new CableNetworkManager(name, level), name);
	}

	public static ClientCableNetworkManager getClient(Level level) {
		String name = getSaveNameForWorld(level);
		if (CLIENT_MANAGERS.containsKey(name)) {
			return CLIENT_MANAGERS.get(name);
		}
		throw new RuntimeException(String.format("Attempted to get the client replicated network manager of name: %1$s but one has not yet been created.", name));
	}

	private static String getSaveNameForWorld(Level level) {
		return PREFIX + "_" + level.dimension().location().getNamespace() + "_" + level.dimension().location().getPath();
	}
}
