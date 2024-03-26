package theking530.staticcore.blockentity.components.multiblock.manager;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;

public class MultiblockManager {
	private static final String PREFIX = StaticCore.MOD_ID + "_multiblock_manager";
	private static final Map<Level, ClientMultiblockManager> CLIENT_MANAGERS = new HashMap<>();

	public static IMultiblockManager get(Level level) {
		if (level.isClientSide()) {
			return CLIENT_MANAGERS.computeIfAbsent(level, (key) -> new ClientMultiblockManager());
		} else {
			ServerLevel serverLevel = (ServerLevel) level;
			String name = getSaveNameForWorld(level);
			return serverLevel.getDataStorage().computeIfAbsent(
					(tag) -> ServerMultiblockManager.load(tag, name, serverLevel),
					() -> new ServerMultiblockManager(serverLevel), name);
		}
	}

	public static ServerMultiblockManager get(ServerLevel level) {
		String name = getSaveNameForWorld(level);
		return level.getDataStorage().computeIfAbsent((tag) -> ServerMultiblockManager.load(tag, name, level),
				() -> new ServerMultiblockManager(level), name);
	}

	private static String getSaveNameForWorld(Level level) {
		return PREFIX + "_" + level.dimension().location().getNamespace() + "_"
				+ level.dimension().location().getPath();
	}
}
