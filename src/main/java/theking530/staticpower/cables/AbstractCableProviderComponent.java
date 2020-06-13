package theking530.staticpower.cables;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import theking530.staticpower.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.factories.cables.CableWrapperRegistry;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public abstract class AbstractCableProviderComponent extends AbstractTileEntityComponent {
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<boolean[]> DISABLED_CABLE_SIDES = new ModelProperty<>();
	/** KEEP IN MIND: This is purely cosmetic and on the client side. */
	public static final ModelProperty<CableConnectionState[]> CABLE_CONNECTION_STATES = new ModelProperty<>();
	/** The type of this cable. */
	private ResourceLocation Type;
	/**
	 * Keeps track of which sides of the cable are disabled. This value is a client
	 * copy of the master value which exists on the server.
	 */
	private boolean[] DisabledSides;
	/**
	 * Cache for the connection states. This is updated every time a new baked model
	 * is requested AND also, on first placement.
	 */
	protected CableConnectionState[] ConnectionStates;

	public AbstractCableProviderComponent(String name, ResourceLocation type) {
		super(name);
		Type = type;
		DisabledSides = new boolean[] { false, false, false, false, false, false };
		ConnectionStates = new CableConnectionState[] { CableConnectionState.NONE, CableConnectionState.NONE, CableConnectionState.NONE, CableConnectionState.NONE, CableConnectionState.NONE,
				CableConnectionState.NONE };
	}

	/**
	 * Gets the type of cable this provider should create.
	 * 
	 * @return
	 */
	public ResourceLocation getCableType() {
		return Type;
	}

	/**
	 * Checks to see if the provided side is disabled. This should only be called on
	 * the client. The server should query the {@link AbstractCableWrapper}
	 * directly.
	 * 
	 * @param side
	 * @return
	 */
	public boolean isSideDisabled(Direction side) {
		return DisabledSides[side.ordinal()];
	}

	public void setSideDisabledState(Direction side, boolean disabledState) {
		DisabledSides[side.ordinal()] = disabledState;
	}

	/**
	 * Gets the connection state on the provided side.
	 * 
	 * @param side
	 * @return
	 */
	public CableConnectionState getConnectionState(Direction side) {
		return ConnectionStates[side.ordinal()];
	}

	/**
	 * Allows us to provide additional data about state of the cable for rendering
	 * purposes.
	 */
	@Override
	public void getModelData(ModelDataMap.Builder builder) {
		updateCachedConnectionStates();
		builder.withInitial(DISABLED_CABLE_SIDES, DisabledSides).withInitial(CABLE_CONNECTION_STATES, ConnectionStates);
	}

	/**
	 * When the owning tile entity is validated, we check to see if there is a cable
	 * wrapper in the network for this cable. If not, we provide one.
	 */
	@Override
	public void onOwningTileEntityValidate() {
		super.onOwningTileEntityValidate();
		// If we're on the server, check to see if the cable network manager for this
		// world is tracking a cable at this position. If it is not, add this cable for
		// tracking.
		if (!getTileEntity().getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
			if (!manager.isTrackingCable(getTileEntity().getPos())) {
				AbstractCableWrapper wrapper = CableWrapperRegistry.get().create(Type, getTileEntity().getWorld(), getTileEntity().getPos());
				if (wrapper != null) {
					manager.addCable(wrapper);
				} else {
					throw new RuntimeException(String.format("Cable supplier for TileEntity at Position: %1$s supplied a null CableWrapper.", getTileEntity().getPos()));
				}
			}
		}
	}

	@Override
	public void updatePostPlacement(BlockState state, Direction direction, BlockState facingState, BlockPos FacingPos) {
		updateCachedConnectionStates();
	}

	/**
	 * When the owning tile entity is removed from the world, we remove the cable
	 * wrapper for this tile entity as well.
	 */
	@Override
	public void onOwningTileEntityRemoved() {
		super.onOwningTileEntityRemoved();
		// If we're on the server, get the cable manager and remove the cable at the
		// current position.
		if (!getTileEntity().getWorld().isRemote) {
			CableNetworkManager manager = CableNetworkManager.get(getTileEntity().getWorld());
			manager.removeCable(getTileEntity().getPos());
		}
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		super.serializeUpdateNbt(nbt);

		// Serialize the disabled states.
		for (int i = 0; i < DisabledSides.length; i++) {
			nbt.putBoolean("disabledState" + i, DisabledSides[i]);
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt) {
		super.deserializeUpdateNbt(nbt);

		// Deserialize the disabled states.
		for (int i = 0; i < DisabledSides.length; i++) {
			DisabledSides[i] = nbt.getBoolean("disabledState" + i);
		}
	}

	protected void updateCachedConnectionStates() {
		for (Direction dir : Direction.values()) {
			ConnectionStates[dir.ordinal()] = cacheConnectionState(dir, getTileEntity().getPos().offset(dir));
		}
	}

	protected abstract CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition);
}
