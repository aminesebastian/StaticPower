package theking530.staticpower.cables.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.ICableStateSyncTarget;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.cables.SparseCableLink.SparseCableConnectionType;

public class ServerCable {
	public enum CableConnectionState {
		NONE, CABLE, TILE_ENTITY
	}

	public static final String DATA_TAG_KEY = "data";
	protected CableNetwork Network;
	protected final Level World;
	protected final HashSet<ResourceLocation> supportedNetworkModules;
	private final BlockPos Position;
	private final boolean[] disabledSides;
	/** This tag should be used to store any data about this server cable. */
	private final CompoundTag dataTag;
	private final Map<Direction, ServerAttachmentDataContainer> attachmentData;

	private final boolean isSparse;
	private final Map<BlockPos, SparseCableLink> sparseLinks;

	public ServerCable(Level world, BlockPos position, boolean sparse, HashSet<ResourceLocation> supportedModules) {
		Position = position;
		World = world;
		dataTag = new CompoundTag();
		attachmentData = new HashMap<Direction, ServerAttachmentDataContainer>();
		for (Direction dir : Direction.values()) {
			attachmentData.put(dir, new ServerAttachmentDataContainer(dir));
		}
		supportedNetworkModules = supportedModules;
		disabledSides = new boolean[] { false, false, false, false, false, false };
		sparseLinks = new HashMap<BlockPos, SparseCableLink>();
		isSparse = sparse;
	}

	public ServerCable(Level world, CompoundTag tag) {
		// Set the world.
		World = world;

		// Get the position.
		Position = BlockPos.of(tag.getLong("position"));

		// Create the disabled sides.
		disabledSides = new boolean[] { false, false, false, false, false, false };

		isSparse = tag.getBoolean("sparse");

		// Create the links.
		sparseLinks = new HashMap<BlockPos, SparseCableLink>();
		ListTag sparseLinkTags = tag.getList("sparse_links", Tag.TAG_COMPOUND);
		for (Tag sparseLinkTag : sparseLinkTags) {
			SparseCableLink link = SparseCableLink.fromTag((CompoundTag) sparseLinkTag);
			sparseLinks.put(link.linkToPosition(), link);
		}

		// Deserialize the attachments.
		attachmentData = new HashMap<Direction, ServerAttachmentDataContainer>();
		CompoundTag attachmentNBT = tag.getCompound("attachments");
		for (Direction dir : Direction.values()) {
			String key = String.valueOf(dir.ordinal());
			if (attachmentNBT.contains(key)) {
				attachmentData.put(dir, ServerAttachmentDataContainer.createFromTag(attachmentNBT.getCompound(String.valueOf(dir.ordinal()))));
			} else {
				attachmentData.put(dir, new ServerAttachmentDataContainer(dir));
			}
		}

		// Get the supported network types.
		supportedNetworkModules = new HashSet<ResourceLocation>();
		ListTag modules = tag.getList("supported_modules", Tag.TAG_COMPOUND);
		for (Tag moduleTag : modules) {
			CompoundTag moduleTagCompound = (CompoundTag) moduleTag;
			supportedNetworkModules.add(new ResourceLocation(moduleTagCompound.getString("module_type")));
		}

		// Serialize the disabled sides.
		for (int i = 0; i < 6; i++) {
			disabledSides[i] = tag.getBoolean("disabled" + i);
		}

		dataTag = tag.getCompound(DATA_TAG_KEY);
	}

	public SparseCableLink addSparseLink(BlockPos linkToPosition, CompoundTag data) {
		if (!linkToPosition.equals(getPos()) && !isLinkedTo(linkToPosition) && isSparse()) {
			long linkId = CableNetworkManager.get(getWorld()).getAndIncrementCurentSparseLinkId();
			ServerCable otherCable = CableNetworkManager.get(getWorld()).getCable(linkToPosition);
			otherCable.sparseLinks.put(getPos(), new SparseCableLink(linkId, getPos(), data, SparseCableConnectionType.STARTING));
			otherCable.synchronizeServerState();

			SparseCableLink endLink = new SparseCableLink(linkId, linkToPosition, data, SparseCableConnectionType.ENDING);
			sparseLinks.put(linkToPosition, endLink);
			CableNetworkManager.get(getWorld()).joinSparseCables(this, otherCable);
			synchronizeServerState();
			return endLink;
		}
		return null;
	}

	public List<SparseCableLink> breakAllSparseLinks(BlockPos... linkedToPosition) {
		return removeSparseLinks(sparseLinks.values().stream().map((x) -> x.linkToPosition()).toList().toArray(new BlockPos[0]));
	}

	public List<SparseCableLink> removeSparseLinks(BlockPos... linkedToPosition) {
		List<SparseCableLink> output = new ArrayList<SparseCableLink>();
		List<ServerCable> targets = new ArrayList<ServerCable>();
		for (BlockPos pos : linkedToPosition) {
			SparseCableLink removedLink = sparseLinks.remove(pos);
			if (removedLink != null) {
				ServerCable otherCable = CableNetworkManager.get(getWorld()).getCable(pos);
				otherCable.sparseLinks.remove(getPos());
				targets.add(otherCable);
				output.add(removedLink);
			}
		}

		// If we did break any connections, try to separate ourselves off those targets.
		if (output.size() > 0) {
			CableNetworkManager.get(getWorld()).separateSparseCables(this, targets);
			for (ServerCable other : targets) {
				other.synchronizeServerState();
			}
			synchronizeServerState();
		}

		return output;
	}

	public boolean isLinkedTo(BlockPos position) {
		return sparseLinks.containsKey(position);
	}

	public boolean isSparse() {
		return this.isSparse;
	}

	public Collection<SparseCableLink> getSparseLinks() {
		return sparseLinks.values();
	}

	public List<CableScanLocation> getScanLocations() {
		List<CableScanLocation> output = new ArrayList<CableScanLocation>();
		for (Direction dir : Direction.values()) {
			output.add(new CableScanLocation(getPos().relative(dir), dir, false));
		}

		for (SparseCableLink link : getSparseLinks()) {
			output.add(new CableScanLocation(link.linkToPosition(), null, true));
		}

		return output;
	}

	public List<ServerCable> getAdjacents() {
		List<ServerCable> wrappers = new ArrayList<ServerCable>();
		for (CableScanLocation scanLoc : getScanLocations()) {
			if (scanLoc.isSparseLink()) {
				ServerCable cable = CableNetworkManager.get(getWorld()).getCable(scanLoc.getLocation());
				wrappers.add(cable);
			} else {
				// Skip checking that side if that side is disabled.
				if (isDisabledOnSide(scanLoc.getSide())) {
					continue;
				}

				// Check if a cable exists on the provided side and it is enabled on that side
				// and of the same type.
				ServerCable adjacent = CableNetworkManager.get(getWorld()).getCable(scanLoc.getLocation());

				if (adjacent == null) {
					continue;
				}

				if (adjacent.isDisabledOnSide(scanLoc.getSide().getOpposite())) {
					continue;
				}

				if (adjacent.getNetwork() == null) {
					continue;
				}

				if (!adjacent.getNetwork().canAcceptCable(adjacent, this)) {
					continue;
				}

				if (adjacent.shouldConnectTo(this)) {
					wrappers.add(adjacent);
				}
			}
		}

		return wrappers;
	}

	public CompoundTag getDataTag() {
		return dataTag;
	}

	public void clearAttachmentDataForSide(Direction side) {
		attachmentData.put(side, new ServerAttachmentDataContainer(side));
	}

	public void addAttachmentDataForSide(Direction side, ResourceLocation id) {
		attachmentData.put(side, new ServerAttachmentDataContainer(id, side));
	}

	public ServerAttachmentDataContainer getAttachmentDataContainerForSide(Direction side) {
		return attachmentData.get(side);
	}

	public BlockPos getPos() {
		return Position;
	}

	public CableNetwork getNetwork() {
		return Network;
	}

	public Level getWorld() {
		return World;
	}

	/**
	 * Lets this cable wrapper know it joined a network. The ONLY time to call it
	 * with updateBlock == false is when first loading the world.
	 * 
	 * @param network
	 * @param updateBlock
	 */
	public void onNetworkJoined(CableNetwork network) {
		// Save the network.
		Network = network;

		// Add all the supported modules if they're not present.
		for (ResourceLocation moduleType : supportedNetworkModules) {
			if (!network.hasModule(moduleType)) {
				network.addModule(CableNetworkModuleRegistry.get().create(moduleType));
			}
		}

		// Update the owning block.
		synchronizeServerState();
		updateCableBlock();
	}

	public void onNetworkLeft(CableNetwork oldNetwork) {
		Network = null;
		synchronizeServerState();
		updateCableBlock();
	}

	public void onRemoved() {
		for (ServerCable otherCable : Network.getGraph().getCables().values()) {
			if (otherCable.isLinkedTo(getPos())) {
				otherCable.sparseLinks.remove(getPos());
				otherCable.synchronizeServerState();
			}
		}
	}

	public HashSet<ResourceLocation> getSupportedNetworkModules() {
		return supportedNetworkModules;
	}

	public boolean supportsNetworkModule(ResourceLocation moduleType) {
		return supportedNetworkModules.contains(moduleType);
	}

	public boolean shouldConnectTo(ServerCable otherCable) {
		for (ResourceLocation moduleType : otherCable.getSupportedNetworkModules()) {
			if (supportsNetworkModule(moduleType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This updates the tile entity that this wrapper represents.
	 */
	public void updateCableBlock() {
		BlockState state = World.getBlockState(Position);
		World.sendBlockUpdated(Position, state, state, 1 | 2);
	}

	public boolean isDisabledOnSide(Direction side) {
		return disabledSides[side.ordinal()];
	}

	public void setDisabledStateOnSide(Direction side, boolean disabledState) {
		disabledSides[side.ordinal()] = disabledState;
		synchronizeServerState();
	}

	public void synchronizeServerState() {
		ICableStateSyncTarget target = (ICableStateSyncTarget) getWorld().getExistingBlockEntity(getPos());
		if (target != null) {
			target.synchronizeServerToClient(this, new CompoundTag());
		}
	}

	public CompoundTag writeToNbt(CompoundTag tag) {
		// Serialize the position.
		tag.putLong("position", Position.asLong());

		// Serialize the supported module types.
		ListTag supportedModules = new ListTag();
		supportedNetworkModules.forEach(moduleType -> {
			CompoundTag moduleTag = new CompoundTag();
			moduleTag.putString("module_type", moduleType.toString());
			supportedModules.add(moduleTag);
		});
		tag.put("supported_modules", supportedModules);

		// Serialize the disabled sides.
		for (int i = 0; i < 6; i++) {
			tag.putBoolean("disabled" + i, disabledSides[i]);
		}

		// Serialize the sparse links..
		ListTag sparseLinkTag = new ListTag();
		sparseLinks.values().forEach(link -> {
			sparseLinkTag.add(link.serialize());
		});
		tag.put("sparse_links", sparseLinkTag);

		// Serialize if sparse.
		tag.putBoolean("sparse", isSparse);

		// Serialize the attachments.
		CompoundTag attachmentTags = new CompoundTag();
		for (Direction dir : Direction.values()) {
			attachmentTags.put(String.valueOf(dir.ordinal()), attachmentData.get(dir).serialize());
		}
		tag.put("attachments", attachmentTags);

		// Serialize the data.
		tag.put(DATA_TAG_KEY, dataTag);

		return tag;
	}

	/**
	 * Gets the list of {@link AbstractCableProviderComponent} components that exist
	 * on the tile entity that corresponds to this cable. This exists on both the
	 * server and the client and is therefore useful to synchronize any values from
	 * here to the client.
	 * 
	 * @return
	 */
	public List<AbstractCableProviderComponent> getCableProviderComponents() {
		BlockEntityBase baseTe = (BlockEntityBase) World.getChunkAt(getPos()).getBlockEntity(getPos(), LevelChunk.EntityCreationType.QUEUED);
		if (baseTe == null) {
			throw new RuntimeException(String.format("A cable wrapper exists without a cooresponding AbstractCableProviderComponent at BlockPos: %1$s.", Position));
		}
		return baseTe.getComponents(AbstractCableProviderComponent.class);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		ServerCable cable = (ServerCable) other;
		return World.equals(cable.World) && Position.equals(cable.Position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(World, Position);
	}
}
