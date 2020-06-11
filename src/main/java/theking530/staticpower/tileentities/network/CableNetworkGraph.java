package theking530.staticpower.tileentities.network;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;

public class CableNetworkGraph {
	private final CableNetwork Network;
	private Set<AbstractCableWrapper> Cables;
	private Set<TileEntity> Destinations;

	public CableNetworkGraph(CableNetwork network) {
		Cables = new HashSet<AbstractCableWrapper>();
		Destinations = new HashSet<TileEntity>();
		Network = network;
	}

	public NetworkMapper scan(World world, BlockPos scanStartPosition) {
		Destinations.clear();
		NetworkMapper mapper = new NetworkMapper(Cables);
		mapper.scanFromLocation(world, scanStartPosition);

		Cables = mapper.getDiscoveredCables();
		mapper.getNewlyAddedCables().forEach(cable -> cable.onNetworkJoined(Network));
		mapper.getRemovedPipes().forEach(cable -> cable.onNetworkLeft());
		Destinations.addAll(mapper.getDestinations());
		
		return mapper;
	}

	public Set<AbstractCableWrapper> getCables() {
		return Cables;
	}

	public Set<TileEntity> getDestinations() {
		return Destinations;
	}
}
