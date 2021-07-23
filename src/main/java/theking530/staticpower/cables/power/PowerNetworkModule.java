package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
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
	private TransferMetrics secondsMetrics;
	private TransferMetrics minuteMetrics;
	private TransferMetrics hourlyMetrics;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		storage = new StaticVoltHandler(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		// No one should extract power from the network, we only provide it.
		storage.setCanDrain(false);
		// Create the interface.
		energyInterface = new StaticVoltAutoConverter(storage);

		// Create the metric capturing values.
		secondsMetrics = new TransferMetrics();
		minuteMetrics = new TransferMetrics();
		hourlyMetrics = new TransferMetrics();
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

		// Capture the seconds metric.
		if (secondsMetrics.isEmpty() || (world.getGameTime() % SDTime.TICKS_PER_SECOND) == 0) {
			secondsMetrics.addMetrics(storage.getReceivedPerTick(), storage.getExtractedPerTick());
		}

		// Capture the minutes metric.
		if (minuteMetrics.isEmpty() || (world.getGameTime() % SDTime.TICKS_PER_MINUTE) == 0) {
			minuteMetrics.addMetrics(storage.getReceivedPerTick(), storage.getExtractedPerTick());
		}

		// Capture the hours metric.
		if (hourlyMetrics.isEmpty() || (world.getGameTime() % SDTime.TICKS_PER_HOUR) == 0) {
			hourlyMetrics.addMetrics(storage.getReceivedPerTick(), storage.getExtractedPerTick());
		}
	}

	public TransferMetrics getSecondsMetrics() {
		return this.secondsMetrics;
	}

	public TransferMetrics getMinutesMetrics() {
		return this.minuteMetrics;
	}

	public TransferMetrics getHoursMetrics() {
		return this.hourlyMetrics;
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
	public void readFromNbt(CompoundNBT tag) {
		storage.deserializeNBT(tag.getCompound("energy_storage"));
		secondsMetrics = TransferMetrics.deserialize(tag.getCompound("seconds"));
		minuteMetrics = TransferMetrics.deserialize(tag.getCompound("minutes"));
		hourlyMetrics = TransferMetrics.deserialize(tag.getCompound("hours"));
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.put("energy_storage", storage.serializeNBT());
		tag.put("seconds", this.secondsMetrics.serialize());
		tag.put("minutes", this.minuteMetrics.serialize());
		tag.put("hours", this.hourlyMetrics.serialize());
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

	protected static class TransferMetrics {
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

		protected TransferMetrics(Queue<Float> received, Queue<Float> provided) {
			this.received = received;
			this.provided = provided;
		}

		public boolean isEmpty() {
			return received.isEmpty() || provided.isEmpty();
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

		public List<Float> getReceivedData() {
			return new ArrayList<Float>(received);
		}

		public List<Float> getProvidedData() {
			return new ArrayList<Float>(provided);
		}

		public CompoundNBT serialize() {
			// Allocate the output.
			CompoundNBT output = new CompoundNBT();

			// Convert the queues to lists.
			List<Float> receivedList = new ArrayList<Float>(received);
			List<Float> providedList = new ArrayList<Float>(provided);

			// Serialize the recieved list.
			ListNBT receivedNBTList = new ListNBT();
			receivedList.forEach(value -> {
				FloatNBT recievedTag = FloatNBT.valueOf(value);
				receivedNBTList.add(recievedTag);
			});
			output.put("received", receivedNBTList);

			// Serialize the provided list.
			ListNBT providedNBTList = new ListNBT();
			providedList.forEach(value -> {
				FloatNBT providedTag = FloatNBT.valueOf(value);
				providedNBTList.add(providedTag);
			});
			output.put("provided", providedNBTList);

			// Return the outputs.
			return output;
		}

		public static TransferMetrics deserialize(CompoundNBT data) {
			// Allocate the inputs.
			List<Float> receivedList = new ArrayList<Float>();
			List<Float> providedList = new ArrayList<Float>();

			// Read the serialized lists.
			ListNBT receivedNBT = data.getList("received", Constants.NBT.TAG_FLOAT);
			ListNBT providedNBT = data.getList("provided", Constants.NBT.TAG_FLOAT);

			// Populate the arrays.
			for (INBT receivedTag : receivedNBT) {
				FloatNBT receivedValue = (FloatNBT) receivedTag;
				receivedList.add(receivedValue.getFloat());
			}
			for (INBT providedTag : providedNBT) {
				FloatNBT providedValue = (FloatNBT) providedTag;
				providedList.add(providedValue.getFloat());
			}

			// Create the transfer metrics.
			return new TransferMetrics(new LinkedList<Float>(receivedList), new LinkedList<Float>(providedList));
		}

		@Override
		public String toString() {
			return "TransferMetrics [received=" + received + ", provided=" + provided + "]";
		}
	}
}
