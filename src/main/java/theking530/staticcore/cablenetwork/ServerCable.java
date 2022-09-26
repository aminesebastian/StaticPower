package theking530.staticcore.cablenetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import theking530.staticcore.cablenetwork.SparseCableLink.SparseCableConnectionType;
import theking530.staticcore.cablenetwork.data.CableSideConnectionState;
import theking530.staticcore.cablenetwork.data.CableSideConnectionState.CableConnectionType;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.cablenetwork.scanning.CableScanLocation;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.utilities.NBTUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class ServerCable {
	protected final Level level;
	private final BlockPos position;
	protected CableNetwork network;
	protected final Set<CableNetworkModuleType> supportedNetworkModules;

	private final CompoundTag dataTag;
	private final CableSideConnectionState[] sidedData;

	private final boolean isSparse;
	private final Map<BlockPos, SparseCableLink> sparseLinks;
	private final Set<CableDestination> supportedDestinationTypes;

	public ServerCable(Level level, BlockPos position, boolean sparse, Set<CableNetworkModuleType> supportedNetworkModules, Set<CableDestination> supportedDestinationTypes) {
		this.position = position;
		this.level = level;
		this.isSparse = sparse;
		this.supportedNetworkModules = supportedNetworkModules;
		this.supportedDestinationTypes = supportedDestinationTypes;
		dataTag = new CompoundTag();
		sparseLinks = new HashMap<BlockPos, SparseCableLink>();

		sidedData = new CableSideConnectionState[6];
		for (Direction dir : Direction.values()) {
			sidedData[dir.ordinal()] = CableSideConnectionState.createEmpty();
		}
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

	/**
	 * Includes all non-disabled directions and also any sparse link positions.
	 * 
	 * @return
	 */
	public List<CableScanLocation> getScanLocations() {
		List<CableScanLocation> output = new ArrayList<CableScanLocation>();
		for (Direction dir : Direction.values()) {
			// Skip checking that side if that side is disabled.
			if (isDisabledOnSide(dir)) {
				continue;
			}
			output.add(new CableScanLocation(getPos().relative(dir), dir, false));
		}

		for (SparseCableLink link : getSparseLinks()) {
			Direction side = WorldUtilities.getDirectionBetweenBlocks(getPos(), link.linkToPosition());
			output.add(new CableScanLocation(link.linkToPosition(), side, true));
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

				if (adjacent.shouldConnectToCable(this)) {
					wrappers.add(adjacent);
				}
			}
		}

		return wrappers;
	}

	public CompoundTag getDataTag() {
		return dataTag;
	}

	public ItemStack removeAttachmentFromSide(Direction side) {
		if (hasAttachmentOnSide(side)) {
			ItemStack attachment = sidedData[side.ordinal()].removeAttachment();
			synchronizeServerState();
			return attachment;
		}
		return ItemStack.EMPTY;
	}

	public boolean addAttachmentToSide(Direction side, ItemStack attachment) {
		if (!hasAttachmentOnSide(side)) {
			sidedData[side.ordinal()].setAttachment(attachment);
			synchronizeServerState();
			return true;
		}
		return false;
	}

	public ItemStack getAttachmentOnSide(Direction side) {
		return sidedData[side.ordinal()].getAttachment();
	}

	public boolean hasAttachmentOnSide(Direction side) {
		return !getAttachmentOnSide(side).isEmpty();
	}

	public ItemStack removeCoverFromSide(Direction side) {
		if (hasCoverOnSide(side)) {
			ItemStack cover = sidedData[side.ordinal()].removeCover();
			synchronizeServerState();
			return cover;
		}
		return ItemStack.EMPTY;
	}

	public boolean addCoverToSide(Direction side, ItemStack cover) {
		if (!hasCoverOnSide(side)) {
			sidedData[side.ordinal()].setCover(cover);
			synchronizeServerState();
			return true;
		}
		return false;
	}

	public ItemStack getCoverOnSide(Direction side) {
		return sidedData[side.ordinal()].getCover();
	}

	public boolean hasCoverOnSide(Direction side) {
		return !getCoverOnSide(side).isEmpty();
	}

	public BlockPos getPos() {
		return position;
	}

	public CableNetwork getNetwork() {
		return network;
	}

	public Level getWorld() {
		return level;
	}

	public Set<CableDestination> getSupportedDestinationTypes() {
		return supportedDestinationTypes;
	}

	/**
	 * Lets this cable wrapper know it joined a network.
	 * 
	 * @param network
	 * @param updateBlock
	 */
	public void onNetworkJoined(CableNetwork network) {
		// Save the network.
		this.network = network;

		// Add all the supported modules if they're not present.
		for (CableNetworkModuleType moduleType : supportedNetworkModules) {
			if (!network.hasModule(moduleType)) {
				network.addModule(moduleType.create());
			}
		}

		// Update the owning block.
		synchronizeServerState();
		updateCableBlock();
	}

	public void onNetworkUpdated(CableNetwork network) {
		for (Direction dir : Direction.values()) {
			BlockPos toTest = getPos().relative(dir);
			if (network.getGraph().getCables().containsKey(toTest)) {
				sidedData[dir.ordinal()].setConnectionType(CableConnectionType.CABLE);
			} else if (network.getGraph().getDestinations().containsKey(toTest)) {
				sidedData[dir.ordinal()].setConnectionType(CableConnectionType.TILE_ENTITY);
			} else {
				sidedData[dir.ordinal()].setConnectionType(CableConnectionType.NONE);
			}
		}
		synchronizeServerState();
	}

	public void onNetworkLeft(CableNetwork oldNetwork) {
		network = null;
		synchronizeServerState();
		updateCableBlock();
	}

	public void onRemoved() {
		for (ServerCable otherCable : network.getGraph().getCables().values()) {
			if (otherCable.isLinkedTo(getPos())) {
				otherCable.sparseLinks.remove(getPos());
				otherCable.synchronizeServerState();
			}
		}
	}

	public Set<CableNetworkModuleType> getSupportedNetworkModules() {
		return supportedNetworkModules;
	}

	public boolean supportsNetworkModule(CableNetworkModuleType moduleType) {
		return supportedNetworkModules.contains(moduleType);
	}

	public boolean shouldConnectToCable(ServerCable otherCable) {
		for (CableNetworkModuleType moduleType : otherCable.getSupportedNetworkModules()) {
			if (supportsNetworkModule(moduleType)) {
				return true;
			}
		}
		return false;
	}

	public boolean shouldConnectToDestination(Level world, BlockPos pos, Direction side) {
		return false;
	}

	/**
	 * This updates the tile entity that this wrapper represents.
	 */
	public void updateCableBlock() {
		BlockState state = level.getBlockState(position);
		level.sendBlockUpdated(position, state, state, 1 | 2);
	}

	public boolean isDisabledOnSide(Direction side) {
		return sidedData[side.ordinal()].isDisabled();
	}

	public void setDisabledStateOnSide(Direction side, boolean disabledState) {
		if (sidedData[side.ordinal()].isDisabled() != disabledState) {
			sidedData[side.ordinal()].setDisabled(disabledState);

			// Only do this if we are already part of a network. This method could be called
			// after a ServerCable is created but before it's added to the manager, so we do
			// this check to be safe.
			if (network != null) {
				CableNetworkManager.get(level).refreshCable(this);
			}

			synchronizeServerState();
		}
	}

	public void synchronizeServerState() {
		ICableStateSyncTarget target = (ICableStateSyncTarget) getWorld().getExistingBlockEntity(getPos());
		if (target != null) {
			CompoundTag defaultData = new CompoundTag();

			// Serialize the sparse links.
			ListTag sparseLinkTag = new ListTag();
			getSparseLinks().forEach(link -> {
				sparseLinkTag.add(link.serialize());
			});
			defaultData.put("sparse_links", sparseLinkTag);

			// Serialize the connection data.
			ListTag sidedTags = new ListTag();
			for (CableSideConnectionState data : sidedData) {
				sidedTags.add(data.serialize());
			}
			defaultData.put("sided_data", sidedTags);

			target.synchronizeServerToClient(this, defaultData);
		}
	}

	public ServerCable(Level world, CompoundTag tag) {
		// Set the world.
		level = world;

		position = BlockPos.of(tag.getLong("position"));
		isSparse = tag.getBoolean("sparse");

		// Get the supported network types.
		supportedNetworkModules = new HashSet<CableNetworkModuleType>();
		ListTag modules = tag.getList("supported_modules", Tag.TAG_COMPOUND);
		for (Tag moduleTag : modules) {
			CompoundTag moduleTagCompound = (CompoundTag) moduleTag;
			ResourceLocation registryName = new ResourceLocation(moduleTagCompound.getString("module_type"));
			supportedNetworkModules.add(StaticPowerRegistries.CableModuleRegsitry().getValue(registryName));
		}

		// Create the links.
		sparseLinks = new HashMap<BlockPos, SparseCableLink>();
		ListTag sparseLinkTags = tag.getList("sparse_links", Tag.TAG_COMPOUND);
		for (Tag sparseLinkTag : sparseLinkTags) {
			SparseCableLink link = SparseCableLink.fromTag((CompoundTag) sparseLinkTag);
			sparseLinks.put(link.linkToPosition(), link);
		}

		// Deserialize the sided data.
		ListTag sidedTags = tag.getList("sided_data", Tag.TAG_COMPOUND);
		sidedData = new CableSideConnectionState[sidedTags.size()];
		for (int i = 0; i < sidedTags.size(); i++) {
			sidedData[i] = CableSideConnectionState.deserialize(sidedTags.getCompound(i));
		}

		// Deserialize the destination types.
		supportedDestinationTypes = new HashSet<CableDestination>();
		List<CableDestination> destinations = NBTUtilities.deserialize(tag.getList("destination_types", Tag.TAG_STRING), (destTag) -> {
			ResourceLocation key = new ResourceLocation(destTag.getAsString());
			return StaticPowerRegistries.CableDestinationRegistry().getValue(key);
		});
		for (CableDestination destination : destinations) {
			supportedDestinationTypes.add(destination);
		}

		dataTag = tag.getCompound("data");
	}

	public CompoundTag writeToNbt(CompoundTag tag) {
		tag.putLong("position", position.asLong());
		tag.putBoolean("sparse", isSparse);

		// Serialize the supported module types.
		ListTag supportedModules = new ListTag();
		supportedNetworkModules.forEach(moduleType -> {
			CompoundTag moduleTag = new CompoundTag();
			moduleTag.putString("module_type", moduleType.getRegistryName().toString());
			supportedModules.add(moduleTag);
		});
		tag.put("supported_modules", supportedModules);

		// Serialize the sparse links..
		ListTag sparseLinkTag = new ListTag();
		sparseLinks.values().forEach(link -> {
			sparseLinkTag.add(link.serialize());
		});
		tag.put("sparse_links", sparseLinkTag);

		// Serialize the sided data.
		ListTag sidedTags = new ListTag();
		for (CableSideConnectionState data : sidedData) {
			sidedTags.add(data.serialize());
		}
		tag.put("sided_data", sidedTags);

		// Serialize the destination types.
		ListTag destinationList = NBTUtilities.serialize(supportedDestinationTypes, (dest) -> {
			return StringTag.valueOf(dest.getRegistryName().toString());
		});
		tag.put("destination_types", destinationList);

		// Serialize the data.
		tag.put("data", dataTag);

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
		BlockEntityBase baseTe = (BlockEntityBase) level.getChunkAt(getPos()).getBlockEntity(getPos(), LevelChunk.EntityCreationType.QUEUED);
		if (baseTe == null) {
			throw new RuntimeException(String.format("A cable wrapper exists without a cooresponding AbstractCableProviderComponent at BlockPos: %1$s.", position));
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
		return level.equals(cable.level) && position.equals(cable.position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, position);
	}
}