package theking530.staticpower.cables.heat;

import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.tileentities.components.heat.CapabilityHeatable;
import theking530.staticpower.tileentities.components.heat.HeatStorage;
import theking530.staticpower.tileentities.components.heat.IHeatStorage;

public class HeatNetworkModule extends AbstractCableNetworkModule {
	private HeatStorage heatStorage;

	public HeatNetworkModule() {
		super(CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		heatStorage = new HeatStorage(0, Integer.MAX_VALUE);
	}

	public HeatStorage getHeatStorage() {
		return heatStorage;
	}

	@Override
	public void getReaderOutput(List<ITextComponent> components) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick(World world) {
		if (heatStorage.getCurrentHeat() > 0) {
			// Handle the active cooling.
			if (Network.getGraph().getDestinations().size() > 0) {
				// Get a map of all the applicable destination that support recieving power.
				HashMap<IHeatStorage, DestinationWrapper> destinations = new HashMap<IHeatStorage, DestinationWrapper>();

				// Check each destination and capture the ones that can recieve heat.
				Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
					if (wrapper.supportsType(DestinationType.HEAT)) {
						IHeatStorage otherHeatStorage = wrapper.getTileEntity().getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, wrapper.getDestinationSide()).orElse(null);
						if (otherHeatStorage != null && otherHeatStorage.heat(heatStorage.getCurrentHeat(), true) > 0) {
							destinations.put(otherHeatStorage, wrapper);
						}
					}
				});

				// Continue if we found some valid destinations.
				if (destinations.size() > 0) {
					// Calculate how we should split the output amount.
					float outputPerDestination = Math.max(1, heatStorage.getCurrentHeat() / destinations.size());

					// Distribute the heat to the destinations.
					for (IHeatStorage wrapper : destinations.keySet()) {
						float toSupply = Math.min(CableNetworkManager.get(world).getCable(destinations.get(wrapper).getConnectedCable()).getFloatProperty(HeatCableComponent.HEAT_RATE_DATA_TAG_KEY),
								outputPerDestination);
						float supplied = wrapper.heat(Math.min(toSupply, heatStorage.getCurrentMaximumHeatOutput()), false);
						if (supplied > 0) {
							heatStorage.cool(supplied, false);
						}
					}
				}
			}
		}

		// Handle the passive heating/cooling.
		for (ServerCable cable : Network.getGraph().getCables().values()) {
			heatStorage.transferWithSurroundings(Network.getWorld(), cable.getPos(), 1.0f);
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		// Allocate the total capacity.
		float total = 0;

		// Get all the cables in the network and get their cable components.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// If they have a heat cable component, get the capacity.
			if (cable.containsProperty(HeatCableComponent.HEAT_CAPACITY_DATA_TAG_KEY)) {
				total += cable.getFloatProperty(HeatCableComponent.HEAT_CAPACITY_DATA_TAG_KEY);
			}
		}

		// Set the capacity of the heat storage to the provided capacity.
		heatStorage.setMaximumHeat(total);
	}

	public void onNetworksJoined(CableNetwork other) {
		if (other.hasModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE)) {
			HeatNetworkModule module = (HeatNetworkModule) other.getModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
			module.getHeatStorage().addHeatIgnoreTransferRate(heatStorage.getCurrentHeat());
		}
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
		heatStorage.deserializeNBT(tag.getCompound("heat_storage"));
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.put("heat_storage", heatStorage.serializeNBT());
		return tag;
	}
}
