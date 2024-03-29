package theking530.staticpower.cables.power;

import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerEnergyTracker;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.sided.ISidedStaticPowerStorage;
import theking530.api.energy.sided.SidedStaticPowerCapabilityWrapper;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.destinations.CableDestination;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class PowerCableComponent extends AbstractCableProviderComponent implements ISidedStaticPowerStorage {
	public static final String VOLTAGE_ORDINAL = "power_voltage_ordinal";
	public static final String CURRENT_MAX = "power_max_current";
	public static final String RESISTANCE = "power_resistance";
	public static final String POWER_INDUSTRIAL_DATA_TAG_KEY = "power_cable_industrial";

	private final CableNetworkModuleType moduleType;
	private final SidedStaticPowerCapabilityWrapper capabilityWrapper;
	private final double powerLoss;
	private final double maxCurrent;
	private final StaticPowerVoltage voltage;
	private final boolean isIndustrial;

	public PowerCableComponent(String name, boolean isIndustrial, StaticPowerVoltage voltage, double maxPower,
			double powerLoss) {
		this(name, ModCableModules.Power.get(), isIndustrial, voltage, maxPower, powerLoss);
	}

	public PowerCableComponent(String name, CableNetworkModuleType powerModuleType, boolean isIndustrial,
			StaticPowerVoltage voltage, double maxCurrent, double powerLoss) {
		super(name, powerModuleType);
		capabilityWrapper = new SidedStaticPowerCapabilityWrapper(this);

		this.voltage = voltage;
		this.powerLoss = powerLoss;
		this.maxCurrent = maxCurrent;
		this.isIndustrial = isIndustrial;
		this.moduleType = powerModuleType;
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
		return getNetworkModule(moduleType);
	}

	@Override
	public <T> LazyOptional<T> provideCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityStaticPower.STATIC_VOLT_CAPABILITY) {
			return LazyOptional.of(() -> capabilityWrapper.get(side)).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	protected void initializeCableProperties(Cable cable, BlockPlaceContext context, BlockState state,
			LivingEntity placer, ItemStack stack) {
		super.initializeCableProperties(cable, context, state, placer, stack);
		cable.getDataTag().putByte(VOLTAGE_ORDINAL, (byte) voltage.ordinal());
		cable.getDataTag().putDouble(RESISTANCE, powerLoss);
		cable.getDataTag().putDouble(CURRENT_MAX, maxCurrent);
		cable.getDataTag().putBoolean(POWER_INDUSTRIAL_DATA_TAG_KEY, isIndustrial);
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
	protected void getSupportedDestinationTypes(Set<CableDestination> types) {
		types.add(ModCableDestinations.Power.get());
	}

	@Override
	public boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticVoltageRange.ANY_VOLTAGE;
	}

	@Override
	public double getMaximumPowerInput() {
		return StaticPowerEnergyUtilities.getMaximumPower();
	}

	@Override
	public boolean canAcceptCurrentType(CurrentType type) {
		// Cables don't care about the input type.
		return true;
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
	public StaticPowerVoltage getOutputVoltage() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getOutputVoltage();
			}
		}
		return StaticPowerVoltage.ZERO;
	}

	@Override
	public double getMaximumPowerOutput() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getMaximumPowerOutput();
			}
		}
		return 0;
	}

	@Override
	public CurrentType getOutputCurrentType() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getOutputCurrentType();
			}
		}
		return CurrentType.DIRECT;
	}

	@Override
	public boolean canAcceptExternalPower() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.canAcceptExternalPower();
			}
		}
		return false;
	}

	@Override
	public boolean canOutputExternalPower() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.canOutputExternalPower();
			}
		}
		return true;
	}

	@Override
	public double addPower(PowerStack power, boolean simulate) {
		return 0;
	}

	@Override
	public double addPower(Direction side, PowerStack power, boolean simulate) {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.addPower(getPos().relative(side), getPos(), power, simulate);
			}
		}
		return 0;
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		return PowerStack.EMPTY;
	}

	@Override
	public IStaticPowerEnergyTracker getEnergyTracker() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getEnergyTracker();
			}
		}
		return null;
	}
}
