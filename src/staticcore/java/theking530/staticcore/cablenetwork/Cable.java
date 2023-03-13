package theking530.staticcore.cablenetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapability;
import theking530.staticcore.cablenetwork.capabilities.ServerCableCapabilityType;
import theking530.staticcore.cablenetwork.data.CableConnectionState;
import theking530.staticcore.cablenetwork.data.CableConnectionState.CableConnectionType;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.cablenetwork.scanning.CableScanLocation;
import theking530.staticpower.StaticPowerRegistries;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.AbstractCableBlock;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.utilities.NBTUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public class Cable {
	protected final Level level;
	private final BlockPos position;
	protected CableNetwork network;
	protected final Set<CableNetworkModuleType> supportedNetworkModules;

	private final CompoundTag dataTag;
	private final CableConnectionState[] sidedData;

	private final boolean canAcceptSparseLink;
	private final Map<BlockPos, SparseCableLink> sparseLinks;
	private final Set<CableDestination> supportedDestinationTypes;
	private final Map<ServerCableCapabilityType<?>, ServerCableCapability> capabilities;

	public Cable(Level level, BlockPos position, boolean canAcceptSparseLink, Set<CableNetworkModuleType> supportedNetworkModules,
			Set<CableDestination> supportedDestinationTypes) {
		this.position = position;
		this.level = level;
		this.canAcceptSparseLink = canAcceptSparseLink;
		this.supportedNetworkModules = supportedNetworkModules;
		this.supportedDestinationTypes = supportedDestinationTypes;
		dataTag = new CompoundTag();
		sparseLinks = new HashMap<>();
		capabilities = new HashMap<>();

		sidedData = new CableConnectionState[6];
		for (Direction dir : Direction.values()) {
			sidedData[dir.ordinal()] = CableConnectionState.createEmpty();
		}
	}

	public void preWorldTick() {
		for (ServerCableCapability cap : capabilities.values()) {
			cap.preWorldTick();
		}
	}

	public void tick() {
		for (ServerCableCapability cap : capabilities.values()) {
			cap.tick();
		}
	}

	public SparseCableLink addSparseLink(BlockPos linkToPosition, CompoundTag data) {
		if (!linkToPosition.equals(getPos()) && !isLinkedTo(linkToPosition)) {
			long linkId = CableNetworkAccessor.get(getLevel()).getAndIncrementCurentSparseLinkId();
			Cable otherCable = CableNetworkAccessor.get(getLevel()).getCable(linkToPosition);
			otherCable.sparseLinks.put(getPos(), new SparseCableLink(linkId, getPos(), data, SparseCableConnectionType.STARTING));
			otherCable.synchronizeServerState();

			SparseCableLink endLink = new SparseCableLink(linkId, linkToPosition, data, SparseCableConnectionType.ENDING);
			sparseLinks.put(linkToPosition, endLink);
			CableNetworkAccessor.get(getLevel()).joinSparseCables(this, otherCable);
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
		List<Cable> targets = new ArrayList<Cable>();
		for (BlockPos pos : linkedToPosition) {
			SparseCableLink removedLink = sparseLinks.remove(pos);
			if (removedLink != null) {
				Cable otherCable = CableNetworkAccessor.get(getLevel()).getCable(pos);
				otherCable.sparseLinks.remove(getPos());
				targets.add(otherCable);
				output.add(removedLink);
			}
		}

		// If we did break any connections, try to separate ourselves off those targets.
		if (output.size() > 0) {
			CableNetworkAccessor.get(getLevel()).separateSparseCables(this, targets);
			for (Cable other : targets) {
				other.synchronizeServerState();
			}
			synchronizeServerState();
		}

		return output;
	}

	public boolean isLinkedTo(BlockPos position) {
		return sparseLinks.containsKey(position);
	}

	public boolean canAcceptSparseLink() {
		return canAcceptSparseLink;
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

	public List<Cable> getAdjacents() {
		List<Cable> wrappers = new ArrayList<Cable>();
		for (CableScanLocation scanLoc : getScanLocations()) {
			if (scanLoc.isSparseLink()) {
				Cable cable = CableNetworkAccessor.get(getLevel()).getCable(scanLoc.getLocation());
				wrappers.add(cable);
			} else {
				// Skip checking that side if that side is disabled.
				if (isDisabledOnSide(scanLoc.getSide())) {
					continue;
				}

				// Check if a cable exists on the provided side and it is enabled on that side
				// and of the same type.
				Cable adjacent = CableNetworkAccessor.get(getLevel()).getCable(scanLoc.getLocation());

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

	public Level getLevel() {
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
			CableConnectionState connectionState = sidedData[dir.ordinal()];
			if (connectionState.getConnectionType() == CableConnectionType.DISABLED) {
				continue;
			}

			BlockPos toTest = getPos().relative(dir);
			if (network.getGraph().getCables().containsKey(toTest)) {
				connectionState.setConnectionType(CableConnectionType.CABLE);
			} else if (network.getGraph().getDestinations().containsKey(toTest)) {
				DestinationWrapper wrapper = network.getGraph().getDestinations().get(toTest);
				if (wrapper.hasSupportedDestinationTypes(dir.getOpposite(), this)) {
					connectionState.setConnectionType(CableConnectionType.DESTINATION);
				}
			} else {
				connectionState.setConnectionType(CableConnectionType.NONE);
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
		for (Cable otherCable : network.getGraph().getCables().values()) {
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

	public boolean shouldConnectToCable(Cable otherCable) {
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
		return sidedData[side.ordinal()].getConnectionType() == CableConnectionType.DISABLED;
	}

	public void setDisabledStateOnSide(Direction side, boolean disabledState) {
		if (isDisabledOnSide(side) != disabledState) {
			if (disabledState) {
				sidedData[side.ordinal()].setConnectionType(CableConnectionType.DISABLED);
			} else {
				sidedData[side.ordinal()].setConnectionType(CableConnectionType.NONE);
			}

			// Only do this if we are already part of a network. This method could be called
			// after a ServerCable is created but before it's added to the manager, so we do
			// this check to be safe.
			if (network != null) {
				CableNetworkAccessor.get(level).refreshCable(this);
			}

			synchronizeServerState();
		}
	}

	public CableConnectionType getConnectionType(Direction side) {
		return this.sidedData[side.ordinal()].getConnectionType();
	}

	public void synchronizeServerState() {
		BlockState cableState = getLevel().getBlockState(getPos());

		// Arbitrary check to see if this block has cable connection types.
		if (CableUtilities.doesBlockStateHaveConnectionProperty(cableState)) {
			for (Direction dir : Direction.values()) {
				cableState = cableState.setValue(AbstractCableBlock.CONNECTION_TYPES.get(dir), sidedData[dir.ordinal()].getConnectionType());
			}
			getLevel().setBlockAndUpdate(getPos(), cableState);
		}

		ICableStateSyncTarget target = (ICableStateSyncTarget) getLevel().getExistingBlockEntity(getPos());
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
			for (CableConnectionState data : sidedData) {
				sidedTags.add(data.serialize());
			}
			defaultData.put("sided_data", sidedTags);

			// Send the sync packet.
			CableStateSyncPacket syncPacket = new CableStateSyncPacket(getPos(), defaultData);
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getLevel(), getPos(), 64, syncPacket);
		}
	}

	public Cable(Level world, CompoundTag tag) {
		// Set the world.
		level = world;

		position = BlockPos.of(tag.getLong("position"));
		canAcceptSparseLink = tag.getBoolean("sparse");

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
		sidedData = new CableConnectionState[sidedTags.size()];
		for (int i = 0; i < sidedTags.size(); i++) {
			sidedData[i] = CableConnectionState.deserialize(sidedTags.getCompound(i));
		}

		// Deserialize the destination types.
		supportedDestinationTypes = new HashSet<CableDestination>();
		NBTUtilities.deserialize(tag.getList("destination_types", Tag.TAG_STRING), (destTag) -> {
			ResourceLocation key = new ResourceLocation(destTag.getAsString());
			CableDestination dest = StaticPowerRegistries.CableDestinationRegistry().getValue(key);
			supportedDestinationTypes.add(dest);
			return dest;
		});

		// Deserialize the capabilities.
		capabilities = new HashMap<>();
		NBTUtilities.deserialize(tag.getList("capability_types", Tag.TAG_COMPOUND), (rawTag) -> {
			CompoundTag capabilityTag = (CompoundTag) rawTag;
			ResourceLocation key = new ResourceLocation(capabilityTag.getString("type"));
			ServerCableCapabilityType<?> type = StaticPowerRegistries.CableCapabilityRegistry().getValue(key);
			ServerCableCapability cap = type.create(this, capabilityTag);
			capabilities.put(cap.getType(), cap);
			return cap;
		});
		dataTag = tag.getCompound("data");
	}

	public CompoundTag writeToNbt(CompoundTag tag) {
		tag.putLong("position", position.asLong());
		tag.putBoolean("sparse", canAcceptSparseLink);

		// Serialize the supported module types.
		ListTag supportedModules = new ListTag();
		supportedNetworkModules.forEach(moduleType -> {
			CompoundTag moduleTag = new CompoundTag();
			moduleTag.putString("module_type", StaticPowerRegistries.CableModuleRegsitry().getKey(moduleType).toString());
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
		for (CableConnectionState data : sidedData) {
			sidedTags.add(data.serialize());
		}
		tag.put("sided_data", sidedTags);

		// Serialize the destination types.
		ListTag destinationList = NBTUtilities.serialize(supportedDestinationTypes, (dest) -> {
			return StringTag.valueOf(StaticPowerRegistries.CableDestinationRegistry().getKey(dest).toString());
		});
		tag.put("destination_types", destinationList);

		// Serialize the capabilities.
		ListTag capabilityList = NBTUtilities.serialize(capabilities.keySet(), (capType) -> {
			CompoundTag capTag = new CompoundTag();
			capTag.putString("type", StaticPowerRegistries.CableCapabilityRegistry().getKey(capType).toString());
			capabilities.get(capType).save(capTag);
			return capTag;
		});
		tag.put("capability_types", capabilityList);

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

	@SuppressWarnings("unchecked")
	public <T extends ServerCableCapability> Optional<T> getCapability(ServerCableCapabilityType<?> capabilityType) {
		if (capabilities.containsKey(capabilityType)) {
			return (Optional<T>) Optional.of(capabilities.get(capabilityType));
		} else {
			return Optional.empty();
		}
	}

	public void registerCapability(ServerCableCapability capability) {
		if (capabilities.containsKey(capability.getType())) {
			throw new RuntimeException("Attempting to register a capability type that is already registered!");
		}
		capabilities.put(capability.getType(), capability);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		Cable cable = (Cable) other;
		return level.equals(cable.level) && position.equals(cable.position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, position);
	}
}
