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

	public boolean addSparseLink(BlockPos linkToPosition) {
		if (!linkToPosition.equals(getPos()) && !isLinkedTo(linkToPosition)) {
			ServerCable otherCable = CableNetworkManager.get(getWorld()).getCable(linkToPosition);
			otherCable.sparseLinks.put(getPos(), new SparseCableLink(getPos(), SparseCableConnectionType.STARTING));

			sparseLinks.put(linkToPosition, new SparseCableLink(linkToPosition, SparseCableConnectionType.ENDING));
			CableNetworkManager.get(getWorld()).joinSparseCables(this, otherCable);
			return true;
		}
		return false;
	}

	public boolean removeSparseLink(BlockPos linkedToPosition) {
		if (!linkedToPosition.equals(getPos())) {
			if (sparseLinks.remove(linkedToPosition) != null) {

				ServerCable otherCable = CableNetworkManager.get(getWorld()).getCable(linkedToPosition);
				otherCable.sparseLinks.remove(getPos());

				CableNetworkManager.get(getWorld()).separateSparseCables(this, otherCable);
				return true;
			}
		}
		return false;
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

	public List<ServerCable> getAdjacents() {
		List<ServerCable> wrappers = new ArrayList<ServerCable>();
		if (isSparse()) {
			for (SparseCableLink link : getSparseLinks()) {
				ServerCable cable = CableNetworkManager.get(getWorld()).getCable(link.linkToPosition());
				wrappers.add(cable);
			}
		} else {
			for (Direction dir : Direction.values()) {
				// Skip checking that side if that side is disabled.
				if (isDisabledOnSide(dir)) {
					continue;
				}

				// Check if a cable exists on the provided side and it is enabled on that side
				// and of the same type.
				ServerCable adjacent = CableNetworkManager.get(getWorld()).getCable(getPos().relative(dir));

				if (adjacent == null) {
					continue;
				}

				if (adjacent.isDisabledOnSide(dir.getOpposite())) {
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

	public boolean containsProperty(String key) {
		return dataTag.contains(key);
	}

	public int getIntProperty(String key) {
		return dataTag.getInt(key);
	}

	public int getByteProperty(String key) {
		return dataTag.getByte(key);
	}

	public float getFloatProperty(String key) {
		return dataTag.getFloat(key);
	}

	public long getLongProperty(String key) {
		return dataTag.getLong(key);
	}

	public double getDoubleProperty(String key) {
		return dataTag.getDouble(key);
	}

	public boolean getBooleanProperty(String key) {
		return dataTag.getBoolean(key);
	}

	public String getStringProperty(String key) {
		return dataTag.getString(key);
	}

	public CompoundTag getTagProperty(String key) {
		return dataTag.getCompound(key);
	}

	public void setProperty(String key, int value) {
		dataTag.putInt(key, value);
	}

	public void setProperty(String key, byte value) {
		dataTag.putByte(key, value);
	}

	public void setProperty(String key, boolean value) {
		dataTag.putBoolean(key, value);
	}

	public void setProperty(String key, float value) {
		dataTag.putFloat(key, value);
	}

	public void setProperty(String key, long value) {
		dataTag.putLong(key, value);
	}

	public void setProperty(String key, double value) {
		dataTag.putDouble(key, value);
	}

	public void setProperty(String key, String value) {
		dataTag.putString(key, value);
	}

	public void setProperty(String key, CompoundTag value) {
		dataTag.put(key, value);
	}

	public CompoundTag getCompleteDataTag() {
		return dataTag;
	}

	public void clearAttachmentDataForSide(Direction side) {
		attachmentData.put(side, new ServerAttachmentDataContainer(side));
	}

	public void reinitializeAttachmentDataForSide(Direction side, ResourceLocation id) {
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
	public void onNetworkJoined(CableNetwork network, boolean updateBlock) {
		// Save the network.
		Network = network;

		// Add all the supported modules if they're not present.
		for (ResourceLocation moduleType : supportedNetworkModules) {
			if (!network.hasModule(moduleType)) {
				network.addModule(CableNetworkModuleRegistry.get().create(moduleType));
			}
		}

		// Update the owning block.
		if (updateBlock) {
			updateCableBlock();
		}
	}

	public void onNetworkLeft(CableNetwork oldNetwork) {
		for (ServerCable otherCable : oldNetwork.getGraph().getCables().values()) {
			if (otherCable.isLinkedTo(getPos())) {
				otherCable.sparseLinks.remove(getPos());
			}
		}
		Network = null;
		updateCableBlock();
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
