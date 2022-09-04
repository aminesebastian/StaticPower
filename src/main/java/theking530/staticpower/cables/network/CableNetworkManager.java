package theking530.staticpower.cables.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import theking530.staticpower.StaticPower;

/**
 * Much thanks to raoulvdberge and RefinedPipes for offering a ton of wisdom on
 * how to start this cable network manager! Learned so much from reading his
 * code, this would not be possible without him.
 * 
 * https://github.com/refinedmods/refinedpipes
 * 
 * @author amine
 *
 */
public class CableNetworkManager extends SavedData {
	private static final Logger LOGGER = LogManager.getLogger(CableNetworkManager.class);
	private static final String PREFIX = StaticPower.MOD_ID + "_cable_network";

	/** Make these values static so they are incremented across all dimensions. */
	private static long currentCraftingId = 0;
	private static long currentPatternId = 0;
	private static long currentItemParcelId = 0;

	private final Level World;
	private final HashMap<BlockPos, ServerCable> WorldCables;
	private final HashMap<Long, CableNetwork> Networks;
	private final String Name;
	private long CurrentNetworkId = 0;
	private boolean firstTick = false;

	public CableNetworkManager(String name, Level world) {
		Name = name;
		World = world;
		WorldCables = new HashMap<BlockPos, ServerCable>();
		Networks = new HashMap<Long, CableNetwork>();
	}

	public void tick() {
		if (!firstTick) {
			firstTick = true;
		}

		// Tick all the networks.
		for (CableNetwork network : Networks.values()) {
			network.tick();
		}

		// Capture any networks that need to be removed.
		List<Long> toRemove = new ArrayList<Long>();
		for (long id : Networks.keySet()) {
			if (Networks.get(id).getGraph().getCables().size() == 0) {
				toRemove.add(id);
			}
		}

		// Then, remove them.
		for (long id : toRemove) {
			Networks.remove(id);
		}
	}

	public void addCable(ServerCable cable) {
		if (!firstTick) {
			LOGGER.error(String.format("Attempted to add a cable before the world is fully loaded: %1$s.", cable.getPos()));
			return;
		}

		if (WorldCables.containsKey(cable.getPos())) {
			throw new RuntimeException(String.format("Attempted to add a cable where one already existed: %1$s.", cable.getPos()));
		}
		WorldCables.put(cable.getPos(), cable);
		LOGGER.debug(String.format("Cable added at position: %1$s.", cable.getPos()));

		setDirty();

		List<ServerCable> adjacents = getAdjacents(cable);
		if (adjacents.isEmpty()) {
			formNetworkAt(cable.getWorld(), cable.getPos());
		} else {
			mergeNetworksIntoOne(adjacents, cable.getWorld(), cable.getPos());
		}
	}

	public void refreshCable(ServerCable cable) {
		if (cable.Network == null) {
			throw new RuntimeException(String.format("Attempted to refresh a cable with a null network at position: %1$s.", cable.getPos()));
		}

		// Get the original network's cables.
		List<ServerCable> originalNetworkCables = new LinkedList<ServerCable>();
		originalNetworkCables.addAll(cable.Network.getGraph().getCables().values());

		// Get all the adjacents.
		List<ServerCable> adjacents = getAdjacents(cable);
		if (!adjacents.isEmpty()) {
			for (ServerCable adjacent : adjacents) {
				if (adjacent.Network == cable.Network) {
					adjacent.Network.updateGraph(World, adjacent.getPos(), true);
					break;
				}
			}
		} else {
			cable.onNetworkLeft();
		}

		setDirty();

		// After the new graph has been updated, loop through the original network
		// cables and repair their network states as needed.
		for (ServerCable originalCable : originalNetworkCables) {
			// If the original cable does not have a network, attempt to give it one.
			if (originalCable.Network == null) {
				// Get it's adjacent.
				List<ServerCable> newAdjacents = getAdjacents(originalCable);
				// If there are no adjacents, create a new network. Otherwise, attempt to join
				// it.
				if (newAdjacents.isEmpty()) {
					formNetworkAt(cable.getWorld(), originalCable.getPos());
				} else {
					mergeNetworksIntoOne(newAdjacents, originalCable.getWorld(), originalCable.getPos());
				}

				if (originalCable.Network == null) {
					System.out.println("STILL NULL NETWORK");
				}
			}
		}
	}

	public void removeCable(BlockPos pos) {
		if (!WorldCables.containsKey(pos)) {
			throw new RuntimeException(String.format("Attempted to remove a cable where one did not already exist: %1$s.", pos));
		}
		// Get the cable.
		ServerCable cable = getCable(pos);

		// Remove it from the manager.
		WorldCables.remove(pos);

		// Mark the manager as dirty.
		setDirty();

		// Debug log the removal.
		LOGGER.debug(String.format("Cable removed at position: %1$s.", pos));

		// If the cable was part of a network, perform the split algorithm.
		if (cable.getNetwork() != null) {
			splitNetworks(cable);
		}
	}

	public @Nullable ServerCable getCable(BlockPos currentPosition) {
		return WorldCables.get(currentPosition);
	}

	public boolean isTrackingCable(BlockPos position) {
		return WorldCables.containsKey(position);
	}

	public Collection<CableNetwork> getNetworks() {
		return Networks.values();
	}

	public long getCurrentCraftingId() {
		return currentCraftingId;
	}

	public long getAndIncrementCurrentCraftingId() {
		// Increment first just to be safe in case someone forgets to increment.
		incrementCurrentCraftingId();
		return currentCraftingId;
	}

	public void incrementCurrentCraftingId() {
		currentCraftingId++;
		setDirty();
	}

	public long getCurrentPatternId() {
		return currentPatternId;
	}

	public long getAndIncrementCurrentPatternId() {
		// Increment first just to be safe in case someone forgets to increment.
		incrementCurrentPatternId();
		return currentPatternId;
	}

	public void incrementCurrentPatternId() {
		currentPatternId++;
		setDirty();
	}

	public long getCurrentItemParcelId() {
		// Increment first just to be safe in case someone forgets to increment.
		incrementCurrentItemParcelId();
		return currentItemParcelId;
	}

	public void incrementCurrentItemParcelId() {
		currentItemParcelId++;
		setDirty();
	}

	public String GetName() {
		return Name;
	}

	public CableNetwork getNetworkById(long id) {
		if (Networks.containsKey(id)) {
			return Networks.get(id);
		}
		return null;
	}

	public static CableNetworkManager load(CompoundTag tag, String name, Level world) {
		CableNetworkManager output = new CableNetworkManager(name, world);

		// Save the current parcel id.
		if (tag.contains("current_parcel_id")) {
			currentItemParcelId = tag.getLong("current_parcel_id");
		}

		// Load the current crafting id.
		if (tag.contains("crafting_id")) {
			currentCraftingId = tag.getLong("crafting_id");
		}

		// Load the pattern id.
		if (tag.contains("pattern_id")) {
			currentPatternId = tag.getLong("pattern_id");
		}

		ListTag cables = tag.getList("cables", Tag.TAG_COMPOUND);
		for (Tag cableTag : cables) {
			CompoundTag cableTagCompound = (CompoundTag) cableTag;
			ServerCable cable = new ServerCable(world, cableTagCompound);
			output.WorldCables.put(cable.getPos(), cable);
		}

		ListTag nets = tag.getList("networks", Tag.TAG_COMPOUND);
		for (Tag netTag : nets) {
			CompoundTag netTagCompound = (CompoundTag) netTag;
			CableNetwork network = CableNetwork.create(netTagCompound);
			network.setWorld(world);
			output.Networks.put(network.getId(), network);
		}

		// Get the current network Id.
		output.CurrentNetworkId = tag.getLong("current_network_id");

		LOGGER.debug(String.format("Deserialized: %1$d cables.", output.WorldCables.size()));
		LOGGER.debug(String.format("Deserialized: %1$d networks.", output.Networks.size()));
		return output;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		// Save the current crafting id.
		tag.putLong("crafting_id", currentCraftingId);
		tag.putLong("pattern_id", currentPatternId);
		tag.putLong("current_parcel_id", currentItemParcelId);

		ListTag cables = new ListTag();
		WorldCables.values().forEach(cable -> {
			CompoundTag cableTag = new CompoundTag();
			cable.writeToNbt(cableTag);
			cables.add(cableTag);
		});
		tag.put("cables", cables);

		ListTag networks = new ListTag();
		Networks.values().forEach(network -> {
			CompoundTag networkTag = new CompoundTag();
			network.writeToNbt(networkTag);
			networks.add(networkTag);
		});
		tag.put("networks", networks);

		// Serialize the current network id.
		tag.putLong("current_network_id", CurrentNetworkId);

		return tag;
	}

	public static CableNetworkManager get(Level world) {
		return get((ServerLevel) world);
	}

	public static CableNetworkManager get(ServerLevel world) {
		String name = PREFIX + "_" + world.dimension().location().getNamespace() + "_" + world.dimension().location().getPath();

		return world.getDataStorage().computeIfAbsent((tag) -> CableNetworkManager.load(tag, name, world), () -> new CableNetworkManager(name, world), name);
	}

	private CableNetwork formNetworkAt(Level world, BlockPos pos) {
		CableNetwork network = new CableNetwork(pos, CurrentNetworkId++);
		addNetwork(network);
		network.updateGraph(world, pos, true);
		return network;
	}

	private void mergeNetworksIntoOne(List<ServerCable> candidates, Level world, BlockPos pos) {
		if (candidates.isEmpty()) {
			throw new RuntimeException("Cannot merge networks: no candidates");
		}

		// Get all network (use a set to ensure uniqueness).
		Set<CableNetwork> networkCandidates = new HashSet<>();
		for (ServerCable candidate : candidates) {
			networkCandidates.add(candidate.getNetwork());
		}

		// Iterate through all the networks.
		Iterator<CableNetwork> networks = networkCandidates.iterator();
		CableNetwork mainNetwork = networks.next();

		// Keep track of the merged networks.
		Set<CableNetwork> mergedNetworks = new HashSet<>();
		while (networks.hasNext()) {
			// Remove all the other networks.
			CableNetwork otherNetwork = networks.next();

			// REVISIT THIS VALUE.
			boolean canMerge = true;

			if (canMerge) {
				mergedNetworks.add(otherNetwork);

				removeNetwork(otherNetwork.getId());
			}
		}

		mainNetwork.updateGraph(world, pos, true);

		mergedNetworks.forEach(n -> n.onJoinedWithOtherNetwork(mainNetwork));

		setDirty();
	}

	private void splitNetworks(ServerCable originCable) {
		// Get all adjacent cables. This accounts for diabled sides.
		List<ServerCable> adjacents = getAdjacents(originCable);

		if (adjacents.size() > 0) {
			// Because all adjacent cables belong to this network, the first one's network
			// is the same as this cable's network.
			// We update the network so that it's origin is now the adjacent cable (to make
			// sure the cable we're removing is never the adjacent).
			ServerCable firstAdjacentCable = adjacents.get(0);
			CableNetwork network = firstAdjacentCable.getNetwork();
			network.setOrigin(firstAdjacentCable.getPos());

			// Then, remap the network.
			NetworkMapper result = network.updateGraph(firstAdjacentCable.getWorld(), firstAdjacentCable.getPos(), true);

			// Keep track of any new networks.
			List<CableNetwork> newNetworks = new ArrayList<CableNetwork>();

			// For sanity checking
			boolean removedCableFound = false;

			// Iterate through all networks.
			for (ServerCable removed : result.getRemovedCables()) {
				// Skip a removed cable when it's the one that trigged this split, but update
				// the flag so we know we found it.
				// This is a simple sanity check.
				if (removed.getPos().equals(originCable.getPos())) {
					removedCableFound = true;
					continue;
				}

				// If the removed does not have a network, create a network.
				if (removed.getNetwork() == null) {
					newNetworks.add(formNetworkAt(removed.getWorld(), removed.getPos()));
				}
			}

			// If the cable that triggered this split was NOT removed, we got a problem!
			if (!removedCableFound) {
				throw new RuntimeException("Didn't find removed cable when splitting network");
			}

			// Let the source network know of any new networks that were split off of it.
			if (newNetworks.size() > 0) {
				network.onNetworksSplitOff(newNetworks);
			}

			// Mark this cable network as dirty.
			setDirty();
		} else {
			// If the adjacents list is empty, then this was the only cable in this network,
			// remove the whole network.
			LOGGER.debug("Removing empty network {}", originCable.getNetwork().getId());
			removeNetwork(originCable.getNetwork().getId());
		}
	}

	private void addNetwork(CableNetwork network) {
		if (Networks.containsKey(network.getId())) {
			throw new RuntimeException(String.format("Attempted to register a network with duplicate Id: %1$s.", network.getId()));
		}
		network.setWorld(World);
		Networks.put(network.getId(), network);
		LOGGER.debug(String.format("Created Network with Id: %1$s.", network.getId()));
		setDirty();
	}

	private void removeNetwork(long id) {
		if (!Networks.containsKey(id)) {
			throw new RuntimeException(String.format("Attempted to remove a network with Id: %1$s that had not been registered.", id));
		}

		Networks.remove(id);
		LOGGER.debug(String.format("Removed Network with Id: %1$s.", id));
		setDirty();
	}

	private List<ServerCable> getAdjacents(ServerCable cable) {
		List<ServerCable> wrappers = new ArrayList<ServerCable>();
		for (Direction dir : Direction.values()) {
			// Skip checking that side if that side is disabled.
			if (cable.isDisabledOnSide(dir)) {
				continue;
			}

			// Check if a cable exists on the provided side and it is enabled on that side
			// and of the same type.
			ServerCable adjacent = getCable(cable.getPos().relative(dir));

			if (adjacent == null) {
				continue;
			}

			if (adjacent.isDisabledOnSide(dir.getOpposite())) {
				continue;
			}

			if (adjacent.getNetwork() == null) {
				continue;
			}

			if (!adjacent.getNetwork().canAcceptCable(adjacent, cable)) {
				continue;
			}

			if (adjacent.shouldConnectTo(cable)) {
				wrappers.add(adjacent);
			}
		}

		return wrappers;
	}

}
