package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
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
	protected record CachedPowerDestination(IStaticPowerStorage power, BlockPos cable, BlockPos desintationPos) {
	}

	private final List<CachedPowerDestination> destinations;
	private double lastProvidedVoltage;
	private CurrentType lastProvidedCurrentType;
	private double maximumCurrentOutput;
	private int lastSuppliedDestinationIndex;

	public PowerNetworkModule() {
		super(CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		destinations = new ArrayList<>();
		lastSuppliedDestinationIndex = 0;
	}

	@Override
	public void preWorldTick(Level world) {
		lastProvidedVoltage = 0;
		lastProvidedCurrentType = CurrentType.DIRECT;
	}

	@Override
	public void tick(Level world) {
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
				if (powerStorage != null) {
					output.add(new CachedPowerDestination(powerStorage, cablePos, wrapper.getPos()));
				}
			}
		}

		return output;
	}

	@Override
	public void getReaderOutput(List<Component> output) {
		output.add(new TextComponent(String.format("Supplying: %1$d destinations.", destinations.size())));
		output.add(new TextComponent("Last Supplied Voltage: ")
				.append(ChatFormatting.GREEN.toString() + StaticPowerEnergyTextUtilities.formatVoltageToString(lastProvidedVoltage).getString()));
	}

	public void getMultimeterOutput(List<Component> output, BlockPos startingLocation, BlockPos endingLocation) {
		output.add(new TextComponent(""));
		getReaderOutput(output);
		// Get all the paths to the destination from this provider.
		double cableResistance = getResistanceBetweenPoints(startingLocation, endingLocation);
		output.add(new TextComponent("Resistance over Points: ")
				.append(ChatFormatting.GOLD.toString() + StaticPowerEnergyTextUtilities.formatResistanceToString(cableResistance).getString()));
		output.add(new TextComponent("Last Power Loss: ")
				.append(ChatFormatting.RED.toString() + StaticPowerEnergyTextUtilities.formatPowerToString(cableResistance / lastProvidedVoltage).getString()));
	}

	public double getResistanceBetweenPoints(BlockPos start, BlockPos end) {
		if (!CableNetworkManager.get(Network.getWorld()).isTrackingCable(start)) {
			return -1;
		}

		if (!CableNetworkManager.get(Network.getWorld()).isTrackingCable(end)) {
			return -1;
		}

		if (start.equals(end)) {
			return 0; // A single cable connection does not have any power loss (for gameplay reasons,
						// no need to be too mean).

		}

		List<Path> paths = Network.getPathCache().getPaths(start, end, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		if (paths.isEmpty()) {
			return -1;
		}

		Path path = paths.get(0);
		double cableResistance = 0;
		for (PathEntry entry : path.getEntries()) {
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(entry.getPosition());
			cableResistance += (cable.getDoubleProperty(PowerCableComponent.POWER_RESISTANCE));
		}
		return cableResistance;
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticVoltageRange.ANY_VOLTAGE;
	}

	@Override
	public double getMaximumCurrentInput() {
		return Double.MAX_VALUE;
	}

	@Override
	public boolean canAcceptCurrentType(CurrentType type) {
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
		return lastProvidedVoltage;
	}

	@Override
	public double getMaximumCurrentOutput() {
		return maximumCurrentOutput;
	}

	@Override
	public CurrentType getOutputCurrentType() {
		return lastProvidedCurrentType;
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		return PowerStack.EMPTY;
	}

	@Override
	public double addPower(PowerStack power, boolean simulate) {
		return 0;
	}

	protected double supplyPower(BlockPos powerSourcePos, BlockPos fromCablePos, PowerStack power, CachedPowerDestination destination, boolean simulate) {
		// Avoid loops
		if (destination.desintationPos.equals(powerSourcePos)) {
			return 0;
		}

		// Get the resistance between the points. If it is -1, there was no path, return
		// 0.
		double cableResistance = getResistanceBetweenPoints(fromCablePos, destination.cable);
		if (cableResistance == -1) {
			return 0;
		}

		// This isn't the scientifically accurate way to do this, but this results in
		// more fun gameplay! We won't factor in the circuit current or load current as
		// part of this calculation. Just a straight inverse relationship between cable
		// resistance and voltage. As voltage goes up, power loss goes down. As
		// resistance goes up, power loss goes up.
		double cablePowerLoss = Math.abs(cableResistance / power.getVoltage());

		// Subtract that power loss from the total amount of power that we were
		// provided.
		double leftoverPower = power.getPower() - cablePowerLoss;

		// Supply the power to the destination. If and only if it actually uses power,
		// then return how much power it used PLUS the power loss. Otherwise, use 0
		// power.
		double machineUsedPower = destination.power.addPower(new PowerStack(leftoverPower, power.getVoltage(), power.getCurrentType()), false);
		if (machineUsedPower == 0) {
			return 0;
		}

		return machineUsedPower + cablePowerLoss;
	}

	public double addPower(BlockPos powerSourcePos, BlockPos fromCablePos, PowerStack power, boolean simulate) {
		lastProvidedVoltage = power.getVoltage();
		lastProvidedCurrentType = power.getCurrentType();

		if (destinations.isEmpty()) {
			return 0;
		}

		double suppliedPower = 0;
		for (int i = 0; i < destinations.size(); i++) {
			lastSuppliedDestinationIndex = (lastSuppliedDestinationIndex + 1) % destinations.size();
			double supplied = supplyPower(powerSourcePos, fromCablePos, power, destinations.get(lastSuppliedDestinationIndex), simulate);
			power.setPower(power.getPower() - supplied);
			suppliedPower += supplied;
			if (power.getPower() <= 0) {
				break;
			}
		}
		return suppliedPower;
	}
}
