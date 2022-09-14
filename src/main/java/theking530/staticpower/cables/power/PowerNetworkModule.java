package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.gui.text.PowerTextFormatting;
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
		output.add(new TranslatableComponent("gui.staticpower.voltage").append(": ")
				.append(ChatFormatting.BLUE.toString() + PowerTextFormatting.formatVoltageToString(lastProvidedVoltage).getString()));
	}

	public void getMultimeterOutput(List<Component> output, BlockPos startingLocation, BlockPos endingLocation) {
		output.add(new TextComponent(""));
		getReaderOutput(output);
		ElectricalPathProperties properties = getPropertiesBetweenPoints(startingLocation, endingLocation);

		if (lastProvidedVoltage != 0) {
			output.add(new TranslatableComponent("gui.staticpower.power_loss").append(": ").append(ChatFormatting.GOLD.toString() + PowerTextFormatting
					.formatPowerToString(StaticPowerVoltage.adjustPowerLossByVoltage(StaticPowerVoltage.getVoltageClass(lastProvidedVoltage), properties.powerLoss)).getString()));
		} else {
			output.add(new TranslatableComponent("gui.staticpower.power_loss").append(": ")
					.append(ChatFormatting.GOLD.toString() + PowerTextFormatting.formatPowerToString(properties.powerLoss).getString()).append(" @ ")
					.append(new TranslatableComponent(StaticPowerVoltage.LOW.getShortName())));
		}

		output.add(new TranslatableComponent("gui.staticpower.max_power").append(": ")
				.append(ChatFormatting.GREEN.toString() + PowerTextFormatting.formatPowerRateToString(properties.maxPower).getString()));
		output.add(new TranslatableComponent("gui.staticpower.length").append(": ").append(ChatFormatting.GRAY.toString() + properties.cables().size()));
	}

	public ElectricalPathProperties getPropertiesBetweenPoints(BlockPos start, BlockPos end) {
		if (!CableNetworkManager.get(Network.getWorld()).isTrackingCable(start)) {
			return ElectricalPathProperties.EMPTY;
		}

		if (!CableNetworkManager.get(Network.getWorld()).isTrackingCable(end)) {
			return ElectricalPathProperties.EMPTY;
		}

		if (start.equals(end)) {
			// A single cable connection does not have anypower loss (for gameplay reasons,
			// no need to be too mean).
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(end);
			double maxPower = cable.getDoubleProperty(PowerCableComponent.POWER_MAX);
			return new ElectricalPathProperties(0, maxPower, List.of(cable));
		}

		List<Path> paths = Network.getPathCache().getPaths(start, end, CableNetworkModuleTypes.POWER_NETWORK_MODULE);
		if (paths.isEmpty()) {
			return ElectricalPathProperties.EMPTY;
		}

		Path path = paths.get(0);
		List<ServerCable> cables = new LinkedList<ServerCable>();
		double cablePowerLoss = 0;
		double maxPowerPerTick = Double.MAX_VALUE;
		for (PathEntry entry : path.getEntries()) {
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(entry.getPosition());
			cablePowerLoss += (cable.getDoubleProperty(PowerCableComponent.POWER_LOSS));

			if (!cables.contains(cable)) {
				cables.add(cable);
			}

			double cableMaxPower = cable.getDoubleProperty(PowerCableComponent.POWER_MAX);
			if (cableMaxPower < maxPowerPerTick) {
				maxPowerPerTick = cableMaxPower;
			}
		}
		return new ElectricalPathProperties(cablePowerLoss, maxPowerPerTick, cables);
	}

	@Override
	public StaticVoltageRange getInputVoltageRange() {
		return StaticVoltageRange.ANY_VOLTAGE;
	}

	@Override
	public double getMaximumPowerInput() {
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
	public double getMaximumPowerOutput() {
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

	protected double supplyPower(BlockPos powerSourcePos, BlockPos fromCablePos, PowerStack stack, CachedPowerDestination destination, boolean simulate) {
		// Avoid loops
		if (destination.desintationPos.equals(powerSourcePos)) {
			return 0;
		}

		// Get the resistance between the points. If it is -1, there was no path, return
		// 0.
		ElectricalPathProperties properties = getPropertiesBetweenPoints(fromCablePos, destination.cable);
		if (properties == ElectricalPathProperties.EMPTY) {
			return 0;
		}

		// If the input voltage is higher than the voltage of a cable, break it. Use ALL
		// the power.
		for (ServerCable cable : properties.cables) {
			StaticPowerVoltage voltage = StaticPowerVoltage.values()[cable.getIntProperty(PowerCableComponent.VOLTAGE_ORDINAL)];
			if (stack.getVoltage() > voltage.getVoltage()) {
				if (!simulate) {
					Network.getWorld().destroyBlock(cable.getPos(), false);
					Network.getWorld().playSound(null, cable.getPos(), SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 1.25f);
				}
				return stack.getPower();
			}
		}

		// Limit the transfered power to the max of the path.
		double power = Math.min(stack.getPower(), properties.maxPower);

		// Adjust the power loss by the transfered voltage.
		double cablePowerLoss = StaticPowerVoltage.adjustPowerLossByVoltage(StaticPowerVoltage.getVoltageClass(stack.getVoltage()), properties.powerLoss);

		// Subtract that power loss from the total amount of power that we were
		// provided.
		double leftoverPower = power - cablePowerLoss;

		// Supply the power to the destination. If and only if it actually uses power,
		// then return how much power it used PLUS the power loss. Otherwise, use 0
		// power.
		double machineUsedPower = destination.power.addPower(new PowerStack(leftoverPower, stack.getVoltage(), stack.getCurrentType()), simulate);
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

	private record ElectricalPathProperties(double powerLoss, double maxPower, List<ServerCable> cables) {
		public static final ElectricalPathProperties EMPTY = new ElectricalPathProperties(0, 0, Collections.emptyList());
	}
}
