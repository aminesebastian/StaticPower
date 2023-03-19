package theking530.staticcore.cablenetwork;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import theking530.staticcore.StaticCore;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;

public class CableNetworkGraph {
	protected static final Logger LOGGER = LogManager.getLogger(CableNetworkGraph.class);
	private final CableNetwork owningNetwork;
	private HashMap<BlockPos, Cable> cables;
	private HashMap<BlockPos, DestinationWrapper> destinations;

	public CableNetworkGraph(CableNetwork network) {
		cables = new HashMap<BlockPos, Cable>();
		destinations = new HashMap<BlockPos, DestinationWrapper>();
		owningNetwork = network;
	}

	public NetworkMapper scan(Level world, BlockPos scanStartPosition) {
		// Map the network.
		NetworkMapper mapper = new NetworkMapper(cables.values());

		try {
			// If the cable is bad, return early.
			if (!CableNetworkAccessor.get(world).isTrackingCable(scanStartPosition)) {
				StaticCore.LOGGER.error(String.format("Encountered a null starting cable at position: %1$s when attempting to scan the network.", scanStartPosition));
				return mapper;
			}

			// Map the network.
			mapper.scanFromLocation(world, scanStartPosition);

			// Raise the removed even BEFORE we clear our current cache.
			mapper.getRemovedCables().forEach(cable -> {
				// A cable may have been removed and added to a different network. We don't want
				// to invalidate that cable's new network, so only remove IF the cable was
				// removed FROM this network directly.
				if (cable.network == owningNetwork) {
					cable.onNetworkLeft(owningNetwork);
				}
			});

			// Clear the old values.
			cables.clear();
			destinations.clear();

			// Cache the new values. We have to do two loops for the discovered cables as
			// cables require access to all the cables.
			destinations = mapper.getDestinations();
			mapper.getDiscoveredCables().forEach(cable -> {
				cables.put(cable.getPos(), cable);
			});
			mapper.getDiscoveredCables().forEach(cable -> {
				cable.onNetworkUpdated(owningNetwork);
			});

			// Raise the network joined event.
			mapper.getNewlyAddedCables().forEach(cable -> cable.onNetworkJoined(owningNetwork));
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to scan a network starting at cable position: %1$s.", scanStartPosition), e);
		}

		return mapper;
	}

	public HashMap<BlockPos, Cable> getCables() {
		return cables;
	}

	public HashMap<BlockPos, DestinationWrapper> getDestinations() {
		return destinations;
	}
}
