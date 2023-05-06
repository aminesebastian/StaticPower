package theking530.staticpower.cables.heat;

import java.util.HashMap;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorage;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.manager.CableNetworkAccessor;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticcore.gui.text.GuiTextUtilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class HeatNetworkModule extends CableNetworkModule {
	private HeatStorage heatStorage;

	public HeatNetworkModule() {
		super(ModCableModules.Heat.get());
		// The actual input and output rates are controlled by the individual cables.
		heatStorage = new HeatStorage(0, 0, Float.MAX_VALUE);
	}

	public HeatStorage getHeatStorage() {
		return heatStorage;
	}

	@Override
	public void getReaderOutput(List<Component> components, BlockPos pos) {
		float averageThermalConductivity = 0.0f;
		for (Cable cable : Network.getGraph().getCables().values()) {
			averageThermalConductivity += cable.getDataTag().getFloat(HeatCableComponent.HEAT_CONDUCTIVITY_TAG_KEY);
		}
		averageThermalConductivity /= Network.getGraph().getCables().size();

		Component currentHeat = GuiTextUtilities.formatHeatToString(heatStorage.getCurrentHeat(),
				heatStorage.getOverheatThreshold());
		Component cooling = GuiTextUtilities.formatHeatRateToString(heatStorage.getTicker().getAverageCooledPerTick());
		Component heating = GuiTextUtilities.formatHeatRateToString(heatStorage.getTicker().getAverageHeatedPerTick());
		Component averageConductivity = GuiTextUtilities.formatHeatRateToString(averageThermalConductivity);

		components.add(Component.literal(ChatFormatting.WHITE.toString()).append(Component.literal("Contains: "))
				.append(ChatFormatting.GRAY.toString()).append(currentHeat));
		components.add(Component.literal(ChatFormatting.RED.toString()).append(Component.literal("Heating: "))
				.append(ChatFormatting.GRAY.toString()).append(heating));
		components.add(Component.literal(ChatFormatting.BLUE.toString()).append(Component.literal("Cooling: "))
				.append(ChatFormatting.GRAY.toString()).append(cooling));
		components.add(
				Component.literal(ChatFormatting.AQUA.toString()).append(Component.literal("Average Conductivity: "))
						.append(ChatFormatting.GRAY.toString()).append(averageConductivity));
	}

	@Override
	public void tick(Level world) {
		// Capture the transfer metrics.
		heatStorage.getTicker().tickWithoutHeatTransfer(world);

		// Capture the cables in an array because the passive heating can affect the
		// list of cables.
		Cable[] cables = new Cable[Network.getGraph().getCables().size()];
		Network.getGraph().getCables().values().toArray(cables);

		// Handle the passive heating/cooling. Each iteration we limit the thermal
		// transfer rate to the current cable's. Do not put this in the IF heat > 0
		// check.
		for (Cable cable : cables) {
			// Skip cables that were at some point removed.
			if (!CableNetworkAccessor.get(world).isTrackingCable(cable.getPos())) {
				continue;
			}

			// Capture the cable's conductivity.
			float cableConductivity = cable.getDataTag().getFloat(HeatCableComponent.HEAT_CONDUCTIVITY_TAG_KEY);

			// Temporarily change the conductivity of the network for this cable.
			heatStorage.setConductivity(cableConductivity);

			// Execute any heating/cooling.
			HeatStorageUtilities.transferHeatWithSurroundings(heatStorage, world, cable.getPos(),
					HeatTransferAction.EXECUTE);
		}

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		// Allocate the total capacity.
		int total = 0;

		// Get all the cables in the network and get their cable components.
		for (Cable cable : mapper.getDiscoveredCables()) {
			// If they have a heat cable component, get the capacity.
			if (cable.getDataTag().contains(HeatCableComponent.HEAT_CAPACITY_DATA_TAG_KEY)) {
				total += cable.getDataTag().getInt(HeatCableComponent.HEAT_CAPACITY_DATA_TAG_KEY);
			}
		}

		// Set the capacity of the heat storage to the average of the network.
		float average = (float) total / Math.max(mapper.getDiscoveredCables().size(), 1);
		heatStorage.setMaximumHeat((int) average);
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		if (other.hasModule(ModCableModules.Heat.get())) {
			HeatNetworkModule module = (HeatNetworkModule) other.getModule(ModCableModules.Heat.get());
			module.getHeatStorage().heat(heatStorage.getCurrentHeat(), HeatTransferAction.EXECUTE);
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
			if (wrapper.hasTileEntity() && wrapper.supportsType(ModCableDestinations.Heat.get())
					&& !Network.getGraph().getCables().containsKey(pos)) {
				IHeatStorage otherHeatStorage = wrapper.getTileEntity()
						.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY,
								wrapper.getFirstConnectedDestinationSide())
						.orElse(null);
				if (otherHeatStorage != null
						&& otherHeatStorage.heat(heatStorage.getCurrentHeat(), HeatTransferAction.SIMULATE) > 0) {
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
