package theking530.staticpower.cables.power;

import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
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
import theking530.staticpower.energy.StaticPowerFEStorage;
import theking530.staticpower.utilities.MetricConverter;

public class PowerNetworkModule extends AbstractCableNetworkModule {
	private StaticPowerFEStorage EnergyStorage;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		EnergyStorage = new StaticPowerFEStorage(0, Integer.MAX_VALUE, Integer.MAX_VALUE);
		// No one should extract power from the network, we only provide it.
		EnergyStorage.setCanExtract(false);
	}

	@Override
	public void tick(World world) {
		if (EnergyStorage.getEnergyStored() > 0 && Network.getGraph().getDestinations().size() > 0) {
			// Get a map of all the applicable destination that support recieving power.
			HashMap<BlockPos, DestinationWrapper> destinations = new HashMap<BlockPos, DestinationWrapper>();
			Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
				if (wrapper.supportsType(DestinationType.POWER)) {
					IEnergyStorage energyStorage = wrapper.getTileEntity().getCapability(CapabilityEnergy.ENERGY, wrapper.getDestinationSide()).orElse(null);
					if (energyStorage != null && energyStorage.receiveEnergy(EnergyStorage.getEnergyStored(), true) > 0) {
						destinations.put(pos, wrapper);
					}
				}
			});

			// If there are no valid destinations, return early.
			if (destinations.size() == 0) {
				return;
			}

			// Calculate how we should split the output amount.
			int outputPerDestination = Math.max(1, EnergyStorage.getEnergyStored() / destinations.size());

			// Distribute the power to the destinations.
			for (DestinationWrapper wrapper : destinations.values()) {
				IEnergyStorage energyStorage = wrapper.getTileEntity().getCapability(CapabilityEnergy.ENERGY, wrapper.getDestinationSide()).orElse(null);
				if (energyStorage != null) {
					if (energyStorage.canReceive()) {
						int toSupply = Math.min(CableNetworkManager.get(world).getCable(wrapper.getConnectedCable()).getIntProperty(PowerCableComponent.POWER_RATE_DATA_TAG_KEY), outputPerDestination);
						int supplied = energyStorage.receiveEnergy(Math.min(toSupply, EnergyStorage.getCurrentMaximumPowerOutput()), false);
						if (supplied > 0) {
							EnergyStorage.setCanExtract(true);
							EnergyStorage.extractEnergy(supplied, false);
							EnergyStorage.setCanExtract(false);
						}
					}
				}
			}
		}
	}

	public void onNetworksJoined(CableNetwork other) {
		if (other.hasModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {
			PowerNetworkModule module = (PowerNetworkModule) other.getModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
			module.getEnergyStorage().receiveEnergy(EnergyStorage.getEnergyStored(), false);
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		// Calculate the total capacity.
		int capacity = mapper.getDiscoveredCables().stream().filter(p -> p.supportsNetworkModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)).mapToInt(p -> p.getIntProperty(PowerCableComponent.POWER_CAPACITY_DATA_TAG_KEY)).sum();

		// If the capacity is less than 0, that means we overflowed. Set the capcaity to
		// the maximum integer value.
		EnergyStorage.setCapacity(capacity < 0 ? Integer.MAX_VALUE : (int) capacity);
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		EnergyStorage.readFromNbt(tag);
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		EnergyStorage.writeToNbt(tag);
		return tag;
	}

	public StaticPowerFEStorage getEnergyStorage() {
		return EnergyStorage;
	}

	@Override
	public void getReaderOutput(List<ITextComponent> output) {
		String storedEnergy = new MetricConverter(this.getEnergyStorage().getEnergyStored()).getValueAsString(true);
		String maximumEnergy = new MetricConverter(this.getEnergyStorage().getMaxEnergyStored()).getValueAsString(true);
		output.add(new StringTextComponent(String.format("Contains: %1$sRF out of a maximum of %2$sRF.", storedEnergy, maximumEnergy)));
	}
}
