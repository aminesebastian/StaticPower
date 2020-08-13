/**
 * 
 */
package theking530.staticpower.cables.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
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
			Graph.getCables().values().forEach(cable -> cable.tick());
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

	public List<ITextComponent> getReaderOutput() {
		// Allocate the output list.
		List<ITextComponent> output = new LinkedList<ITextComponent>();
		output.add(new StringTextComponent(""));
		output.add(new StringTextComponent("NetworkID: ").appendText(String.format("%1$s%2$d with %3$d cables", TextFormatting.GRAY.toString(), NetworkId, Graph.getCables().size())));

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

	public World getWorld() {
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
