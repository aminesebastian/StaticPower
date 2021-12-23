package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.api.power.PowerEnergyInterface;
import theking530.api.power.StaticVoltAutoConverter;
import theking530.api.power.StaticVoltHandler;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.tileentities.components.power.PowerTransferMetrics;
import theking530.staticpower.utilities.MetricConverter;

public class PowerNetworkModule extends AbstractCableNetworkModule {
	private final StaticVoltAutoConverter energyInterface;
	private final StaticVoltHandler storage;
	private PowerTransferMetrics metrics;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		storage = new StaticVoltHandler(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		// No one should extract power from the network, we only provide it.
		storage.setCanDrain(false);
		// Create the interface.
		energyInterface = new StaticVoltAutoConverter(storage);

		// Create the metric capturing values.
		metrics = new PowerTransferMetrics();
	}

	@Override
	public void tick(Level world) {
		// Check to make sure we have power and valid desinations.
		if (storage.getStoredPower() > 0 && Network.getGraph().getDestinations().size() > 0) {
			// Get a map of all the applicable destination that support recieveing power.
			List<PowerEnergyInterfaceWrapper> destinations = new ArrayList<PowerEnergyInterfaceWrapper>();

			Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
				// Get the cable and skip if its industrial.
				ServerCable cable = CableNetworkManager.get(world).getCable(wrapper.getFirstConnectedCable());
				if (cable == null || cable.getBooleanProperty(PowerCableComponent.POWER_INDUSTRIAL_DATA_TAG_KEY)) {
					return;
				}

				// Add all the power interfaces.
				List<PowerEnergyInterfaceWrapper> powerInterfaces = getInterfaceForDesination(wrapper);
				if (powerInterfaces.size() > 0) {
					destinations.addAll(powerInterfaces);
				}
			});

			// If there are no valid destinations, return early.
			if (destinations.size() > 0) {
				// Calculate how we should split the output amount.
				long outputPerDestination = Math.max(1, storage.getStoredPower() / destinations.size());

				// Distribute the power to the destinations.
				for (PowerEnergyInterfaceWrapper powerWrapper : destinations) {
					// Get the amount of power we can supply.
					long toSupply = Math.min(CableNetworkManager.get(Network.getWorld()).getCable(powerWrapper.cablePos).getLongProperty(PowerCableComponent.POWER_RATE_DATA_TAG_KEY),
							outputPerDestination);

					// Supply the power.
					long supplied = powerWrapper.powerInterface.receivePower(Math.min(toSupply, storage.getCurrentMaximumPowerOutput()), false);

					// If we supplied any power, extract the power.
					if (supplied > 0) {
						storage.setCanDrain(true);
						storage.drainPower(supplied, false);
						storage.setCanDrain(false);
					}
				}
			}
		}

		// Capture metrics.
		storage.captureEnergyMetric();

		// Capture time metrics.
		metrics.addMetric(storage.getReceivedPerTick(), storage.getExtractedPerTick());
	}

	public PowerTransferMetrics getMetrics() {
		return metrics;
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		if (other.hasModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {
			PowerNetworkModule module = (PowerNetworkModule) other.getModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
			module.storage.addPowerIgnoreTransferRate(storage.getStoredPower());
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		// Allocate a hash map to contain all the values.
		HashMap<Long, Integer> averageMap = new HashMap<Long, Integer>();

		// Aggregate the counts for each type of cable.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Skip non-power cables.
			if (!cable.supportsNetworkModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {
				continue;
			}

			// Get the capacity of the cable, add it to the map, and increase the count.
			long capacity = cable.getLongProperty(PowerCableComponent.POWER_CAPACITY_DATA_TAG_KEY);
			if (!averageMap.containsKey(capacity)) {
				averageMap.put(capacity, 1);
			} else {
				averageMap.put(capacity, averageMap.get(capacity) + 1);
			}
		}

		// Calculate the weighted average.
		long average = 0;
		for (Long key : averageMap.keySet()) {
			average += key * (averageMap.get(key) / (float) mapper.getDiscoveredCables().size());
		}

		// If the capacity is less than 0, that means we overflowed. Set the capcaity to
		// the maximum integer value.
		storage.setCapacity(average < 0 ? Integer.MAX_VALUE : (int) average);
		storage.setMaxExtract(storage.getCapacity());
		storage.setMaxReceive(storage.getCapacity());
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		storage.deserializeNBT(tag.getCompound("energy_storage"));

		metrics = new PowerTransferMetrics();
		metrics.deserializeNBT(tag.getCompound("metrics"));
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		tag.put("energy_storage", storage.serializeNBT());
		tag.put("metrics", metrics.serializeNBT());
		return tag;
	}

	public StaticVoltAutoConverter getEnergyAutoConverter() {
		return energyInterface;
	}

	public StaticVoltHandler getEnergyStorage() {
		return storage;
	}

	@Nullable
	public List<PowerEnergyInterfaceWrapper> getInterfaceForDesination(DestinationWrapper wrapper) {
		// Allocate the output.
		List<PowerEnergyInterfaceWrapper> output = new ArrayList<PowerEnergyInterfaceWrapper>();

		// Iterate through all the connected cables.
		for (BlockPos cablePos : wrapper.getConnectedCables().keySet()) {
			// Get the cable pos.
			Direction connectedSide = wrapper.getConnectedCables().get(cablePos);

			// Skip NON tile entity destinations.
			if (!wrapper.hasTileEntity()) {
				continue;
			}

			if (wrapper.supportsType(DestinationType.POWER)) {
				IStaticVoltHandler powerStorage = wrapper.getTileEntity().getCapability(CapabilityStaticVolt.STATIC_VOLT_CAPABILITY, connectedSide).orElse(null);
				if (powerStorage != null && powerStorage.canRecievePower() && powerStorage.receivePower(Integer.MAX_VALUE, true) > 0) {
					output.add(new PowerEnergyInterfaceWrapper(new PowerEnergyInterface(powerStorage), cablePos));
				}
			} else if (wrapper.supportsType(DestinationType.FORGE_POWER)) {
				IEnergyStorage energyStorage = wrapper.getTileEntity().getCapability(CapabilityEnergy.ENERGY, connectedSide).orElse(null);
				if (energyStorage != null && energyStorage.canReceive() && energyStorage.receiveEnergy(Integer.MAX_VALUE, true) > 0) {
					output.add(new PowerEnergyInterfaceWrapper(new PowerEnergyInterface(energyStorage), cablePos));
				}
			}
		}

		return output;
	}

	@Override
	public void getReaderOutput(List<Component> output) {
		String storedEnergy = new MetricConverter(getEnergyAutoConverter().getStoredPower()).getValueAsString(true);
		String maximumEnergy = new MetricConverter(getEnergyAutoConverter().getCapacity()).getValueAsString(true);
		output.add(new TextComponent(String.format("Contains: %1$sRF out of a maximum of %2$sRF.", storedEnergy, maximumEnergy)));
	}

	protected class PowerEnergyInterfaceWrapper {
		protected PowerEnergyInterface powerInterface;
		protected BlockPos cablePos;

		protected PowerEnergyInterfaceWrapper(PowerEnergyInterface powerInterface, BlockPos cablePos) {
			this.powerInterface = powerInterface;
			this.cablePos = cablePos;
		}
	}
}
