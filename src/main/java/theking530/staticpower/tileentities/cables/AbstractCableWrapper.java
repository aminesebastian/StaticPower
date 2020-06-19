package theking530.staticpower.tileentities.cables;

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
import net.minecraftforge.common.util.Constants;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.cables.network.CableNetwork;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;

public abstract class AbstractCableWrapper {
	public enum CableConnectionState {
		NONE, CABLE, TILE_ENTITY
	}

	protected CableNetwork Network;
	protected final World World;
	protected final ResourceLocation Type;
	protected final HashSet<ResourceLocation> SupportedNetworkModules;
	private final BlockPos Position;
	private boolean[] DisabledSides;

	public AbstractCableWrapper(World world, BlockPos position, ResourceLocation type, ResourceLocation... supportedModules) {
		Position = position;
		World = world;

		// Capture the types.
		SupportedNetworkModules = new HashSet<ResourceLocation>();
		for (ResourceLocation module : supportedModules) {
			SupportedNetworkModules.add(module);
		}

		Type = type;
		DisabledSides = new boolean[] { false, false, false, false, false, false };
	}

	public AbstractCableWrapper(World world, CompoundNBT tag) {
		// Set the world.
		World = world;

		// Get the type and the position.
		Type = new ResourceLocation(tag.getString("type"));
		Position = BlockPos.fromLong(tag.getLong("position"));

		// Get the supported network types.
		SupportedNetworkModules = new HashSet<ResourceLocation>();
		ListNBT modules = tag.getList("supported_modules", Constants.NBT.TAG_COMPOUND);
		for (INBT moduleTag : modules) {
			CompoundNBT moduleTagCompound = (CompoundNBT) moduleTag;
			SupportedNetworkModules.add(new ResourceLocation(moduleTagCompound.getString("module_type")));
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
		Network = network;
		if (updateBlock) {
			updateCableBlock();
		}
	}
//
//	public boolean hasType(ResourceLocation type) {
//		return Types.contains(type);
//	}

	public ResourceLocation getType() {
		return Type;
	}

	public void onNetworkLeft() {
		Network = null;
		updateCableBlock();
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
		CableNetworkManager.get(World).removeCable(Position);

		DisabledSides[side.ordinal()] = disabledState;

		AbstractCableWrapper opposite = CableNetworkManager.get(World).getCable(Position.offset(side));
		if (opposite != null) {
			CableNetworkManager.get(World).removeCable(Position.offset(side));
			DisabledSides[side.getOpposite().ordinal()] = disabledState;
			CableNetworkManager.get(World).addCable(opposite);
		}

		CableNetworkManager.get(World).addCable(this);
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
	protected List<AbstractCableProviderComponent> getCableProviderComponents() {
		TileEntityBase baseTe = (TileEntityBase) World.getTileEntity(Position);
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
		AbstractCableWrapper cable = (AbstractCableWrapper) other;
		return World.equals(cable.World) && Position.equals(cable.Position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(World, Position);
	}
}
