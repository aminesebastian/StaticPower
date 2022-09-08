package theking530.staticpower.cables.power;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.staticpower.blockentities.components.energy.SidedEnergyProxy;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.CableUtilities;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.ServerCable.CableConnectionState;

public class PowerCableComponent extends AbstractCableProviderComponent implements IStaticPowerStorage {
	public static final String POWER_MAX_CURRENT = "power_max_current";
	public static final String POWER_RESISTANCE = "power_resistance";
	public static final String POWER_INDUSTRIAL_DATA_TAG_KEY = "power_cable_industrial";

	private final Map<Direction, SidedEnergyProxy> powerInterfaces;
	private final double resistance;
	private final double maxCurrent;
	private final boolean isIndustrial;

	public PowerCableComponent(String name, boolean isIndustrial, double maxCurrent, double resistance) {
		super(name, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		powerInterfaces = new HashMap<>();
		for (Direction dir : Direction.values()) {
			powerInterfaces.put(dir, new SidedEnergyProxy(dir, this) {
				@Override
				public double addPower(Direction side, double voltage, double power, boolean simulate) {
					return PowerCableComponent.this.addPower(side, voltage, power, simulate);
				}
			});
		}

		this.resistance = resistance;
		this.maxCurrent = maxCurrent;
		this.isIndustrial = isIndustrial;
	}

	/**
	 * Gets the power network module for the network this cable belongs to. We have
	 * to wrap it in an optional because while we can guarantee once this component
	 * is validated that the network is valid, since this component exposes external
	 * methods, other tile entity that are made valid before us may call some of our
	 * methods.
	 * 
	 * @return
	 */
	protected Optional<PowerNetworkModule> getPowerNetworkModule() {
		return getNetworkModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			if (side == null) {
				return LazyOptional.of(() -> this).cast();
			} else if (!isSideDisabled(side)) {
				return LazyOptional.of(() -> powerInterfaces.get(side)).cast();
			}
		}
		return LazyOptional.empty();
	}

	@Override
	protected void initializeCableProperties(ServerCable cable) {
		cable.setProperty(POWER_RESISTANCE, resistance);
		cable.setProperty(POWER_MAX_CURRENT, maxCurrent);
		cable.setProperty(POWER_INDUSTRIAL_DATA_TAG_KEY, isIndustrial);
	}

	@Override
	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.serializeUpdateNbt(nbt, fromUpdate);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		super.deserializeUpdateNbt(nbt, fromUpdate);
	}

	@Override
	protected CableConnectionState getUncachedConnectionState(Direction side, @Nullable BlockEntity te, BlockPos blockPosition, boolean firstWorldLoaded) {
		AbstractCableProviderComponent otherProvider = CableUtilities.getCableWrapperComponent(getLevel(), blockPosition);
		if (otherProvider != null && otherProvider.areCableCompatible(this, side)) {
			if (!otherProvider.isSideDisabled(side.getOpposite())) {
				return CableConnectionState.CABLE;
			}
		}
		if (te != null) {
			if (te.getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticVoltageRange.ANY_VOLTAGE;
	}

	@Override
	public double getMaximumCurrentInput() {
		return Double.MAX_VALUE;
	}

	@Override
	public double getStoredPower() {
		return 0;
	}

	@Override
	public double getCapacity() {
		return 0;
	}

	@Override
	public double getVoltageOutput() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getVoltageOutput();
			}
		}
		return 0;
	}

	@Override
	public double getMaximumCurrentOutput() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getMaximumCurrentOutput();
			}
		}
		return 0;
	}

	@Override
	public double addPower(double voltage, double current, boolean simulate) {
		return 0;
	}

	public double addPower(Direction side, double voltage, double power, boolean simulate) {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.addPower(getPos().relative(side), getPos(), voltage, power, simulate);
			}
		}
		return 0;
	}

	@Override
	public double drainPower(double power, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canAcceptPower() {
		return true;
	}

	@Override
	public boolean doesProvidePower() {
		return true;
	}
}
