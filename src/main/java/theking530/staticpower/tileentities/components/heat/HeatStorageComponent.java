package theking530.staticpower.tileentities.components.heat;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.common.utilities.TriFunction;
import theking530.staticpower.network.StaticPowerMessageHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;

public class HeatStorageComponent extends AbstractTileEntityComponent {
	public enum HeatManipulationAction {
		COOL, HEAT
	}

	protected TriFunction<Float, Direction, HeatManipulationAction, Boolean> filter;
	public static final float ENERGY_SYNC_MAX_DELTA = 1;
	protected final HeatStorage heatStorage;
	private float lastSyncHeat;
	private float thermalConductivity;

	private HeatComponentCapabilityAccess capabilityAccessor;

	public HeatStorageComponent(String name, float maxHeat, float maxTransferRate, float thermalConductivity) {
		this(name, maxHeat, maxTransferRate);
		this.thermalConductivity = thermalConductivity;
	}

	public HeatStorageComponent(String name, float maxHeat, float maxTransferRate) {
		super(name);
		heatStorage = new HeatStorage(maxHeat, maxTransferRate);
		capabilityAccessor = new HeatComponentCapabilityAccess();
		lastSyncHeat = 0.0f;
		thermalConductivity = 1.0f;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing on the client.
		if (getWorld().isRemote) {
			return;
		}

		// Cool off the heat storage.
		heatStorage.transferWithSurroundings(getWorld(), getPos(), thermalConductivity);
	}

	@Override
	public void postProcessUpdate() {
		if (!getWorld().isRemote) {
			// Get the current delta between the amount of power we have and the power we
			// had last tick.
			float delta = Math.abs(heatStorage.getCurrentHeat() - lastSyncHeat);

			// Determine if we should sync.
			boolean shouldSync = delta > ENERGY_SYNC_MAX_DELTA;
			shouldSync |= heatStorage.getCurrentHeat() == 0 && lastSyncHeat != 0;
			shouldSync |= heatStorage.getCurrentHeat() == heatStorage.getMaximumHeat() && lastSyncHeat != heatStorage.getMaximumHeat();

			// If we should sync, perform the sync.
			if (shouldSync) {
				lastSyncHeat = heatStorage.getCurrentHeat();
				syncToClient();
			}
			heatStorage.captureHeatTransferMetric();
		}
	}

	/**
	 * Gets the raw heat storage object.
	 * 
	 * @return
	 */
	public HeatStorage getStorage() {
		return heatStorage;
	}

	public float getThermalConductivity() {
		return thermalConductivity;
	}

	public void setThermalConductivity(float thermalConductivity) {
		this.thermalConductivity = thermalConductivity;
	}

	/**
	 * This method syncs the current state of this energy storage component to all
	 * clients within a 64 block radius.
	 */
	public void syncToClient() {
		if (!getWorld().isRemote) {
			PacketHeatStorageComponent syncPacket = new PacketHeatStorageComponent(this, getPos(), this.getComponentName());
			StaticPowerMessageHandler.sendMessageToPlayerInArea(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, getWorld(), getPos(), 64, syncPacket);
		} else {
			throw new RuntimeException("This method should only be called on the server!");
		}

	}

	/**
	 * Sets the filter used to restrict access to this component through
	 * capabilities. Use this to prevent certain actions from the capability access
	 * (ie. make it so external accessor cannot extract heat). No need to check side
	 * configurations (ex. if a side is disabled) here as the heat component
	 * automatically picks those up.
	 * 
	 * @param filter
	 * @return
	 */
	public HeatStorageComponent setCapabiltiyFilter(TriFunction<Float, Direction, HeatManipulationAction, Boolean> filter) {
		this.filter = filter;
		return this;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
		heatStorage.deserializeNBT(nbt.getCompound("heat_storage"));
		thermalConductivity = nbt.getFloat("thermal_conductivity");
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		nbt.put("heat_storage", heatStorage.serializeNBT());
		nbt.putFloat("thermal_conductivity", thermalConductivity);
		return nbt;
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		if (isEnabled()) {
			if (cap == CapabilityHeatable.HEAT_STORAGE_CAPABILITY) {
				capabilityAccessor.currentSide = side;
				return LazyOptional.of(() -> capabilityAccessor).cast();
			}
		}

		return LazyOptional.empty();
	}

	private class HeatComponentCapabilityAccess implements IHeatStorage {
		protected Direction currentSide;

		@Override
		public float heat(float amountToHeat, boolean simulate) {
			if (HeatStorageComponent.this.filter != null && !HeatStorageComponent.this.filter.apply(amountToHeat, currentSide, HeatManipulationAction.HEAT)) {
				return 0.0f;
			}
			return HeatStorageComponent.this.getStorage().heat(amountToHeat, simulate);
		}

		@Override
		public float cool(float amountToCool, boolean simulate) {
			if (HeatStorageComponent.this.filter != null && !HeatStorageComponent.this.filter.apply(amountToCool, currentSide, HeatManipulationAction.COOL)) {
				return 0.0f;
			}
			return HeatStorageComponent.this.getStorage().cool(amountToCool, simulate);
		}

		@Override
		public float getCurrentHeat() {
			return HeatStorageComponent.this.getStorage().getCurrentHeat();
		}

		@Override
		public float getMaximumHeat() {
			return HeatStorageComponent.this.getStorage().getMaximumHeat();
		}

		@Override
		public float getMaximumHeatTransferRate() {
			return HeatStorageComponent.this.getStorage().getMaximumHeatTransferRate();
		}
	}
}
