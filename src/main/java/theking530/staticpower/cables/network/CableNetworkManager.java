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

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
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
public class CableNetworkManager extends WorldSavedData {
	private static final Logger LOGGER = LogManager.getLogger(CableNetworkManager.class);
	private static final String PREFIX = StaticPower.MOD_ID + "_cable_network";

	private long currentCraftingId = 0;
	private long currentItemParcelId = 0;

	private final World World;
	private final HashMap<BlockPos, ServerCable> WorldCables;
	private final HashMap<Long, CableNetwork> Networks;
	private long CurrentNetworkId = 0;
	private boolean firstTick = false;

	public CableNetworkManager(String name, World world) {
		super(name);
		World = world;
		WorldCables = new HashMap<BlockPos, ServerCable>();
		Networks = new HashMap<Long, CableNetwork>();
	}

	public void tick() {
		if (!firstTick) {
			firstTick = true;
		}
		getNetworks().forEach(n -> n.tick());
		// getNetworks().forEach(n -> System.out.println("ID:" + n.getId() + " Cables: "
		// + n.getGraph().getCables().size()));

		List<Long> toRemove = new ArrayList<Long>();
		for (long id : Networks.keySet()) {
			if (Networks.get(id).getGraph().getCables().size() == 0) {
				toRemove.add(id);
			}
		}

		for (long id : toRemove) {
			Networks.remove(id);
		}
	}

	public void addCable(ServerCable cable) {
		if (!firstTick) {
			LOGGER.error(
					String.format("Attempted to add a cable before the world is fully loaded: %1$s.", cable.getPos()));
			return;
		}

		if (WorldCables.containsKey(cable.getPos())) {
			throw new RuntimeException(
					String.format("Attempted to add a cable where one already existed: %1$s.", cable.getPos()));
		}
		WorldCables.put(cable.getPos(), cable);
		LOGGER.debug(String.format("Cable added at position: %1$s.", cable.getPos()));

		markDirty();

		List<ServerCable> adjacents = getAdjacents(cable);
		if (adjacents.isEmpty()) {
			formNetworkAt(cable.getWorld(), cable.getPos());
		} else {
			mergeNetworksIntoOne(adjacents, cable.getWorld(), cable.getPos());
		}
	}

	public void refreshCable(ServerCable cable) {
		if (cable.Network == null) {
			throw new RuntimeException(String
					.format("Attempted to refresh a cable with a null network at position: %1$s.", cable.getPos()));
		}

		// Get the original network's cables.
		List<ServerCable> originalNetworkCables = new LinkedList<ServerCable>();
		originalNetworkCables.addAll(cable.Network.getGraph().getCables().values());

		// Get all the adjacents.
		List<ServerCable> adjacents = getAdjacents(cable);
		if (!adjacents.isEmpty()) {
			for (ServerCable adjacent : adjacents) {
				if (adjacent.Network == cable.Network) {
					adjacent.Network.updateGraph(World, adjacent.getPos());
					break;
				}
			}
		} else {
			cable.onNetworkLeft();
		}

		markDirty();

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
			throw new RuntimeException(
					String.format("Attempted to remove a cable where one did not already exist: %1$s.", pos));
		}
		// Get the cable.
		ServerCable cable = getCable(pos);

		// Remove it from the manager.
		WorldCables.remove(pos);

		// Mark the manager as dirty.
		markDirty();

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

	public void addNetwork(CableNetwork network) {
		if (Networks.containsKey(network.getId())) {
			throw new RuntimeException(
					String.format("Attempted to register a network with duplicate Id: %1$s.", network.getId()));
		}
		network.setWorld(World);
		Networks.put(network.getId(), network);
		LOGGER.debug(String.format("Created Network with Id: %1$s.", network.getId()));
		markDirty();
	}

	public void removeNetwork(long id) {
		if (!Networks.containsKey(id)) {
			throw new RuntimeException(
					String.format("Attempted to remove a network with Id: %1$s that had not been registered.", id));
		}

		Networks.remove(id);
		LOGGER.debug(String.format("Removed Network with Id: %1$s.", id));
		markDirty();
	}

	private void formNetworkAt(World world, BlockPos pos) {
		CableNetwork network = new CableNetwork(pos, CurrentNetworkId++);

		addNetwork(network);

		network.updateGraph(world, pos);
	}

	private void mergeNetworksIntoOne(List<ServerCable> candidates, World world, BlockPos pos) {
		if (candidates.isEmpty()) {
			throw new RuntimeException("Cannot merge networks: no candidates");
		}

		Set<CableNetwork> networkCandidates = new HashSet<>();
		for (ServerCable candidate : candidates) {
			networkCandidates.add(candidate.getNetwork());
		}

		Iterator<CableNetwork> networks = networkCandidates.iterator();

		CableNetwork mainNetwork = networks.next();

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

		mainNetwork.updateGraph(world, pos);

		mergedNetworks.forEach(n -> n.onJoinedWithOtherNetwork(mainNetwork));

		markDirty();
	}

	private void splitNetworks(ServerCable originCable) {
		// Get all adjacent cables.
		List<ServerCable> adjacents = getAdjacents(originCable);

		if (adjacents.size() > 0) {
			ServerCable firstAdjacentCable = adjacents.get(0);

			firstAdjacentCable.getNetwork().setOrigin(firstAdjacentCable.getPos());

			NetworkMapper result = firstAdjacentCable.getNetwork().updateGraph(firstAdjacentCable.getWorld(),
					firstAdjacentCable.getPos());

			// For sanity checking
			boolean removedCableFound = false;

			for (ServerCable removed : result.getRemovedCables()) {
				// Skip the removed cables if it is the origin one of the remove.
				if (removed.getPos().equals(originCable.getPos())) {
					removedCableFound = true;
					continue;
				}

				// If the removed does not have a network, create a network.
				if (removed.getNetwork() == null) {
					formNetworkAt(removed.getWorld(), removed.getPos());
				}
			}

			if (!removedCableFound) {
				throw new RuntimeException("Didn't find removed cable when splitting network");
			}

			markDirty();
		} else {
			LOGGER.debug("Removing empty network {}", originCable.getNetwork().getId());

			removeNetwork(originCable.getNetwork().getId());
		}
	}

	public List<ServerCable> getAdjacents(ServerCable cable) {
		List<ServerCable> wrappers = new ArrayList<ServerCable>();
		for (Direction dir : Direction.values()) {
			// Skip checking that side if that side is disabled.
			if (cable.isDisabledOnSide(dir)) {
				continue;
			}

			// Check if a cable exists on the provided side and it is enabled on that side
			// and of the same type.
			ServerCable adjacent = getCable(cable.getPos().offset(dir));

			if (adjacent == null) {
				continue;
			}

			if (adjacent.isDisabledOnSide(dir.getOpposite())) {
				continue;
			}

			if (adjacent.getNetwork() == null) {
				continue;
			}

			if (adjacent.shouldConnectTo(cable)) {
				wrappers.add(adjacent);
			}
		}

		return wrappers;
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
		markDirty();
	}

	public long getCurrentItemParcelId() {
		// Increment first just to be safe in case someone forgets to increment.
		incrementCurrentItemParcelId();
		return currentItemParcelId;
	}

	public void incrementCurrentItemParcelId() {
		currentItemParcelId++;
		markDirty();
	}

	public CableNetwork getNetworkById(long id) {
		if (Networks.containsKey(id)) {
			return Networks.get(id);
		}
		return null;
	}

	@Override
	public void read(CompoundNBT tag) {
		// Save the current parcel id.
		if (tag.contains("current_parcel_id")) {
			currentItemParcelId = tag.getLong("current_parcel_id");
		}

		// Load the current crafting id.
		if (tag.contains("crafting_id")) {
			currentCraftingId = tag.getLong("crafting_id");
		}

		ListNBT cables = tag.getList("cables", Constants.NBT.TAG_COMPOUND);
		for (INBT cableTag : cables) {
			CompoundNBT cableTagCompound = (CompoundNBT) cableTag;
			ServerCable cable = new ServerCable(World, cableTagCompound);
			WorldCables.put(cable.getPos(), cable);
		}

		ListNBT nets = tag.getList("networks", Constants.NBT.TAG_COMPOUND);
		for (INBT netTag : nets) {
			CompoundNBT netTagCompound = (CompoundNBT) netTag;
			CableNetwork network = CableNetwork.create(netTagCompound);
			network.setWorld(World);
			Networks.put(network.getId(), network);
		}

		// Get the current network Id.
		CurrentNetworkId = tag.getLong("current_network_id");

		LOGGER.debug(String.format("Deserialized: %1$d cables.", WorldCables.size()));
		LOGGER.debug(String.format("Deserialized: %1$d networks.", Networks.size()));
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		// Save the current crafting id.
		tag.putLong("crafting_id", currentCraftingId);
		tag.putLong("current_parcel_id", currentItemParcelId);

		ListNBT cables = new ListNBT();
		WorldCables.values().forEach(cable -> {
			CompoundNBT cableTag = new CompoundNBT();
			cable.writeToNbt(cableTag);
			cables.add(cableTag);
		});
		tag.put("cables", cables);

		ListNBT networks = new ListNBT();
		Networks.values().forEach(network -> {
			CompoundNBT networkTag = new CompoundNBT();
			network.writeToNbt(networkTag);
			networks.add(networkTag);
		});
		tag.put("networks", networks);

		// Serialize the current network id.
		tag.putLong("current_network_id", CurrentNetworkId);

		return tag;
	}

	public static CableNetworkManager get(World world) {
		return get((ServerWorld) world);
	}

	public static CableNetworkManager get(ServerWorld world) {
		String name = PREFIX + "_" + world.getDimensionKey().getLocation().getNamespace() + "_"
				+ world.getDimensionKey().getLocation().getPath();

		return world.getSavedData().getOrCreate(() -> new CableNetworkManager(name, world), name);
	}
}
