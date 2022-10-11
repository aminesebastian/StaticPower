/**
 * 
 */
package theking530.staticcore.cablenetwork;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.cablenetwork.pathfinding.PathCache;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticpower.StaticPowerRegistries;

/**
 * @author Amine Sebastian
 *
 */
public class CableNetwork {
	private static final Logger LOGGER = LogManager.getLogger(CableNetwork.class);
	private final CableNetworkGraph graph;
	private final PathCache pathCache;
	private final long networkId;
	private BlockPos origin;
	private boolean initialScanCompleted;
	private Level level;
	private HashMap<CableNetworkModuleType, CableNetworkModule> modules;
	private boolean networkUpdatesDisabled;

	public CableNetwork(BlockPos origin, long id) {
		networkId = id;
		this.origin = origin;
		pathCache = new PathCache(this);
		graph = new CableNetworkGraph(this);
		modules = new HashMap<CableNetworkModuleType, CableNetworkModule>();
		networkUpdatesDisabled = false;
	}

	public void preWorldTick() {
		// Tick all the modules.
		for (CableNetworkModule module : modules.values()) {
			try {
				module.preWorldTick(level);
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to pre-tick a graph module of type: %1$s.", module.getType().toString()), e);
			}
		}
	}

	public void tick() {
		// Perform a single scan when the world/network first loads.
		if (!initialScanCompleted) {
			initialScanCompleted = true;
			updateGraph(level, origin, true);
		}

		// Tick all the modules.
		for (CableNetworkModule module : modules.values()) {
			try {
				module.tick(level);
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to tick a graph module of type: %1$s.", module.getType().toString()), e);
			}
		}
	}

	public boolean hasModule(CableNetworkModuleType type) {
		return modules.containsKey(type);
	}

	public void addModule(CableNetworkModule module) {
		// If we have already registered an module of this type, throw an error.
		if (hasModule(module.getType())) {
			throw new RuntimeException(String.format("Attempted to add a module of a type: %1$s that was already added.", module.getType()));
		}

		// Add the module and let it know that it was added to a network.
		modules.put(module.getType(), module);
		module.onAddedToNetwork(this);
	}

	@SuppressWarnings("unchecked")
	public <T extends CableNetworkModule> T getModule(CableNetworkModuleType type) {
		// If we have already registered an module of this type, throw an error.
		if (!hasModule(type)) {
			throw new RuntimeException(String.format("Attempted to get a module of a type: %1$s that does not exist on this network.", type));
		}
		return (T) modules.get(type);
	}

	public List<CableNetworkModule> getModules() {
		return modules.values().stream().collect(Collectors.toList());
	}

	public @Nullable NetworkMapper updateGraph(Level world, BlockPos startingPosition, boolean force) {
		if (networkUpdatesDisabled) {
			return null;
		}

		// Invalidate the path cache.
		pathCache.invalidateCache();

		// Map the graph.
		NetworkMapper output = graph.scan(world, startingPosition);

		// Let all the modules know the graph was updated.
		for (CableNetworkModule module : modules.values()) {
			try {
				module.onNetworkGraphUpdated(output, startingPosition);
			} catch (Exception e) {
				throw new RuntimeException(
						String.format("An error occured when attempting to update a network module of type: %1$s with a new graph.", module.getType().toString()), e);
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
			for (CableNetworkModule module : modules.values()) {
				module.onNetworkUpdatesDisabled();
			}
		}
	}

	public void enableNetworkUpdates() {
		if (networkUpdatesDisabled) {
			networkUpdatesDisabled = false;
			for (CableNetworkModule module : modules.values()) {
				module.onNetworkUpdatesEnabled();
			}
		}
	}

	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {
		// Let all the modules know the graph was updated.
		for (CableNetworkModule module : modules.values()) {
			try {
				module.onNetworksSplitOff(newNetworks);
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to let a network module of type: %1$s know of new networks that resulted from a split.",
						module.getType().toString()), e);
			}
		}
	}

	public List<Component> getReaderOutput(BlockPos fromPos) {
		// Allocate the output list.
		List<Component> output = new LinkedList<Component>();
		output.add(Component.literal(""));
		output.add(Component.literal("NetworkID: ").append(String.format("%1$s%2$d with %3$d cables", ChatFormatting.GRAY.toString(), networkId, graph.getCables().size())));

		// Capture the output contents of the modules.
		for (CableNetworkModule module : modules.values()) {
			module.getReaderOutput(output, fromPos);
		}

		return output;
	}

	public BlockPos getOrigin() {
		return origin;
	}

	public void setOrigin(BlockPos pos) {
		origin = pos;
	}

	public CableNetworkGraph getGraph() {
		return graph;
	}

	public PathCache getPathCache() {
		return pathCache;
	}

	public Level getWorld() {
		return level;
	}

	public void onJoinedWithOtherNetwork(CableNetwork mainNetwork) {
		for (CableNetworkModule module : modules.values()) {
			module.onAddedToNetwork(mainNetwork);
		}
	}

	public long getId() {
		return networkId;
	}

	public boolean isEmpty() {
		return graph.getCables().isEmpty();
	}

	public boolean canAcceptCable(ServerCable currentNetworkCable, ServerCable newCable) {
		for (CableNetworkModule module : modules.values()) {
			if (!module.canAcceptCable(currentNetworkCable, newCable)) {
				return false;
			}
		}
		return true;
	}

	public void setWorld(Level world) {
		level = world;
	}

	public static CableNetwork create(CompoundTag tag) {
		// Create the network.
		CableNetwork network = new CableNetwork(BlockPos.of(tag.getLong("origin")), tag.getLong("network_id"));

		// Deserialize the modules.
		ListTag moduleTagList = tag.getList("modules", Tag.TAG_COMPOUND);
		for (Tag moduleTag : moduleTagList) {
			// Get the module compound.
			CompoundTag moduleTagCompound = (CompoundTag) moduleTag;

			// Get the module type.
			ResourceLocation moduleType = new ResourceLocation(moduleTagCompound.getString("type"));

			// Create the module.
			CableNetworkModule moduleInstance = StaticPowerRegistries.CableModuleRegsitry().getValue(moduleType).create();

			// Add the attachment to the attachments list.
			network.addModule(moduleInstance);
		}

		LOGGER.debug("Deserialized item network {}", network.getId());
		return network;
	}

	public CompoundTag writeToNbt(CompoundTag tag) {
		// Put the network ID and the origin.
		tag.putLong("network_id", networkId);
		tag.putLong("origin", origin.asLong());

		// Serialize the attachments.
		ListTag moduleTagList = new ListTag();
		modules.values().forEach(module -> {
			CompoundTag moduleTag = new CompoundTag();
			moduleTag.putString("type", StaticPowerRegistries.CableModuleRegsitry().getKey(module.getType()).toString());
			module.writeToNbt(moduleTag);
			moduleTagList.add(moduleTag);
		});

		// Add the attachments.
		tag.put("modules", moduleTagList);

		return tag;
	}

	public void recieveCrossNetworkUpdate(CableNetwork sendingNetwork, Set<CableNetwork> previousNetworks) {
		for (CableNetworkModule module : modules.values()) {
			module.recieveCrossNetworkUpdate(sendingNetwork, previousNetworks);
		}
	}
}
