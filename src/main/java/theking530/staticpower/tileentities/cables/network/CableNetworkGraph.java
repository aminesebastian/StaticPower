package theking530.staticpower.tileentities.cables.network;

import java.util.HashMap;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;

public class CableNetworkGraph {
	private final CableNetwork Network;
	private HashMap<BlockPos, AbstractCableWrapper> Cables;
	private HashMap<BlockPos, TileEntity> Destinations;

	public CableNetworkGraph(CableNetwork network) {
		Cables = new HashMap<BlockPos, AbstractCableWrapper>();
		Destinations = new HashMap<BlockPos, TileEntity>();
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

	public HashMap<BlockPos, AbstractCableWrapper> getCables() {
		return Cables;
	}

	public HashMap<BlockPos, TileEntity> getDestinations() {
		return Destinations;
	}
}
