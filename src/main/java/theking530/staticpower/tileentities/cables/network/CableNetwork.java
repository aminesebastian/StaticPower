/**
 * 
 */
package theking530.staticpower.tileentities.cables.network;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.tileentities.cables.network.factories.modules.CableNetworkModuleRegistry;
import theking530.staticpower.tileentities.cables.network.modules.AbstractCableNetworkModule;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.tileentities.cables.network.pathfinding.PathCache;

/**
 * @author Amine Sebastian
 *
 */
public class CableNetwork {
	private static final Logger LOGGER = LogManager.getLogger(CableNetwork.class);
	private final CableNetworkGraph Graph;
	private final PathCache PathCache;
	private final long NetworkId;
	private BlockPos Origin;
	private boolean InitialScanComplete;
	private World World;
	private HashMap<ResourceLocation, AbstractCableNetworkModule> Modules;

	public CableNetwork(BlockPos origin, long id) {
		NetworkId = id;
		Origin = origin;
		PathCache = new PathCache(this);
		Graph = new CableNetworkGraph(this);
		Modules = new HashMap<ResourceLocation, AbstractCableNetworkModule>();
	}

	public void tick() {
		// Perform a single scan when the world/network first loads.
		if (!InitialScanComplete) {
			InitialScanComplete = true;
			updateGraph(World, Origin);
		}

		// Tick all the attachments.
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.tick(World);
		}

		if (Graph.getCables().size() > 0) {
			Graph.getCables().forEach(cable -> cable.tick());
			// System.out.println("Network with ID: " + NetworkId + " of Size: " +
			// Graph.getCables().size());
		}
	}

	public boolean hasModule(ResourceLocation type) {
		return Modules.containsKey(type);
	}

	public void addModule(AbstractCableNetworkModule module) {
		// If we have already registered an module of this type, throw an error.
		if (hasModule(module.getType())) {
			throw new RuntimeException(String.format("Attempted to add a module of a type: %1$s that was already added.", module.getType()));
		}

		// Add the module and let it know that it was added to a network.
		Modules.put(module.getType(), module);
		module.onAddedToNetwork(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractCableNetworkModule> T getModule(ResourceLocation type) {
		// If we have already registered an module of this type, throw an error.
		if (!hasModule(type)) {
			throw new RuntimeException(String.format("Attempted to get a module of a type: %1$s that does not exist on this network.", type));
		}
		return (T) Modules.get(type);
	}

	public NetworkMapper updateGraph(World world, BlockPos startingPosition) {
		// Invalidate the path cache.
		PathCache.invalidateCache();

		// Map the graph.
		NetworkMapper output = Graph.scan(world, startingPosition);

		// Let all the modules know the graph was updated.
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.onNetworkGraphUpdated(output);
		}

		// Return the mapping result.
		return output;
	}

	public BlockPos getOrigin() {
		return Origin;
	}

	public void setOrigin(BlockPos pos) {
		Origin = pos;
	}

	public CableNetworkGraph getGraph() {
		return Graph;
	}

	public PathCache getPathCache() {
		return PathCache;
	}

	public World getWorld() {
		return World;
	}

	/**
	 * Gets the shortest path to a tile entity that matches the passed predicate
	 * test.
	 * 
	 * @param startingPos  The cable position to start at.
	 * @param validityTest The predicate to use to see if a tile entity is valid.
	 * @return The path to the closest tile entity of the provided type.
	 */
	public @Nullable Path getPathToCloestsTileEntityPassingTest(BlockPos startingPos, BiPredicate<TileEntity, Direction> validityTest) {
		int shortestPathLength = Integer.MAX_VALUE;
		Path shortestPath = null;
		for (TileEntity te : Graph.getDestinations()) {
			// Get the path.
			List<Path> paths = PathCache.getPaths(startingPos, te.getPos());

			// If there are no paths (HOW??), continue.
			if (paths == null) {
				continue;
			}

			for (Path path : paths) {
				// Check if the tile entity is valid.
				if (!validityTest.test(te, path.getDestinationDirection())) {
					continue;
				}

				// Check if this is the shortest path.
				if (path.getLength() < shortestPathLength) {
					shortestPathLength = path.getLength();
					shortestPath = path;
				}
			}
		}
		return shortestPath;
	}

	public void onJoinedWithOtherNetwork(CableNetwork mainNetwork) {
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.onAddedToNetwork(mainNetwork);
		}
	}

	public long getId() {
		return NetworkId;
	}

	public boolean isEmpty() {
		return Graph.getCables().isEmpty();
	}

	public void setWorld(World world) {
		World = world;
	}

	public static CableNetwork create(CompoundNBT tag) {
		// Create the network.
		CableNetwork network = new CableNetwork(BlockPos.fromLong(tag.getLong("origin")), tag.getLong("network_id"));

		// Deserialize the modules.
		ListNBT modules = tag.getList("modules", Constants.NBT.TAG_COMPOUND);
		for (INBT moduleTag : modules) {
			// Get the module compound.
			CompoundNBT moduleTagCompound = (CompoundNBT) moduleTag;

			// Get the module type.
			ResourceLocation moduleType = new ResourceLocation(moduleTagCompound.getString("type"));

			// Create the module.
			AbstractCableNetworkModule moduleInstance = CableNetworkModuleRegistry.get().create(moduleType, moduleTagCompound);

			// Add the attachment to the attachments list.
			network.addModule(moduleInstance);
		}

		LOGGER.debug("Deserialized item network {}", network.getId());
		return network;
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		// Put the network ID and the origin.
		tag.putLong("network_id", NetworkId);
		tag.putLong("origin", Origin.toLong());

		// Serialize the attachments.
		ListNBT modules = new ListNBT();
		Modules.values().forEach(module -> {
			CompoundNBT moduleTag = new CompoundNBT();
			moduleTag.putString("type", module.getType().toString());
			module.writeToNbt(moduleTag);
			modules.add(moduleTag);
		});

		// Add the attachments.
		tag.put("modules", modules);

		return tag;
	}
}
