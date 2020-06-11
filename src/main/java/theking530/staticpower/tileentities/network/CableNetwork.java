/**
 * 
 */
package theking530.staticpower.tileentities.network;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper;
import theking530.staticpower.tileentities.network.factories.modules.CableNetworkModuleRegistry;
import theking530.staticpower.tileentities.network.modules.AbstractCableNetworkModule;

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
	private boolean InitialScanComplete;
	private HashMap<ResourceLocation, AbstractCableNetworkModule> Modules;

	public CableNetwork(BlockPos origin, long id) {
		NetworkMap = new HashMap<BlockPos, AbstractCableWrapper>();
		NetworkId = id;
		Origin = origin;
		Graph = new CableNetworkGraph(this);
		Modules = new HashMap<ResourceLocation, AbstractCableNetworkModule>();
	}

	public void tick(ServerWorld world) {
		// Perform a single scan when the world/network first loads.
		if (!InitialScanComplete) {
			InitialScanComplete = true;
			updateGraph(world, Origin);
		}

		// Tick all the attachments.
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.tick(world);
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
			throw new RuntimeException(String.format("Attempted to get a module of a type: %1$s that was already added.", type));
		}
		return (T) Modules.get(type);
	}

	public NetworkMapper updateGraph(World world, BlockPos startingPosition) {
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
		for (AbstractCableNetworkModule module : Modules.values()) {
			module.onAddedToNetwork(mainNetwork);
		}
	}

	public long getId() {
		return NetworkId;
	}

	public boolean isEmpty() {
		return NetworkMap.isEmpty();
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
