package theking530.staticpower.cables.network;

import java.util.HashMap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableNetworkGraph {
	private final CableNetwork Network;
	private HashMap<BlockPos, ServerCable> Cables;
	private HashMap<BlockPos, DestinationWrapper> Destinations;

	public CableNetworkGraph(CableNetwork network) {
		Cables = new HashMap<BlockPos, ServerCable>();
		Destinations = new HashMap<BlockPos, DestinationWrapper>();
		Network = network;
	}

	public NetworkMapper scan(World world, BlockPos scanStartPosition) {
		// Map the network.
		NetworkMapper mapper = new NetworkMapper(Cables.values());
		mapper.scanFromLocation(world, scanStartPosition);

		// Clear the old values.
		Cables.clear();
		Destinations.clear();

		// Cache the new values.
		mapper.getDiscoveredCables().forEach(cable -> Cables.put(cable.getPos(), cable));
		mapper.getDestinations().forEach(destTe -> Destinations.put(destTe.getPos(), destTe));

		// Raise the network joined and left events.
		mapper.getNewlyAddedCables().forEach(cable -> cable.onNetworkJoined(Network, true));
		mapper.getRemovedCables().forEach(cable -> cable.onNetworkLeft());

		return mapper;
	}

	public HashMap<BlockPos, ServerCable> getCables() {
		return Cables;
	}

	public HashMap<BlockPos, DestinationWrapper> getDestinations() {
		return Destinations;
	}
}
