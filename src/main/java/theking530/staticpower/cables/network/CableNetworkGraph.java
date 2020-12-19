package theking530.staticpower.cables.network;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

public class CableNetworkGraph {
	protected static final Logger LOGGER = LogManager.getLogger(CableNetworkGraph.class);
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

		try {
			// If the cable is bad, return early.
			if (!CableNetworkManager.get(world).isTrackingCable(scanStartPosition)) {
				StaticPower.LOGGER.error(String.format("Encountered a null starting cable at position: %1$s when attempting to scan the network.", scanStartPosition));
				return mapper;
			}

			// Map the network.
			mapper.scanFromLocation(world, scanStartPosition);

			// Clear the old values.
			Cables.clear();
			Destinations.clear();

			// Cache the new values.
			mapper.getDiscoveredCables().forEach(cable -> Cables.put(cable.getPos(), cable));
			Destinations = mapper.getDestinations();

			// Raise the network joined and left events.
			mapper.getNewlyAddedCables().forEach(cable -> cable.onNetworkJoined(Network, true));
			mapper.getRemovedCables().forEach(cable -> {
				// A cable may have been removed and added to a different network. We don't want
				// to invalidate that cable's new network, so only remove IF the cable was
				// removed FROM this network directly.
				if (cable.Network == Network) {
					cable.onNetworkLeft();
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to scan a network starting at cable position: %1$s.", scanStartPosition), e);
		}

		return mapper;
	}

	public HashMap<BlockPos, ServerCable> getCables() {
		return Cables;
	}

	public HashMap<BlockPos, DestinationWrapper> getDestinations() {
		return Destinations;
	}
}
