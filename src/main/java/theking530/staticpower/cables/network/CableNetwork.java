/**
 * 
 */
package theking530.staticpower.cables.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.cables.network.pathfinding.PathCache;

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
	private Level World;
	private HashMap<ResourceLocation, AbstractCableNetworkModule> Modules;
	private boolean networkUpdatesDisabled;

	public CableNetwork(BlockPos origin, long id) {
		NetworkId = id;
		Origin = origin;
		PathCache = new PathCache(this);
		Graph = new CableNetworkGraph(this);
		Modules = new HashMap<ResourceLocation, AbstractCableNetworkModule>();
		networkUpdatesDisabled = false;
	}

	public void tick() {
		// Perform a single scan when the world/network first loads.
		if (!InitialScanComplete) {
			InitialScanComplete = true;
			updateGraph(World, Origin);
		}

		// Tick all the modules.

		for (AbstractCableNetworkModule module : Modules.values()) {
			try {
				module.tick(World);
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to tick a graph module of type: %1$s.", module.getType().toString()), e);
			}
		}

		// Tick all the cables.
		if (Graph.getCables().size() > 0) {
			for (ServerCable cable : Graph.getCables().values()) {
				try {
					cable.tick();
				} catch (Exception e) {
					throw new RuntimeException(String.format("An error occured when attempting to tick a cable with data tag: %1$s.", cable.getCompleteDataTag().toString()), e);
				}
			}
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

	public List<AbstractCableNetworkModule> getModules() {
		return Modules.values().stream().collect(Collectors.toList());
	}

	public @Nullable NetworkMapper updateGraph(Level world, BlockPos startingPosition) {
		if (networkUpdatesDisabled) {
			return null;
		}

		// Invalidate the path cache.
		PathCache.invalidateCache();

		// Map the graph.
		NetworkMapper output = Graph.scan(world, startingPosition);

		// Let all the modules know the graph was updated.
		for (AbstractCableNetworkModule module : Modules.values()) {
			try {
				module.onNetworkGraphUpdated(output, startingPosition);
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to update a network module of type: %1$s with a new graph.", module.getType().toString()), e);
			}
		}

		// Return the mapping result.
		return output;
	}

	public boolean areNetworkUpdatesDisabled() {
		return networkUpdatesDisabled;
	}

	public void disableNetworkUpdates() {
		if (!networkUpdatesDisabled) {
			networkUpdatesDisabled = true;
			for (AbstractCableNetworkModule module : Modules.values()) {
				module.onNetworkUpdatesDisabled();
			}
		}
	}

	public void enableNetworkUpdates() {
		if (networkUpdatesDisabled) {
			networkUpdatesDisabled = false;
			for (AbstractCableNetworkModule module : Modules.values()) {
				module.onNetworkUpdatesEnabled();
			}
		}
	}

	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {
		// Let all the modules know the graph was updated.
		for (AbstractCableNetworkModule module : Modules.values()) {
			try {
				module.onNetworksSplitOff(newNetworks);
			} catch (Exception e) {
				throw new RuntimeException(
						String.format("An error occured when attempting to let a network module of type: %1$s know of new networks that resulted from a split.", module.getType().toString()),
						e);
			}
		}
	}

	public List<Component> getReaderOutput() {
		// Allocate the output list.
		List<Component> output = new LinkedList<Component>();
		output.add(new TextComponent(""));
		output.add(new TextComponent("NetworkID: ").append(String.format("%1$s%2$d with %3$d cables", ChatFormatting.GRAY.toString(), NetworkId, Graph.getCables().size())));

		// Capture the output contents of the modules.
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.getReaderOutput(output);
		}

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

	public Level getWorld() {
		return World;
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

	public void setWorld(Level world) {
		World = world;
	}

	public static CableNetwork create(CompoundTag tag) {
		// Create the network.
		CableNetwork network = new CableNetwork(BlockPos.of(tag.getLong("origin")), tag.getLong("network_id"));

		// Deserialize the modules.
		ListTag modules = tag.getList("modules", Constants.NBT.TAG_COMPOUND);
		for (Tag moduleTag : modules) {
			// Get the module compound.
			CompoundTag moduleTagCompound = (CompoundTag) moduleTag;

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

	public CompoundTag writeToNbt(CompoundTag tag) {
		// Put the network ID and the origin.
		tag.putLong("network_id", NetworkId);
		tag.putLong("origin", Origin.asLong());

		// Serialize the attachments.
		ListTag modules = new ListTag();
		Modules.values().forEach(module -> {
			CompoundTag moduleTag = new CompoundTag();
			moduleTag.putString("type", module.getType().toString());
			module.writeToNbt(moduleTag);
			modules.add(moduleTag);
		});

		// Add the attachments.
		tag.put("modules", modules);

		return tag;
	}

	public void recieveCrossNetworkUpdate(CableNetwork sendingNetwork, Set<CableNetwork> previousNetworks) {
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.recieveCrossNetworkUpdate(sendingNetwork, previousNetworks);
		}
	}
}
