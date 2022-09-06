package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.consumer.CapabilityStaticPower;
import theking530.api.energy.consumer.IStaticPowerStorage;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.DestinationWrapper;
import theking530.staticpower.cables.network.DestinationWrapper.DestinationType;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.pathfinding.Path;
import theking530.staticpower.cables.network.pathfinding.Path.PathEntry;

public class PowerNetworkModule extends AbstractCableNetworkModule implements IStaticPowerStorage {
	private final List<CachedPowerDestination> destinations;
	private double lastTransferedVoltage;
	private int lastSuppliedDestinationIndex;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		destinations = new ArrayList<>();
	}

	@Override
	public void tick(Level world) {
		lastTransferedVoltage = 0;
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		if (other.hasModule(CableNetworkModuleTypes.POWER_NETWORK_MODULE)) {

		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		destinations.clear();

		// Check to make sure we have power and valid desinations.
		if (mapper.getDestinations().size() > 0) {
			mapper.getDestinations().forEach((pos, wrapper) -> {
				if (!CableNetworkManager.get(getNetwork().getWorld()).isTrackingCable(wrapper.getFirstConnectedCable())) {
					return;
				}

				// Add all the power interfaces.
				List<CachedPowerDestination> powerInterfaces = getInterfaceForDesination(wrapper);
				if (powerInterfaces.size() > 0) {
					destinations.addAll(powerInterfaces);
				}
			});
		}
	}

	@Override
	public void readFromNbt(CompoundTag tag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {

		return tag;
	}

	@Nullable
	public List<CachedPowerDestination> getInterfaceForDesination(DestinationWrapper wrapper) {
		// Allocate the output.
		List<CachedPowerDestination> output = new ArrayList<CachedPowerDestination>();

		// Iterate through all the connected cables.
		for (BlockPos cablePos : wrapper.getConnectedCables().keySet()) {
			// Get the cable pos.
			Direction connectedSide = wrapper.getConnectedCables().get(cablePos);

			// Skip NON tile entity destinations.
			if (!wrapper.hasTileEntity()) {
				continue;
			}

			if (wrapper.supportsType(DestinationType.POWER)) {
				IStaticPowerStorage powerStorage = wrapper.getTileEntity().getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, connectedSide).orElse(null);
				if (powerStorage != null && powerStorage.canAcceptPower()) {
					output.add(new CachedPowerDestination(powerStorage, cablePos));
				}
			}
		}

		return output;
	}

	@Override
	public void getReaderOutput(List<Component> output) {

	}

	protected class CachedPowerDestination {
		public final IStaticPowerStorage power;
		public final BlockPos cable;

		public CachedPowerDestination(IStaticPowerStorage power, BlockPos cable) {
			this.power = power;
			this.cable = cable;
		}
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticVoltageRange.ANY_VOLTAGE;
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
	public double getVoltageOutput() {
		return lastTransferedVoltage;
	}

	@Override
	public double usePower(double power, boolean simulate) {
		return 0;
	}

	@Override
	public double addPower(double voltage, double current, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canAcceptPower() {
		return true;
	}

	@Override
	public boolean doesProvidePower() {
		return true;
	}

	public double addPower(BlockPos pos, double voltage, double power, boolean simulate) {
		if (destinations.isEmpty()) {
			return 0;
		}

		// We round robin the output destination. Perform this check first as the last
		// time around we could end up with an invalid index. We do NOT clear the index
		// on graph update so even if we get spammed with updates, we end up preserving
		// the round robin.
		if (lastSuppliedDestinationIndex >= destinations.size()) {
			lastSuppliedDestinationIndex = 0;
		}
		CachedPowerDestination destination = destinations.get(lastSuppliedDestinationIndex);
		lastSuppliedDestinationIndex++;

		// Get all the paths to the destination from this provider.
		List<Path> paths = Network.getPathCache().getPaths(pos, destination.cable, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		if (paths.isEmpty()) {
			return 0;
		}

		// The shortest path will be the first one always. We then sum up the total
		// resistance along the path.
		Path path = paths.get(0);
		double cableResistance = 0;
		for (PathEntry entry : path.getEntries()) {
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(entry.getPosition());
			cableResistance += (cable.getDoubleProperty(PowerCableComponent.POWER_RESISTANCE) * 1000);
		}

		// This isn't the scientifically accurate way to do this, but this results in
		// more fun gameplay! We won't factor in the circuit current or load current as
		// part of this calculation. Just a straight inverse relationship between cable
		// resistance and voltage. As voltage goes up, power loss goes down. As
		// resistance goes up, power loss goes up.
		double cablePowerLoss = cableResistance / voltage;

		// If we would use more power transferring than we have, do nothing. This
		// shouldn't be possible in the real world, but this is possible in this mod due
		// to the simplified math from above.
		if (cablePowerLoss >= power) {
			return 0;
		}

		// Subtract that power loss from the total amount of power that we were
		// provided.
		power -= cablePowerLoss;

		// Supply the power to the destination. If and only if it actually uses power,
		// then return how much power it used PLUS the power loss. Otherwise, use 0
		// power.
		double machineUsedPower = destination.power.addPower(voltage, power, false);
		lastTransferedVoltage = voltage;

		// TODO: If there is leftover power, supply to the next destination.
		return machineUsedPower + cablePowerLoss;
	}
}
