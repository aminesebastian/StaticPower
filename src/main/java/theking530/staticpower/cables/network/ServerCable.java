package theking530.staticpower.cables.network;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

	protected CableNetwork Network;
	protected final World World;
	protected final HashSet<ResourceLocation> SupportedNetworkModules;
	private final BlockPos Position;
	private final boolean[] DisabledSides;

	public ServerCable(World world, BlockPos position, HashSet<ResourceLocation> supportedModules) {
		Position = position;
		World = world;

		// Capture the types.
		SupportedNetworkModules = supportedModules;

		DisabledSides = new boolean[] { false, false, false, false, false, false };
	}

	public ServerCable(World world, CompoundNBT tag) {
		// Set the world.
		World = world;

		// Get the position.
		Position = BlockPos.fromLong(tag.getLong("position"));

		// Create the disabled sides.
		DisabledSides = new boolean[] { false, false, false, false, false, false };

		// Get the supported network types.
		SupportedNetworkModules = new HashSet<ResourceLocation>();
		ListNBT modules = tag.getList("supported_modules", Constants.NBT.TAG_COMPOUND);
		for (INBT moduleTag : modules) {
			CompoundNBT moduleTagCompound = (CompoundNBT) moduleTag;
			SupportedNetworkModules.add(new ResourceLocation(moduleTagCompound.getString("module_type")));
		}

		// Serialize the disabled sides.
		for (int i = 0; i < 6; i++) {
			DisabledSides[i] = tag.getBoolean("disabled" + i);
		}
	}

	public void tick() {

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
		for (ResourceLocation moduleType : SupportedNetworkModules) {
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
		return SupportedNetworkModules;
	}

	public boolean supportsNetworkModule(ResourceLocation moduleType) {
		return SupportedNetworkModules.contains(moduleType);
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
		return DisabledSides[side.ordinal()];
	}

	public void setDisabledStateOnSide(Direction side, boolean disabledState) {
		DisabledSides[side.ordinal()] = disabledState;
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		// Serialize the position.
		tag.putLong("position", Position.toLong());

		// Serialize the supported module types.
		ListNBT supportedModules = new ListNBT();
		SupportedNetworkModules.forEach(moduleType -> {
			CompoundNBT moduleTag = new CompoundNBT();
			moduleTag.putString("module_type", moduleType.toString());
			supportedModules.add(moduleTag);
		});
		tag.put("supported_modules", supportedModules);

		// Serialize the disabled sides.
		for (int i = 0; i < 6; i++) {
			tag.putBoolean("disabled" + i, DisabledSides[i]);
		}
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
