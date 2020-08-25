package theking530.staticpower.cables.power;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.energy.CapabilityStaticVolt;
import theking530.staticpower.energy.IStaticVoltHandler;
import theking530.staticpower.energy.PowerEnergyInterface;
import theking530.staticpower.energy.StaticVoltAutoConverter;
import theking530.staticpower.energy.StaticVoltHandler;
import theking530.staticpower.utilities.MetricConverter;

public class PowerNetworkModule extends AbstractCableNetworkModule {
	private final StaticVoltAutoConverter energyInterface;
	private final StaticVoltHandler EnergyStorage;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		EnergyStorage = new StaticVoltHandler(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		// No one should extract power from the network, we only provide it.
		EnergyStorage.setCanDrain(false);
		// Create the interface.
		energyInterface = new StaticVoltAutoConverter(EnergyStorage);
	}

	@Override
	public void tick(World world) {
		// Check to make sure we have power and valid desinations.
		if (EnergyStorage.getStoredPower() > 0 && Network.getGraph().getDestinations().size() > 0) {
			// Get a map of all the applicable destination that support recieveing power.
			HashMap<PowerEnergyInterface, DestinationWrapper> destinations = new HashMap<PowerEnergyInterface, DestinationWrapper>();
			Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
				PowerEnergyInterface powerInterface = getInterfaceForDesination(wrapper);
				if (powerInterface != null) {
					destinations.put(powerInterface, wrapper);
				}
			});

			// If there are no valid destinations, return early.
			if (destinations.size() == 0) {
				return;
			}

			// Calculate how we should split the output amount.
			int outputPerDestination = Math.max(1, EnergyStorage.getStoredPower() / destinations.size());

			// Distribute the power to the destinations.
			for (PowerEnergyInterface powerInterface : destinations.keySet()) {
				if (powerInterface != null) {
					if (powerInterface.canRecievePower()) {
						// Get the amount of power we can supply.
						int toSupply = Math.min(
								CableNetworkManager.get(Network.getWorld()).getCable(destinations.get(powerInterface).getConnectedCable()).getIntProperty(PowerCableComponent.POWER_RATE_DATA_TAG_KEY),
								outputPerDestination);

						// Supply the power.
						int supplied = powerInterface.receivePower(Math.min(toSupply, EnergyStorage.getCurrentMaximumPowerOutput()), false);

						// If we supplied any power, extract the power.
						if (supplied > 0) {
							EnergyStorage.setCanDrain(true);
							EnergyStorage.drainPower(supplied, false);
							EnergyStorage.setCanDrain(false);
						}
					}
				}
			}
		}

	}

	public void onNetworksJoined(CableNetwork other) {
		if (other.hasModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {
			PowerNetworkModule module = (PowerNetworkModule) other.getModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
			module.EnergyStorage.addPowerIgnoreTransferRate(EnergyStorage.getStoredPower());
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		// Calculate the total capacity.
		int capacity = mapper.getDiscoveredCables().stream().filter(p -> p.supportsNetworkModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE))
				.mapToInt(p -> p.getIntProperty(PowerCableComponent.POWER_CAPACITY_DATA_TAG_KEY)).sum();

		// If the capacity is less than 0, that means we overflowed. Set the capcaity to
		// the maximum integer value.
		EnergyStorage.setCapacity(capacity < 0 ? Integer.MAX_VALUE : (int) capacity);
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		EnergyStorage.deserializeNBT(tag.getCompound("energy_storage"));
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.put("energy_storage", EnergyStorage.serializeNBT());
		return tag;
	}

	public StaticVoltAutoConverter getEnergyStorage() {
		return energyInterface;
	}

	@Nullable
	public PowerEnergyInterface getInterfaceForDesination(DestinationWrapper wrapper) {
		if (wrapper.supportsType(DestinationType.POWER)) {
			IStaticVoltHandler powerStorage = wrapper.getTileEntity().getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY, wrapper.getDestinationSide()).orElse(null);
			if (powerStorage != null && powerStorage.receivePower(Integer.MAX_VALUE, true) > 0) {
				return new PowerEnergyInterface(powerStorage);
			}
		} else if (wrapper.supportsType(DestinationType.FORGE_POWER)) {
			IEnergyStorage energyStorage = wrapper.getTileEntity().getCapability(CapabilityEnergy.ENERGY, wrapper.getDestinationSide()).orElse(null);
			if (energyStorage != null && energyStorage.receiveEnergy(Integer.MAX_VALUE, true) > 0) {
				return new PowerEnergyInterface(energyStorage);
			}
		}
		return null;
	}

	@Override
	public void getReaderOutput(List<ITextComponent> output) {
		String storedEnergy = new MetricConverter(getEnergyStorage().getStoredPower()).getValueAsString(true);
		String maximumEnergy = new MetricConverter(getEnergyStorage().getCapacity()).getValueAsString(true);
		output.add(new StringTextComponent(String.format("Contains: %1$sRF out of a maximum of %2$sRF.", storedEnergy, maximumEnergy)));
	}
}
