package theking530.staticpower.tileentities.components.energy;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.consumer.CapabilityStaticPower;
import theking530.api.energy.consumer.IStaticPowerStorage;
import theking530.api.energy.consumer.StaticPowerStorage;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.serialization.UpdateSerialize;

public class PowerStorageComponent extends AbstractTileEntityComponent implements IStaticPowerStorage {

	@UpdateSerialize
	private final StaticPowerStorage storage;
	private final Map<Direction, SidedEnergyProxy> powerInterfaces;
	private SideConfigurationComponent sideConfig;
	private boolean exposeAsCapability;

	public PowerStorageComponent(String name, double minimumVoltage, double maximumVoltage, double capacity) {
		this(name, minimumVoltage, maximumVoltage, capacity, 0);
	}

	public PowerStorageComponent(String name, double minimumVoltage, double maximumVoltage, double capacity, double voltageOutput) {
		super(name);
		exposeAsCapability = true;
		storage = new StaticPowerStorage(new StaticVoltageRange(minimumVoltage, maximumVoltage), capacity, voltageOutput);
		powerInterfaces = new HashMap<>();
		for (Direction dir : Direction.values()) {
			powerInterfaces.put(dir, new SidedEnergyProxy(dir, this) {
				@Override
				public boolean canAcceptPower(Direction side) {
					if (sideConfig != null) {
						return sideConfig.getWorldSpaceDirectionConfiguration(side).isInputMode();
					}
					return true;
				}

				@Override
				public boolean doesProvidePower(Direction side) {
					if (sideConfig != null) {
						return sideConfig.getWorldSpaceDirectionConfiguration(side).isOutputMode();
					}
					return true;
				}
			});
		}
	}

	public PowerStorageComponent setSideConfiguration(SideConfigurationComponent sideConfig) {
		this.sideConfig = sideConfig;
		return this;
	}

	public PowerStorageComponent setCapacity(double capacity) {
		storage.setCapacity(capacity);
		return this;
	}

	public PowerStorageComponent setOutputVoltage(double voltageOutput) {
		storage.setOutputVoltage(voltageOutput);
		return this;
	}

	public PowerStorageComponent setExposeAsCapability(boolean exposeAsCapability) {
		this.exposeAsCapability = exposeAsCapability;
		return this;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return storage.getInputVoltageRange();
	}

	@Override
	public double getStoredPower() {
		return storage.getStoredPower();
	}

	@Override
	public double getCapacity() {
		return storage.getCapacity();
	}

	@Override
	public double getVoltageOutput() {
		return storage.getVoltageOutput();
	}

	@Override
	public double addPower(double voltage, double power, boolean simulate) {
		return storage.addPower(voltage, power, simulate);
	}

	@Override
	public double usePower(double power, boolean simulate) {
		return storage.usePower(power, simulate);
	}

	@Override
	public boolean canAcceptPower() {
		return storage.canAcceptPower();
	}

	@Override
	public boolean doesProvidePower() {
		return storage.doesProvidePower();
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Still expose even if exposeAsCapability is false if the side is null. This is
		// used for JADE and other overlays.
		if (isEnabled() && (side == null || exposeAsCapability)) {
			return manuallyGetCapability(cap, side);
		}

		return LazyOptional.empty();
	}

	public <T> LazyOptional<T> manuallyGetCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			if (side == null) {
				return LazyOptional.of(() -> this).cast();
			} else {
				return LazyOptional.of(() -> powerInterfaces.get(side)).cast();
			}
		}
		return LazyOptional.empty();
	}
}
