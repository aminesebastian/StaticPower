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

	private record ElectricalPathProperties(double powerLoss, double maxPower, List<ServerCable> cables) {
		public static final ElectricalPathProperties EMPTY = new ElectricalPathProperties(0, 0, Collections.emptyList());
	}

	private final List<CachedPowerDestination> destinations;
	private double lastProvidedVoltage;
	private CurrentType lastProvidedCurrentType;
	private double maximumCurrentOutput;
	private int lastSuppliedDestinationIndex;

	public PowerNetworkModule() {
		this(ModCableModules.Power.get());
	}

	public PowerNetworkModule(CableNetworkModuleType type) {
		super(type);
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
		output.add(Component.translatable("gui.staticpower.voltage").append(": ")
				.append(ChatFormatting.BLUE.toString() + PowerTextFormatting.formatVoltageToString(lastProvidedVoltage).getString()));
	}

	public void getMultimeterOutput(List<Component> output, BlockPos startingLocation, BlockPos endingLocation) {
		output.add(Component.literal(""));
		getReaderOutput(output, null);
		ElectricalPathProperties properties = getPropertiesBetweenPoints(startingLocation, endingLocation);

		if (lastProvidedVoltage != 0) {
			output.add(Component.translatable("gui.staticpower.power_loss").append(": ").append(ChatFormatting.GOLD.toString() + PowerTextFormatting
					.formatPowerToString(StaticPowerVoltage.adjustPowerLossByVoltage(StaticPowerVoltage.getVoltageClass(lastProvidedVoltage), properties.powerLoss)).getString()));
		} else {
			output.add(Component.translatable("gui.staticpower.power_loss").append(": ")
					.append(ChatFormatting.GOLD.toString() + PowerTextFormatting.formatPowerToString(properties.powerLoss).getString()).append(" @ ")
					.append(Component.translatable(StaticPowerVoltage.LOW.getShortName())));
		}

		output.add(Component.translatable("gui.staticpower.max_power").append(": ")
				.append(ChatFormatting.GREEN.toString() + PowerTextFormatting.formatPowerRateToString(properties.maxPower).getString()));
		output.add(Component.translatable("gui.staticpower.length").append(": ").append(ChatFormatting.GRAY.toString() + properties.cables().size()));
	}

	public ElectricalPathProperties getPropertiesBetweenPoints(BlockPos start, BlockPos end) {
		if (!CableNetworkManager.get(Network.getWorld()).isTrackingCable(start)) {
			return ElectricalPathProperties.EMPTY;
		}

		if (start.equals(end)) {
			// A single cable connection does not have anypower loss (for gameplay reasons,
			// no need to be too mean).
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(end);
			double maxPower = cable.getDataTag().getDouble(PowerCableComponent.POWER_MAX);
			return new ElectricalPathProperties(0, maxPower, List.of(cable));
		}

		List<Path> paths = Network.getPathCache().getPaths(start, end, getType());
		if (paths.isEmpty()) {
			return ElectricalPathProperties.EMPTY;
		}

		Path path = paths.get(0);
		List<ServerCable> cables = new LinkedList<ServerCable>();
		double cablePowerLoss = 0;
		double maxPowerPerTick = Double.MAX_VALUE;
		for (PathEntry entry : path.getEntries()) {
			// Make sure the cable is not null. The cable will be null when checking the
			// last point in an actual run, as this will be the destination. The reason we
			// don't just check if the entry position != the end is when using the
			// multimeter, you can scan between two cables, not just a cable and a
			// destination.
			ServerCable cable = CableNetworkManager.get(this.Network.getWorld()).getCable(entry.getPosition());
			if (cable != null) {
				cablePowerLoss += (cable.getDataTag().getDouble(PowerCableComponent.POWER_LOSS)) * entry.getDistance();
				if (!cables.contains(cable)) {
					cables.add(cable);
				}

				double cableMaxPower = cable.getDataTag().getDouble(PowerCableComponent.POWER_MAX);
				if (cableMaxPower < maxPowerPerTick) {
					maxPowerPerTick = cableMaxPower;
				}
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

	protected double supplyPower(BlockPos powerSourcePos, BlockPos fromCablePos, PowerStack stack, CachedPowerDestination destination, boolean simulate) {
		// Avoid loops
		if (destination.desintationPos.equals(powerSourcePos)) {
			return 0;
		}

		// Get the resistance between the points. If it is -1, there was no path, return
		// 0.
		ElectricalPathProperties properties = getPropertiesBetweenPoints(fromCablePos, destination.desintationPos);
		if (properties == ElectricalPathProperties.EMPTY) {
			return 0;
		}

		// If the input voltage is higher than the voltage of a cable, break it. Use ALL
		// the power.
		for (ServerCable cable : properties.cables) {
			StaticPowerVoltage voltage = StaticPowerVoltage.values()[cable.getDataTag().getByte(PowerCableComponent.VOLTAGE_ORDINAL)];
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
}
