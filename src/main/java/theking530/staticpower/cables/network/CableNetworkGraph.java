package theking530.staticpower.cables.network;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticpower.StaticPower;

public class CableNetworkGraph {
	protected static final Logger LOGGER = LogManager.getLogger(CableNetworkGraph.class);
	private final CableNetwork owningNetwork;
	private HashMap<BlockPos, ServerCable> cables;
	private HashMap<BlockPos, DestinationWrapper> destinations;

	public CableNetworkGraph(CableNetwork network) {
		cables = new HashMap<BlockPos, ServerCable>();
		destinations = new HashMap<BlockPos, DestinationWrapper>();
		owningNetwork = network;
	}

	public NetworkMapper scan(Level world, BlockPos scanStartPosition) {
		// Map the network.
		NetworkMapper mapper = new NetworkMapper(cables.values());

		try {
			// If the cable is bad, return early.
			if (!CableNetworkManager.get(world).isTrackingCable(scanStartPosition)) {
				StaticPower.LOGGER.error(String.format("Encountered a null starting cable at position: %1$s when attempting to scan the network.", scanStartPosition));
				return mapper;
			}

			// Map the network.
			mapper.scanFromLocation(world, scanStartPosition);

			// Raise the removed even BEFORE we clear our current cache.
			mapper.getRemovedCables().forEach(cable -> {
				// A cable may have been removed and added to a different network. We don't want
				// to invalidate that cable's new network, so only remove IF the cable was
				// removed FROM this network directly.
				if (cable.Network == owningNetwork) {
					cable.onNetworkLeft(owningNetwork);
				}
			});

			// Clear the old values.
			cables.clear();
			destinations.clear();
			
			// Cache the new values.
			mapper.getDiscoveredCables().forEach(cable -> cables.put(cable.getPos(), cable));
			destinations = mapper.getDestinations();

			// Raise the network joined event.
			mapper.getNewlyAddedCables().forEach(cable -> cable.onNetworkJoined(owningNetwork, true));
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to scan a network starting at cable position: %1$s.", scanStartPosition), e);
		}

		return mapper;
	}

	public HashMap<BlockPos, ServerCable> getCables() {
		return cables;
	}

	public HashMap<BlockPos, DestinationWrapper> getDestinations() {
		return destinations;
	}
}
