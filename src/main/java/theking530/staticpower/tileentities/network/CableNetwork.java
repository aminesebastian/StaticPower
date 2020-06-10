/**
 * 
 */
package theking530.staticpower.tileentities.network;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;

/**
 * @author Amine Sebastian
 *
 */
public class CableNetwork {
	private static final Logger LOGGER = LogManager.getLogger(CableNetwork.class);
	public final HashMap<BlockPos, AbstractCableWrapper> NetworkMap;
	private final CableNetworkGraph Graph;
	private final long NetworkId;
	private BlockPos Origin;

	public CableNetwork(BlockPos origin, long id) {
		NetworkMap = new HashMap<BlockPos, AbstractCableWrapper>();
		NetworkId = id;
		Origin = origin;
		Graph = new CableNetworkGraph(this);
	}

	public void tick(ServerWorld world) {
		if (Graph.getCables().size() > 0) {
			System.out.println(Graph.getCables().size());
		}
	}

	public void updateGraph(World world, BlockPos startingPosition) {
		Graph.scan(world, startingPosition);
	}

	public BlockPos getOrigin() {
		return Origin;
	}

	public void addPipe(AbstractCableWrapper cable) {
		if (NetworkMap.containsKey(cable.getPos())) {
			throw new RuntimeException(String.format("Attempted to add a cable that already exists to network: %1$s.", NetworkId));
		}
		NetworkMap.put(cable.getPos(), cable);
	}

	public void removePipe(AbstractCableWrapper cable) {
		if (!NetworkMap.containsKey(cable.getPos())) {
			throw new RuntimeException(String.format("Attempted to remove a cable that was not part of network: %1$s.", NetworkId));
		}
		NetworkMap.remove(cable.getPos());
	}

	public void onJoinedWithOtherNetwork(CableNetwork mainNetwork) {

	}

	public long getId() {
		return NetworkId;
	}

	public boolean isEmpty() {
		return NetworkMap.isEmpty();
	}

	public static CableNetwork create(CompoundNBT tag) {
		CableNetwork network = new CableNetwork(BlockPos.fromLong(tag.getLong("origin")), tag.getLong("network_id"));
		LOGGER.debug("Deserialized item network {}", network.getId());
		return network;
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.putLong("network_id", NetworkId);
		tag.putLong("origin", Origin.toLong());

		return tag;
	}
}
