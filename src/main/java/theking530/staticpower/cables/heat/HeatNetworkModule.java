package theking530.staticpower.cables.heat;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorage;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class HeatNetworkModule extends AbstractCableNetworkModule {
	private HeatStorage heatStorage;

	public HeatNetworkModule() {
		super(CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
		// The actual input and output rates are controlled by the individual cables.
		heatStorage = new HeatStorage(0, Float.MAX_VALUE);
	}

	public HeatStorage getHeatStorage() {
		return heatStorage;
	}

	@Override
	public void getReaderOutput(List<Component> components) {
		float averageThermalConductivity = 0.0f;
		for (ServerCable cable : Network.getGraph().getCables().values()) {
			averageThermalConductivity += cable.getDoubleProperty(HeatCableComponent.HEAT_CONDUCTIVITY_TAG_KEY);
		}
		averageThermalConductivity /= Network.getGraph().getCables().size();

		Component currentHeat = GuiTextUtilities.formatHeatToString(heatStorage.getCurrentHeat(), heatStorage.getMaximumHeat());
		Component cooling = GuiTextUtilities.formatHeatRateToString(heatStorage.getCooledPerTick());
		Component heating = GuiTextUtilities.formatHeatRateToString(heatStorage.getHeatPerTick());
		Component averageConductivity = GuiTextUtilities.formatHeatRateToString(averageThermalConductivity);

		components
				.add(new TextComponent(ChatFormatting.WHITE.toString()).append(new TextComponent("Contains: ")).append(ChatFormatting.GRAY.toString()).append(currentHeat));
		components.add(new TextComponent(ChatFormatting.RED.toString()).append(new TextComponent("Heating: ")).append(ChatFormatting.GRAY.toString()).append(heating));
		components.add(new TextComponent(ChatFormatting.BLUE.toString()).append(new TextComponent("Cooling: ")).append(ChatFormatting.GRAY.toString()).append(cooling));
		components.add(new TextComponent(ChatFormatting.AQUA.toString()).append(new TextComponent("Average Conductivity: ")).append(ChatFormatting.GRAY.toString())
				.append(averageConductivity));
	}

	@Override
	public void tick(Level world) {
		// Capture the transfer metrics.
		heatStorage.captureHeatTransferMetric();

		// Capture the original conductivity.
		double originalConductivity = heatStorage.getConductivity();

		// Capture the cables in an array because the passive heating can affect the
		// list of cables.
		ServerCable[] cables = new ServerCable[Network.getGraph().getCables().size()];
		Network.getGraph().getCables().values().toArray(cables);

		// Handle the passive heating/cooling. Each iteration we limit the thermal
		// transfer rate to the current cable's. Do not put this in the IF heat > 0
		// check.
		for (ServerCable cable : cables) {
			// Skip cables that were at some point removed.
			if (!CableNetworkManager.get(world).isTrackingCable(cable.getPos())) {
				continue;
			}

			// Capture the cable's conductivity.
			double cableConductivity = cable.getDoubleProperty(HeatCableComponent.HEAT_CONDUCTIVITY_TAG_KEY);

			// Temporarily change the conductivity of the network for this cable.
			heatStorage.setConductivity(cableConductivity);

			// Execute any passive heating/cooling.
			for (Direction dir : Direction.values()) {
				HeatStorageUtilities.transferHeatPassivelyWithBlockFromDirection(world, cable.getPos(), dir, heatStorage);
			}
		}

		// Reset the conductivity.
		heatStorage.setConductivity(originalConductivity);

		// If we still have heat after dissipation, send the heat through the network.
		if (heatStorage.getCurrentHeat() > 0) {
			// Handle the active cooling.
			if (Network.getGraph().getDestinations().size() > 0) {
				// Get a map of all the applicable destination that support receiving heat.
				HashMap<IHeatStorage, DestinationWrapper> destinations = getValidDestinations();

				// Continue if we found some valid destinations.
				if (destinations.size() > 0) {
					// Calculate how we should split the output amount.
					double outputPerDestination = Math.max(1, heatStorage.getCurrentHeat() / destinations.size());

					// Distribute the heat to the destinations.
					for (IHeatStorage wrapper : destinations.keySet()) {
						// Get the thermal conductivity of the cable connected to this destination.
						double cableConductivity = CableNetworkManager.get(world).getCable(destinations.get(wrapper).getFirstConnectedCable())
								.getDoubleProperty(HeatCableComponent.HEAT_CONDUCTIVITY_TAG_KEY);

						// Get the thermal conductivity of the attached cable.
						double toSupply = Math.min(cableConductivity * wrapper.getConductivity(), outputPerDestination);

						// Limit that to the max amount we currently have.
						double supplied = wrapper.heat(Math.min(toSupply, heatStorage.getCurrentHeat()), false);

						// If the supplied amount is > 0, supply it.
						if (supplied > 0) {
							heatStorage.cool(supplied, false);
						}
					}
				}
			}
		}

		// Generate heat as needed.
		if (!heatStorage.isAtMaxHeat()) {
			for (ServerCable cable : Network.getGraph().getCables().values()) {
				// Generate heat if required to.
				BlockEntity te = world.getChunkAt(cable.getPos()).getBlockEntity(cable.getPos(), LevelChunk.EntityCreationType.QUEUED);
				Optional<HeatCableComponent> heatComponent = ComponentUtilities.getComponent(HeatCableComponent.class, te);
				if (heatComponent.isPresent()) {
					heatComponent.get().generateHeat(this);
				}
			}
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		// Allocate the total capacity.
		float total = 0;

		// Get all the cables in the network and get their cable components.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// If they have a heat cable component, get the capacity.
			if (cable.containsProperty(HeatCableComponent.HEAT_CAPACITY_DATA_TAG_KEY)) {
				total += cable.getDoubleProperty(HeatCableComponent.HEAT_CAPACITY_DATA_TAG_KEY);
			}
		}

		// Set the capacity of the heat storage to the provided capacity.
		heatStorage.setMaximumHeat(total);
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		if (other.hasModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE)) {
			HeatNetworkModule module = (HeatNetworkModule) other.getModule(CableNetworkModuleTypes.HEAT_NETWORK_MODULE);
			module.getHeatStorage().heat(heatStorage.getCurrentHeat(), false);
		}
	}

	public double getHeatPerCable() {
		return heatStorage.getCurrentHeat() / Network.getGraph().getCables().size();
	}

	protected HashMap<IHeatStorage, DestinationWrapper> getValidDestinations() {
		// Get a map of all the applicable destination that support recieving power.
		HashMap<IHeatStorage, DestinationWrapper> destinations = new HashMap<IHeatStorage, DestinationWrapper>();

		// Check each destination and capture the ones that can recieve heat.
		Network.getGraph().getDestinations().forEach((pos, wrapper) -> {
			if (wrapper.hasTileEntity() && wrapper.supportsType(DestinationType.HEAT) && !Network.getGraph().getCables().containsKey(pos)) {
				IHeatStorage otherHeatStorage = wrapper.getTileEntity().getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, wrapper.getFirstConnectedDestinationSide()).orElse(null);
				if (otherHeatStorage != null && otherHeatStorage.heat(heatStorage.getCurrentHeat(), true) > 0) {
					destinations.put(otherHeatStorage, wrapper);
				}
			}
		});
		return destinations;
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		heatStorage.deserializeNBT(tag.getCompound("heat_storage"));
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		tag.put("heat_storage", heatStorage.serializeNBT());
		return tag;
	}
}
