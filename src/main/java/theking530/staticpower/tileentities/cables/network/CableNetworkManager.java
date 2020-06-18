package theking530.staticpower.tileentities.cables.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.cables.network.factories.cables.CableWrapperRegistry;
import theking530.staticpower.utilities.Reference;

public class CableNetworkManager extends WorldSavedData {
	private static final Logger LOGGER = LogManager.getLogger(CableNetworkManager.class);
	private static final String PREFIX = Reference.MOD_ID + "_cable_network";
	private final World World;
	private final HashMap<BlockPos, AbstractCableWrapper> WorldCables;
	private final HashMap<Long, CableNetwork> Networks;
	private long CurrentNetworkId = 0;

	public CableNetworkManager(String name, World world) {
		super(name);
		World = world;
		WorldCables = new HashMap<BlockPos, AbstractCableWrapper>();
		Networks = new HashMap<Long, CableNetwork>();
	}

	public void tick() {
		getNetworks().forEach(n -> n.tick());
	}

	public void addCable(AbstractCableWrapper cable) {
		if (WorldCables.containsKey(cable.getPos())) {
			throw new RuntimeException(String.format("Attempted to add a cable where one already existed: %1$s.", cable.getPos()));
		}
		WorldCables.put(cable.getPos(), cable);
		LOGGER.debug(String.format("Cable added at position: %1$s.", cable.getPos()));

		markDirty();

		List<AbstractCableWrapper> adjacents = getAdjacents(cable);
		if (adjacents.isEmpty()) {
			formNetworkAt(cable.getWorld(), cable.getPos());
		} else {
			mergeNetworksIntoOne(adjacents, cable.getWorld(), cable.getPos());
		}
	}

	public void removeCable(BlockPos pos) {
		if (!WorldCables.containsKey(pos)) {
			throw new RuntimeException(String.format("Attempted to remove a cable where one did not already exist: %1$s.", pos));
		}
		// Get the cable.
		AbstractCableWrapper cable = getCable(pos);

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

	public @Nullable AbstractCableWrapper getCable(BlockPos currentPosition) {
		return WorldCables.get(currentPosition);
	}

	public boolean isTrackingCable(BlockPos position) {
		return WorldCables.containsKey(position);
	}

	public void addNetwork(CableNetwork network) {
		if (Networks.containsKey(network.getId())) {
			throw new RuntimeException(String.format("Attempted to register a network with duplicate Id: %1$s.", network.getId()));
		}
		network.setWorld(World);
		Networks.put(network.getId(), network);
		LOGGER.debug(String.format("Created Network with Id: %1$s.", network.getId()));
		markDirty();
	}

	public void removeNetwork(long id) {
		if (!Networks.containsKey(id)) {
			throw new RuntimeException(String.format("Attempted to remove a network with Id: %1$s that had not been registered.", id));
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

	private void mergeNetworksIntoOne(List<AbstractCableWrapper> candidates, World world, BlockPos pos) {
		if (candidates.isEmpty()) {
			throw new RuntimeException("Cannot merge networks: no candidates");
		}

		Set<CableNetwork> networkCandidates = new HashSet<>();

		for (AbstractCableWrapper candidate : candidates) {
			if (candidate.getNetwork() == null) {
				throw new RuntimeException("Pipe network is null!");
			}

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

	private void splitNetworks(AbstractCableWrapper originCable) {
		// Get all adjacent cables.
		List<AbstractCableWrapper> adjacents = getAdjacents(originCable);

		if (adjacents.size() > 0) {
			// We can assume all adjacent pipes (with the same network type) share the same
			// network with the removed pipe.
			// That means it doesn't matter which pipe network we use for splitting, we'll
			// take the first found one.
			AbstractCableWrapper otherPipeInNetwork = adjacents.get(0);

			otherPipeInNetwork.getNetwork().setOrigin(otherPipeInNetwork.getPos());

			NetworkMapper result = otherPipeInNetwork.getNetwork().updateGraph(otherPipeInNetwork.getWorld(), otherPipeInNetwork.getPos());

			// For sanity checking
			boolean foundRemovedPipe = false;

			for (AbstractCableWrapper removed : result.getRemovedCables()) {
				// Skip the removed cables if it is the origin one of the remove.
				if (removed.getPos().equals(originCable.getPos())) {
					foundRemovedPipe = true;
					continue;
				}

				// If the removed does not have a network, create a network.
				if (removed.getNetwork() == null) {
					formNetworkAt(removed.getWorld(), removed.getPos());
				}
			}

			if (!foundRemovedPipe) {
				throw new RuntimeException("Didn't find removed cable when splitting network");
			}

			markDirty();
		} else {
			LOGGER.debug("Removing empty network {}", originCable.getNetwork().getId());

			removeNetwork(originCable.getNetwork().getId());
		}
	}

	public List<AbstractCableWrapper> getAdjacents(AbstractCableWrapper cable) {
		List<AbstractCableWrapper> wrappers = new ArrayList<AbstractCableWrapper>();
		for (Direction dir : Direction.values()) {
			// Skip checking that side if that side is disabled.
			if (cable.isDisabledOnSide(dir)) {
				continue;
			}

			// Check if a cable exists on the provided side and it is enabled on that side
			// and of the same type.
			AbstractCableWrapper wrapper = getCable(cable.getPos().offset(dir));

			if (wrapper == null) {
				continue;
			}

			if (wrapper.getType() != cable.getType()) {
				continue;
			}

			if (wrapper != null && !wrapper.isDisabledOnSide(dir.getOpposite())) {
				wrappers.add(wrapper);
			}
		}
		return wrappers;
	}

	public Collection<CableNetwork> getNetworks() {
		return Networks.values();
	}

	@Override
	public void read(CompoundNBT tag) {
		ListNBT cables = tag.getList("cables", Constants.NBT.TAG_COMPOUND);
		for (INBT cableTag : cables) {
			CompoundNBT cableTagCompound = (CompoundNBT) cableTag;
			ResourceLocation type = new ResourceLocation(cableTagCompound.getString("type"));
			AbstractCableWrapper cable = CableWrapperRegistry.get().create(type, World, cableTagCompound);
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

		LOGGER.debug("Read {} pipes", WorldCables.size());
		LOGGER.debug("Read {} networks", Networks.size());
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		ListNBT cables = new ListNBT();
		WorldCables.values().forEach(cable -> {
			CompoundNBT cableTag = new CompoundNBT();
			cableTag.putString("type", cable.getType().toString());
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
		String name = PREFIX + "_" + world.getDimension().getType().getRegistryName().getNamespace() + "_" + world.getDimension().getType().getRegistryName().getPath();

		return world.getSavedData().getOrCreate(() -> new CableNetworkManager(name, world), name);
	}
}
