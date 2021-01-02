package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import theking530.api.power.CapabilityStaticVolt;
import theking530.api.power.IStaticVoltHandler;
import theking530.api.power.PowerEnergyInterface;
import theking530.api.power.StaticVoltAutoConverter;
import theking530.api.power.StaticVoltHandler;
import theking530.staticcore.utilities.SDTime;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.utilities.MetricConverter;

public class PowerNetworkModule extends AbstractCableNetworkModule {
	public static final int MAX_METRIC_SAMPLES = 60;

	private final StaticVoltAutoConverter energyInterface;
	private final StaticVoltHandler storage;
	private TransferMetrics secondsDelta;
	private TransferMetrics minutesDelta;
	private TransferMetrics hoursDelta;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		storage = new StaticVoltHandler(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		// No one should extract power from the network, we only provide it.
		storage.setCanDrain(false);
		// Create the interface.
		energyInterface = new StaticVoltAutoConverter(storage);

		// Create the metric capturing values.
		secondsDelta = new TransferMetrics();
		minutesDelta = new TransferMetrics();
		hoursDelta = new TransferMetrics();
	}

	@Override
	public void tick(World world) {
		// Check to make sure we have power and valid desinations.
		if (storage.getStoredPower() > 0 && Network.getGraph().getDestinations().size() > 0) {
			// Get a map of all the applicable destination that support recieveing power.
			List<PowerEnergyInterfaceWrapper> destinations = new ArrayList<PowerEnergyInterfaceWrapper>();

			Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
				// Get the cable and skip if its industrial.
				ServerCable cable = CableNetworkManager.get(world).getCable(wrapper.getFirstConnectedCable());
				if (cable.getBooleanProperty(PowerCableComponent.POWER_INDUSTRIAL_DATA_TAG_KEY)) {
					return;
				}

				// Add all the power interfaces.
				List<PowerEnergyInterfaceWrapper> powerInterfaces = getInterfaceForDesination(wrapper);
				if (powerInterfaces.size() > 0) {
					destinations.addAll(powerInterfaces);
				}
			});

			// If there are no valid destinations, return early.
			if (destinations.size() == 0) {
				return;
			}

			// Calculate how we should split the output amount.
			int outputPerDestination = Math.max(1, storage.getStoredPower() / destinations.size());

			// Distribute the power to the destinations.
			for (PowerEnergyInterfaceWrapper powerWrapper : destinations) {
				// Get the amount of power we can supply.
				int toSupply = Math.min(CableNetworkManager.get(Network.getWorld()).getCable(powerWrapper.cablePos).getIntProperty(PowerCableComponent.POWER_RATE_DATA_TAG_KEY),
						outputPerDestination);

				// Supply the power.
				int supplied = powerWrapper.powerInterface.receivePower(Math.min(toSupply, storage.getCurrentMaximumPowerOutput()), false);

				// If we supplied any power, extract the power.
				if (supplied > 0) {
					storage.setCanDrain(true);
					storage.drainPower(supplied, false);
					storage.setCanDrain(false);
				}
			}
		}

		// Capture metrics.
		storage.captureEnergyMetric();

		// Capture the seconds metric.
		if ((world.getGameTime() % SDTime.TICKS_PER_SECOND) == 0) {
			secondsDelta.addMetrics(storage.getReceivedPerTick(), storage.getExtractedPerTick());
		}

		// Capture the minutes metric.
		if ((world.getGameTime() % SDTime.TICKS_PER_MINUTE) == 0) {
			minutesDelta.addMetrics(storage.getReceivedPerTick(), storage.getExtractedPerTick());
		}

		// Capture the hours metric.
		if ((world.getGameTime() % SDTime.TICKS_PER_HOUR) == 0) {
			hoursDelta.addMetrics(storage.getReceivedPerTick(), storage.getExtractedPerTick());
		}
	}

	public TransferMetrics getSecondsMetrics() {
		return this.secondsDelta;
	}

	public TransferMetrics getMinutesMetrics() {
		return this.minutesDelta;
	}

	public TransferMetrics getHoursMetrics() {
		return this.hoursDelta;
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
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		// Allocate a hash map to contain all the values.
		HashMap<Integer, Integer> averageMap = new HashMap<Integer, Integer>();

		// Aggregate the counts for each type of cable.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Skip non-power cables.
			if (!cable.supportsNetworkModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {
				continue;
			}

			// Get the capacity of the cable, add it to the map, and increase the count.
			int capacity = cable.getIntProperty(PowerCableComponent.POWER_CAPACITY_DATA_TAG_KEY);
			if (!averageMap.containsKey(capacity)) {
				averageMap.put(capacity, 1);
			} else {
				averageMap.put(capacity, averageMap.get(capacity) + 1);
			}
		}

		// Calculate the weighted average.
		int average = 0;
		for (Integer key : averageMap.keySet()) {
			average += key * (averageMap.get(key) / (float) mapper.getDiscoveredCables().size());
		}

		// If the capacity is less than 0, that means we overflowed. Set the capcaity to
		// the maximum integer value.
		storage.setCapacity(average < 0 ? Integer.MAX_VALUE : (int) average);
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		storage.deserializeNBT(tag.getCompound("energy_storage"));
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.put("energy_storage", storage.serializeNBT());
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
	public void getReaderOutput(List<ITextComponent> output) {
		String storedEnergy = new MetricConverter(getEnergyAutoConverter().getStoredPower()).getValueAsString(true);
		String maximumEnergy = new MetricConverter(getEnergyAutoConverter().getCapacity()).getValueAsString(true);
		output.add(new StringTextComponent(String.format("Contains: %1$sRF out of a maximum of %2$sRF.", storedEnergy, maximumEnergy)));
	}

	protected class PowerEnergyInterfaceWrapper {
		protected PowerEnergyInterface powerInterface;
		protected BlockPos cablePos;

		protected PowerEnergyInterfaceWrapper(PowerEnergyInterface powerInterface, BlockPos cablePos) {
			this.powerInterface = powerInterface;
			this.cablePos = cablePos;
		}
	}

	protected class TransferMetrics {
		private final Queue<Float> received;
		private final Queue<Float> provided;

		/**
		 * @param received
		 * @param provided
		 */
		public TransferMetrics() {
			this.received = new LinkedList<Float>();
			this.provided = new LinkedList<Float>();
		}

		public void addMetrics(float received, float provided) {
			// Add the values.
			this.received.add(received);
			this.provided.add(provided);

			// Make sure our queues only keep the correct values.
			if (this.received.size() > MAX_METRIC_SAMPLES) {
				this.received.poll();
			}

			if (this.provided.size() > MAX_METRIC_SAMPLES) {
				this.provided.poll();
			}
		}
	}
}
