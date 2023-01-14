package theking530.staticcore.cablenetwork.manager;

import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.Cable;

public class ClientCableNetworkManager implements ICableNetworkManager {
	private final Level level;
	private final HashMap<BlockPos, Cable> worldCables;
	private final HashMap<Long, CableNetwork> worldNetworks;
	private final String name;

	public ClientCableNetworkManager(String name, Level level) {
		this.name = name;
		this.level = level;
		worldCables = new HashMap<BlockPos, Cable>();
		worldNetworks = new HashMap<Long, CableNetwork>();
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Cable getCable(BlockPos currentPosition) {
		return worldCables.getOrDefault(currentPosition, null);
	}

	@Override
	public boolean isTrackingCable(BlockPos position) {
		return worldCables.containsKey(position);
	}

	@Override
	public CableNetwork getNetworkById(long id) {
		return worldNetworks.getOrDefault(id, null);
	}
}
