package theking530.staticpower.cables.power;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import theking530.api.energy.CapabilityStaticPower;
import theking530.api.energy.CurrentType;
import theking530.api.energy.IStaticPowerStorage;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.cablenetwork.pathfinding.Path;
import theking530.staticcore.cablenetwork.pathfinding.Path.PathEntry;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticcore.gui.text.PowerTextFormatting;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class PowerNetworkModule extends CableNetworkModule implements IStaticPowerStorage {
	protected record CachedPowerDestination(IStaticPowerStorage power, BlockPos cable, BlockPos desintationPos) {
	}

	protected record CablePowerSupplyEvent(double actualPower, double powerLoss) {

		public static final CablePowerSupplyEvent EMPTY = new CablePowerSupplyEvent(0, 0);

		public double getTotalPower() {
			return actualPower + powerLoss;
		}
	}

	private record ElectricalPathProperties(double powerLoss, double maxCurrent, double length, List<ServerCable> cables) {
		public static final ElectricalPathProperties EMPTY = new ElectricalPathProperties(0, 0, 0, Collections.emptyList());
	}

	private final List<CachedPowerDestination> destinations;
	private final Map<BlockPos, PowerStack> currentTickCurrentMap;

	private double maximumCurrentOutput;
	private int lastSuppliedDestinationIndex;

	private PowerStack lastTransferedPower;

	public PowerNetworkModule() {
		this(ModCableModules.Power.get());
	}

	public PowerNetworkModule(CableNetworkModuleType type) {
		super(type);
		destinations = new ArrayList<>();
		currentTickCurrentMap = new HashMap<>();
		lastSuppliedDestinationIndex = 0;
		lastTransferedPower = PowerStack.EMPTY.copy();
	}

	@Override
	public void preWorldTick(Level world) {
		lastTransferedPower.setPower(0);
		lastTransferedPower.setVoltage(StaticPowerVoltage.ZERO);
		lastTransferedPower.setType(CurrentType.DIRECT);
		currentTickCurrentMap.clear();
	}

	@Override
	public void tick(Level world) {
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

			if (wrapper.supportsType(ModCableDestinations.Power.get())) {
				IStaticPowerStorage powerStorage = wrapper.getTileEntity().getCapability(CapabilityStaticPower.STATIC_VOLT_CAPABILITY, connectedSide).orElse(null);
				if (powerStorage != null) {
					output.add(new CachedPowerDestination(powerStorage, cablePos, wrapper.getPos()));
				}
			}
		}

		return output;
	}

	@Override
	public void getReaderOutput(List<Component> output, BlockPos pos) {
		output.add(Component.literal(String.format("Supplying: %1$d destinations.", destinations.size())));
		getMultimeterOutput(output, pos, pos);
	}

	public void getMultimeterOutput(List<Component> output, BlockPos startingLocation, BlockPos endingLocation) {
		output.add(Component.literal(""));
		ElectricalPathProperties properties = getPropertiesBetweenPoints(startingLocation, endingLocation);

		output.add(Component.literal("From [").append(ChatFormatting.GRAY.toString() + startingLocation.toShortString()).append(ChatFormatting.WHITE + "] to [")

				.append(ChatFormatting.GRAY.toString() + endingLocation.toShortString()).append("]"));
		{
			Component usedCurrentComponent = PowerTextFormatting.formatCurrentToString(lastTransferedPower.getCurrent(), false, true);
			Component maxCurrentComponent = PowerTextFormatting.formatCurrentToString(properties.maxCurrent);

			output.add(Component.translatable("gui.staticpower.current").append(": ")
					.append(ChatFormatting.RED.toString() + String.format("%1$s/%2$s", usedCurrentComponent.getString(), maxCurrentComponent.getString())));
		}
		{
			Component usedPowerComponent = PowerTextFormatting.formatPowerToString(lastTransferedPower.getPower(), false, true);
			Component maxPowerComponent = PowerTextFormatting.formatPowerRateToString(properties.maxCurrent * lastTransferedPower.getVoltage().getValue());

			output.add(Component.translatable("gui.staticpower.power").append(": ")
					.append(ChatFormatting.GOLD.toString() + String.format("%1$s/%2$s", usedPowerComponent.getString(), maxPowerComponent.getString())));
		}

		output.add(Component.translatable("gui.staticpower.power_loss").append(": ").append(ChatFormatting.GOLD.toString()
				+ PowerTextFormatting.formatPowerToString(StaticPowerVoltage.adjustPowerLossByVoltage(lastTransferedPower.getVoltage(), properties.powerLoss)).getString()));

		output.add(Component.translatable("gui.staticpower.length").append(": ").append(ChatFormatting.GRAY.toString() + properties.length()));
	}

	public ElectricalPathProperties getPropertiesBetweenPoints(BlockPos start, BlockPos end) {
		if (!CableNetworkManager.get(Network.getWorld()).isTrackingCable(start)) {
			return ElectricalPathProperties.EMPTY;
		}

		if (start.equals(end)) {
			// A single cable connection does not have anypower loss (for gameplay reasons,
			// no need to be too mean).
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(end);
			double maxPower = cable.getDataTag().getDouble(PowerCableComponent.CURRENT_MAX);
			return new ElectricalPathProperties(0, maxPower, 1, List.of(cable));
		}

		List<Path> paths = Network.getPathCache().getPaths(start, end, getType());
		if (paths.isEmpty()) {
			return ElectricalPathProperties.EMPTY;
		}

		Path path = paths.get(0);
		Set<ServerCable> cables = new LinkedHashSet<ServerCable>(); // Use a LinkedHashSet to maintain order.
		double cablePowerLoss = 0;
		double lowestMaxCurrent = Double.MAX_VALUE;
		for (PathEntry entry : path.getEntries()) {
			// Make sure the cable is not null. The cable will be null when checking the
			// last point in an actual run, as this will be the destination. The reason we
			// don't just check if the entry position != the end is when using the
			// multimeter, you can scan between two cables, not just a cable and a
			// destination.
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(entry.getPosition());
			if (cable != null) {
				if (cables.contains(cable)) {
					continue;
				}
				cablePowerLoss += (cable.getDataTag().getDouble(PowerCableComponent.POWER_LOSS));

				if (!cables.contains(cable)) {
					cables.add(cable);
				}

				double cableMaxCurrent = cable.getDataTag().getDouble(PowerCableComponent.CURRENT_MAX);
				lowestMaxCurrent = Math.min(lowestMaxCurrent, cableMaxCurrent);
			}
		}
		cablePowerLoss /= cables.size();
		cablePowerLoss *= path.getLength();
		return new ElectricalPathProperties(cablePowerLoss, lowestMaxCurrent, path.getLength(), cables.stream().toList());
	}

	public PowerStack getLastTransferedStack() {
		return lastTransferedPower;
	}

	protected CablePowerSupplyEvent supplyPower(BlockPos powerSourcePos, BlockPos fromCablePos, PowerStack stack, CachedPowerDestination destination, boolean simulate) {
		// Avoid loops
		if (destination.desintationPos.equals(powerSourcePos)) {
			return CablePowerSupplyEvent.EMPTY;
		}

		// Get the resistance between the points. If it is -1, there was no path, return
		// 0.
		ElectricalPathProperties properties = getPropertiesBetweenPoints(fromCablePos, destination.cable());
		if (properties == ElectricalPathProperties.EMPTY) {
			return CablePowerSupplyEvent.EMPTY;
		}

		// Adjust the power loss by the transfered voltage.
		double cablePowerLoss = StaticPowerVoltage.adjustPowerLossByVoltage(stack.getVoltage(), properties.powerLoss);

		// Subtract that power loss from the total amount of power that we were
		// provided.
		double leftoverPower = stack.getPower() - cablePowerLoss;

		// Simulate adding power to the destination. If the destination doens't need
		// power, OR we are simulating, just return the simulated power.
		double simulatedMachineUsage = destination.power.addPower(new PowerStack(leftoverPower, stack.getVoltage(), stack.getCurrentType()), true);
		if (simulatedMachineUsage == 0) {
			return CablePowerSupplyEvent.EMPTY;
		}

		if (simulate) {
			return new CablePowerSupplyEvent(simulatedMachineUsage, cablePowerLoss);
		}

		// Break any cables that should burn given the current we are transferring.
		for (int i = 0; i < properties.cables.size(); i++) {
			currentTickCurrentMap.compute(properties.cables.get(i).getPos(), (pos, existing) -> {
				if (existing != null) {
					existing.setPower(existing.getPower() + simulatedMachineUsage);
					return existing;
				}
				return new PowerStack(simulatedMachineUsage, stack.getVoltage(), stack.getCurrentType());
			});
		}

		// If we made it this far, actually supply the power and then return the power
		// usage + the power loss;
		double machineUsedPower = destination.power.addPower(new PowerStack(leftoverPower, stack.getVoltage(), stack.getCurrentType()), simulate);
		return new CablePowerSupplyEvent(machineUsedPower, cablePowerLoss);
	}

	public double addPower(BlockPos powerSourcePos, BlockPos fromCablePos, PowerStack power, boolean simulate) {
		if (destinations.isEmpty()) {
			// Still set the received stack so we can show the voltage with the multimeter.
			updateTransferedPower(CablePowerSupplyEvent.EMPTY, power.getVoltage(), power.getCurrentType());
			return 0;
		}

		PowerStack powerCopy = power.copy();
		double suppliedPower = 0;
		for (int i = 0; i < destinations.size(); i++) {
			lastSuppliedDestinationIndex = (lastSuppliedDestinationIndex + 1) % destinations.size();
			CablePowerSupplyEvent supplyEvent = supplyPower(powerSourcePos, fromCablePos, powerCopy, destinations.get(lastSuppliedDestinationIndex), simulate);

			powerCopy.setPower(powerCopy.getPower() - supplyEvent.getTotalPower());
			suppliedPower += supplyEvent.getTotalPower();

			if (!simulate) {
				updateTransferedPower(supplyEvent, power.getVoltage(), power.getCurrentType());
			}

			if (powerCopy.isEmpty()) {
				break;
			}
		}

		// If any cables were over-currented, break them.
		for (Entry<BlockPos, PowerStack> entry : currentTickCurrentMap.entrySet()) {
			ServerCable cable = CableNetworkManager.get(getNetwork().getWorld()).getCable(entry.getKey());
			if (cable != null) {
				double currentThroughCable = currentTickCurrentMap.get(cable.getPos()).getCurrent();
				double maxCurrent = cable.getDataTag().getDouble(PowerCableComponent.CURRENT_MAX);
				if (currentThroughCable > maxCurrent) {
					breakCable(cable.getPos());
				}
			}
		}

		return suppliedPower;
	}

	private void updateTransferedPower(CablePowerSupplyEvent supplyEvent, StaticPowerVoltage voltage, CurrentType currentType) {
		lastTransferedPower.setPower(lastTransferedPower.getPower() + supplyEvent.getTotalPower());

		if (voltage.isGreaterThan(lastTransferedPower.getVoltage())) {
			lastTransferedPower.setVoltage(voltage);
		}

		if (currentType == CurrentType.ALTERNATING) {
			lastTransferedPower.setType(CurrentType.ALTERNATING);
		}
	}

	private void breakCable(BlockPos cablePos) {
		Network.getWorld().destroyBlock(cablePos, false);
		Network.getWorld().playSound(null, cablePos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5f, 1.25f);
		((ServerLevel) Network.getWorld()).sendParticles(ParticleTypes.SMOKE, cablePos.getX() + 0.5, cablePos.getY() + 0.5, cablePos.getZ() + 0.5, 5, 0.15, 0.15, 0.15, 0);
		((ServerLevel) Network.getWorld()).sendParticles(ParticleTypes.LANDING_LAVA, cablePos.getX() + 0.5, cablePos.getY() + 0.5, cablePos.getZ() + 0.5, 2, 0.15, 0.15, 0.15, 0);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		return tag;
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
	public StaticPowerVoltage getOutputVoltage() {
		return lastTransferedPower.getVoltage();
	}

	@Override
	public double getMaximumPowerOutput() {
		return maximumCurrentOutput;
	}

	@Override
	public CurrentType getOutputCurrentType() {
		return lastTransferedPower.getCurrentType();
	}

	@Override
	public PowerStack drainPower(double power, boolean simulate) {
		return PowerStack.EMPTY;
	}

	@Override
	public boolean canAcceptExternalPower() {
		return true;
	}

	@Override
	public boolean canOutputExternalPower() {
		return true;
	}

	@Override
	public double addPower(PowerStack power, boolean simulate) {
		return 0;
	}
}
