package theking530.staticpower.cables.power;

import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.sided.ISidedStaticPowerStorage;
import theking530.api.energy.sided.SidedStaticPowerCapabilityWrapper;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.destinations.CableDestination;
import theking530.staticpower.cables.network.destinations.ModCableDestinations;
import theking530.staticpower.cables.network.modules.CableNetworkModuleTypes;

public class PowerCableComponent extends AbstractCableProviderComponent implements ISidedStaticPowerStorage {
	public static final String VOLTAGE_ORDINAL = "power_voltage_ordinal";
	public static final String POWER_MAX = "power_max_power";
	public static final String POWER_LOSS = "power_resistance";
	public static final String POWER_INDUSTRIAL_DATA_TAG_KEY = "power_cable_industrial";

	private final ResourceLocation moduleType;
	private final SidedStaticPowerCapabilityWrapper capabilityWrapper;
	private final double powerLoss;
	private final double maxPower;
	private final StaticPowerVoltage voltage;
	private final boolean isIndustrial;

	public PowerCableComponent(String name, boolean isIndustrial, StaticPowerVoltage voltage, double maxPower, double powerLoss) {
		this(name, CableNetworkModuleTypes.POWER_NETWORK_MODULE, isIndustrial, voltage, maxPower, powerLoss);
	}

	public PowerCableComponent(String name, ResourceLocation powerModuleType, boolean isIndustrial, StaticPowerVoltage voltage, double maxPower, double powerLoss) {
		super(name, powerModuleType);
		capabilityWrapper = new SidedStaticPowerCapabilityWrapper(this);

		this.voltage = voltage;
		this.powerLoss = powerLoss;
		this.maxPower = maxPower;
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
	protected void initializeCableProperties(ServerCable cable, BlockPlaceContext context, BlockState state, LivingEntity placer, ItemStack stack) {
		cable.getDataTag().putByte(VOLTAGE_ORDINAL, (byte) voltage.ordinal());
		cable.getDataTag().putDouble(POWER_LOSS, powerLoss);
		cable.getDataTag().putDouble(POWER_MAX, maxPower);
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
	protected boolean canAttachAttachment(ItemStack attachment) {
		return false;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticVoltageRange.ANY_VOLTAGE;
	}

	@Override
	public double getMaximumPowerInput() {
		return Double.MAX_VALUE;
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
	public double getOutputVoltage() {
		if (!isClientSide()) {
			PowerNetworkModule module = getPowerNetworkModule().orElse(null);
			if (module != null) {
				return module.getOutputVoltage();
			}
		}
		return 0;
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
}
