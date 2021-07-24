package theking530.staticpower.cables.network;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.TileEntityBase;

public class ServerCable {
	public enum CableConnectionState {
		NONE, CABLE, TILE_ENTITY
	}

	public static final String DATA_TAG_KEY = "data";
	protected CableNetwork Network;
	protected final World World;
	protected final HashSet<ResourceLocation> supportedNetworkModules;
	private final BlockPos Position;
	private final boolean[] disabledSides;
	/** This tag should be used to store any data about this server cable. */
	private final CompoundNBT dataTag;
	private final Map<Direction, ServerAttachmentDataContainer> attachmentData;

	public ServerCable(World world, BlockPos position, HashSet<ResourceLocation> supportedModules) {
		Position = position;
		World = world;
		dataTag = new CompoundNBT();
		attachmentData = new HashMap<Direction, ServerAttachmentDataContainer>();
		for (Direction dir : Direction.values()) {
			attachmentData.put(dir, new ServerAttachmentDataContainer(dir));
		}
		supportedNetworkModules = supportedModules;
		disabledSides = new boolean[] { false, false, false, false, false, false };
	}

	public ServerCable(World world, BlockPos position, HashSet<ResourceLocation> supportedModules, Consumer<ServerCable> propertiesHandle) {
		this(world, position, supportedModules);
		propertiesHandle.accept(this);
	}

	public ServerCable(World world, CompoundNBT tag) {
		// Set the world.
		World = world;

		// Get the position.
		Position = BlockPos.fromLong(tag.getLong("position"));

		// Create the disabled sides.
		disabledSides = new boolean[] { false, false, false, false, false, false };

		// Deserialize the attachments.
		attachmentData = new HashMap<Direction, ServerAttachmentDataContainer>();
		CompoundNBT attachmentNBT = tag.getCompound("attachments");
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
		ListNBT modules = tag.getList("supported_modules", Constants.NBT.TAG_COMPOUND);
		for (INBT moduleTag : modules) {
			CompoundNBT moduleTagCompound = (CompoundNBT) moduleTag;
			supportedNetworkModules.add(new ResourceLocation(moduleTagCompound.getString("module_type")));
		}

		// Serialize the disabled sides.
		for (int i = 0; i < 6; i++) {
			disabledSides[i] = tag.getBoolean("disabled" + i);
		}

		dataTag = tag.getCompound(DATA_TAG_KEY);
	}

	public void tick() {

	}

	public boolean containsProperty(String key) {
		return dataTag.contains(key);
	}

	public int getIntProperty(String key) {
		return dataTag.getInt(key);
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

	public CompoundNBT getTagProperty(String key) {
		return dataTag.getCompound(key);
	}

	public void setProperty(String key, int value) {
		dataTag.putInt(key, value);
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

	public void setProperty(String key, CompoundNBT value) {
		dataTag.put(key, value);
	}

	public CompoundNBT getCompleteDataTag() {
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

	public World getWorld() {
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

	public void onNetworkLeft() {
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
		World.notifyBlockUpdate(Position, state, state, 1 | 2);
	}

	public boolean isDisabledOnSide(Direction side) {
		return disabledSides[side.ordinal()];
	}

	public void setDisabledStateOnSide(Direction side, boolean disabledState) {
		disabledSides[side.ordinal()] = disabledState;
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		// Serialize the position.
		tag.putLong("position", Position.toLong());

		// Serialize the supported module types.
		ListNBT supportedModules = new ListNBT();
		supportedNetworkModules.forEach(moduleType -> {
			CompoundNBT moduleTag = new CompoundNBT();
			moduleTag.putString("module_type", moduleType.toString());
			supportedModules.add(moduleTag);
		});
		tag.put("supported_modules", supportedModules);

		// Serialize the disabled sides.
		for (int i = 0; i < 6; i++) {
			tag.putBoolean("disabled" + i, disabledSides[i]);
		}

		// Serailize the attachments.
		CompoundNBT attachmentTags = new CompoundNBT();
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
		TileEntityBase baseTe = (TileEntityBase) World.getChunkAt(getPos()).getTileEntity(getPos(), Chunk.CreateEntityType.QUEUED);
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
