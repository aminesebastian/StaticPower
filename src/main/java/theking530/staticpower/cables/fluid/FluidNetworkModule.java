package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;
import theking530.staticpower.utilities.MetricConverter;

public class FluidNetworkModule extends CableNetworkModule {
	protected record CachedFluidDestination(Direction connectedSide, ServerCable cable, BlockPos desintationPos) {
	}

	private static float PRESSURE_BALANCING_RATE = 1f;

	private final Map<BlockPos, List<CachedFluidDestination>> destinations;
	private final List<FluidCableCapability> fluidCapabilities;

	public FluidNetworkModule() {
		super(ModCableModules.Fluid.get());
		destinations = new HashMap<>();
		fluidCapabilities = new LinkedList<>();
	}

	@Override
	public void preWorldTick(Level world) {
		world.getProfiler().push("FluidNetworkModule.Balancing");
		{
			world.getProfiler().push("FluidNetworkModule.Balancing.Pressure");
			balancePressures();
			world.getProfiler().pop();
		}
		{
			world.getProfiler().push("FluidNetworkModule.Balancing.Fluid");
			balanceCables();
			world.getProfiler().pop();
		}
		world.getProfiler().pop();
	}

	@Override
	public void tick(Level world) {
		for (FluidCableCapability capability : fluidCapabilities) {
			capability.getFluidStorage().captureFluidMetrics();
		}

		world.getProfiler().push("FluidNetworkModule.Supplying");
		supplyFluid();
		world.getProfiler().pop();
	}

	protected void balancePressures() {
		// First, establish the low pressure cables.
		for (CachedFluidDestination destination : destinations.values().stream().flatMap(x -> x.stream()).toList()) {
			Optional<FluidCableCapability> destinationCable = getFluidCableCapability(destination.cable().getPos());
			if (destinationCable.isEmpty()) {
				continue;
			}

			IFluidHandler handler = Network.getWorld().getBlockEntity(destination.desintationPos())
					.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.connectedSide()).orElse(null);
			if (handler == null) {
				continue;
			}

			if (handler.fill(destinationCable.get().getFluidStorage().getFluid().copy(), FluidAction.SIMULATE) > 0) {
				destinationCable.get().setPressure(destinationCable.get().getPressure() + calculateDeltaPressure(destinationCable.get().getPressure(), 0));
			}
		}

		// Then average out the rest.
		for (FluidCableCapability capability : fluidCapabilities) {
			float totalPressure = capability.getPressure();
			int count = 1;
			for (FluidCableCapability adjacent : this.getAdjacentCableProxies(capability.getPos())) {
				totalPressure += adjacent.getPressure();
				count++;
			}
			capability.setPressure(capability.getPressure() + calculateDeltaPressure(capability.getPressure(), totalPressure / count));

		}
	}

	protected void balanceCables() {
		// Sort so we flow from high to low pressure.
		List<FluidCableCapability> storedCables = fluidCapabilities.stream().sorted((first, second) -> (int) (second.getPressure() - first.getPressure())).toList();

		for (FluidCableCapability capability : storedCables) {
			if (capability.getFluidStorage().isEmpty()) {
				continue;
			}

			// Capture the sides with cables that can take fluid, and how much they can
			// take.
			List<FluidCableCapability> adjacentCables = getAdjacentCableProxies(capability.getPos());
			Map<FluidCableCapability, Integer> validAdjacentCables = new HashMap<>();
			float totalDelta = 0;
			for (int i = 0; i < adjacentCables.size(); i++) {
				FluidCableCapability adjacentCable = adjacentCables.get(i);

				// Leave early if we've run out of fluid.
				if (capability.getFluidStorage().isEmpty()) {
					break;
				}

				if (adjacentCable.getPressure() > capability.getPressure()) {
					continue;
				}

				int deltaFirstToSecond = capability.getFluidStorage().getFluidAmount() - adjacentCable.getFluidStorage().getFluidAmount();
				if (deltaFirstToSecond <= 0) {
					continue;
				}
				validAdjacentCables.put(adjacentCable, deltaFirstToSecond);
				totalDelta += deltaFirstToSecond;
			}

			for (FluidCableCapability adjacentCable : validAdjacentCables.keySet()) {
				int absoluteDelta = validAdjacentCables.get(adjacentCable);
				float ratio = absoluteDelta / totalDelta;
				absoluteDelta *= ratio;
				if (absoluteDelta > 0) {
					FluidStack simulatedDrain = capability.drain(absoluteDelta, FluidAction.SIMULATE);
					int filled = adjacentCable.fill(simulatedDrain, adjacentCable.getPressure(), FluidAction.EXECUTE);
					capability.drain(filled, FluidAction.EXECUTE);
				}
			}
		}
	}

	protected void supplyFluid() {
		for (FluidCableCapability capability : fluidCapabilities) {
			if (capability.getFluidStorage().isEmpty()) {
				continue;
			}

			// Capture all the sides on this cable that have a destination.
			List<Direction> sidesWithDest = new LinkedList<>();
			for (Direction dir : Direction.values()) {
				BlockPos destPos = capability.getPos().relative(dir);
				if (Network.getWorld().getBlockEntity(destPos) == null) {
					continue;
				}
				if (destinations.containsKey(destPos) && !Network.getGraph().getCables().get(capability.getPos()).isDisabledOnSide(dir)) {
					sidesWithDest.add(dir);
				}
			}

			// Supply to the all the sides with a destination.
			supplyFluidToSides(capability, sidesWithDest);
		}
	}

	protected void supplyFluidToSides(FluidCableCapability capability, List<Direction> sidesWithDest) {
		if (sidesWithDest.isEmpty() || capability.getFluidStorage().isEmpty()) {
			return;
		}

		// Determine how much fluid we could AT MAXIMUM supply.
		FluidStack maxSupply = capability.drain(capability.getTransferRate(), FluidAction.SIMULATE);

		// For each of those destinations, see how much fluid they actually need (if
		// any). Also track the total amount of fluid we would like to distribute.
		Map<IFluidHandler, Integer> validDestinations = new HashMap<>();
		float totalSupply = 0;
		for (Direction sideWithDest : sidesWithDest) {
			if (capability.getFluidStorage().isEmpty()) {
				break;
			}

			BlockPos destPos = capability.getPos().relative(sideWithDest);
			List<CachedFluidDestination> blockDestinations = destinations.get(destPos);
			for (CachedFluidDestination dest : blockDestinations) {
				// Destinations can have multiple cables attached. Make sure we're checking for
				// the actual cable we're currently supplying from.
				if (dest.cable().getPos() == capability.getPos()) {
					IFluidHandler handler = Network.getWorld().getBlockEntity(dest.desintationPos())
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dest.connectedSide()).orElse(null);
					if (handler != null) {
						int simulatedFill = handler.fill(maxSupply.copy(), FluidAction.SIMULATE);
						if (simulatedFill > 0) {
							totalSupply += simulatedFill;
							validDestinations.put(handler, simulatedFill);
						}
					}
				}
			}
		}

		// For each of the destinations that wanted fluid, give them the proportionate
		// amount of the fluid stored in our cable.
		for (IFluidHandler output : validDestinations.keySet()) {
			int absoluteDelta = validDestinations.get(output);
			float ratio = absoluteDelta / totalSupply;
			absoluteDelta *= ratio;

			FluidStack canSupply = capability.drain(absoluteDelta, FluidAction.SIMULATE);
			int filled = output.fill(canSupply, FluidAction.EXECUTE);
			capability.drain(filled, FluidAction.EXECUTE);
		}
	}

	protected FluidStack supplyFluidToDestination(FluidCableCapability capability, CachedFluidDestination destination, FluidStack toSupply) {
		IFluidHandler handler = Network.getWorld().getBlockEntity(destination.desintationPos())
				.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.connectedSide()).orElse(null);
		if (handler != null) {
			int filled = handler.fill(toSupply, FluidAction.EXECUTE);
			return capability.drain(filled, FluidAction.EXECUTE);
		}
		return FluidStack.EMPTY;
	}

	public Optional<FluidCableCapability> getFluidCableCapability(BlockPos pos) {
		ServerCable cable = Network.getGraph().getCables().get(pos);
		if (cable == null) {
			return Optional.empty();
		}

		return cable.getCapability(ModCableCapabilities.Fluid.get());
	}

	protected List<FluidCableCapability> getAdjacentCableProxies(BlockPos pos) {
		List<FluidCableCapability> output = new LinkedList<FluidCableCapability>();
		for (Direction dir : Direction.values()) {
			BlockPos testPos = pos.relative(dir);
			Optional<FluidCableCapability> adjacentCable = getFluidCableCapability(testPos);
			if (adjacentCable.isEmpty()) {
				continue;
			}

			if (Network.getGraph().getCables().get(testPos).isDisabledOnSide(dir.getOpposite())) {
				continue;
			}

			output.add(adjacentCable.get());
		}
		return output;
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
	}

	@Override
	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		fluidCapabilities.clear();
		for (ServerCable cable : Network.getGraph().getCables().values()) {
			Optional<FluidCableCapability> capability = cable.getCapability(ModCableCapabilities.Fluid.get());
			if (capability.isPresent()) {
				fluidCapabilities.add(capability.get());
			}
		}

		// Check for cached fluid destinations.
		destinations.clear();
		if (mapper.getDestinations().size() > 0) {
			mapper.getDestinations().forEach((pos, wrapper) -> {
				if (!CableNetworkManager.get(getNetwork().getWorld()).isTrackingCable(wrapper.getFirstConnectedCable())) {
					return;
				}

				List<CachedFluidDestination> fluidDest = getInterfaceForDesination(wrapper);
				if (fluidDest.size() > 0) {
					destinations.put(pos, fluidDest);
				}
			});
		}
	}

	@Override
	public void getReaderOutput(List<Component> output, BlockPos pos) {
		Optional<FluidCableCapability> capability = this.getFluidCableCapability(pos);
		if (capability.isEmpty()) {
			return;
		}

		String storedFluidAtPos = new MetricConverter(capability.get().getFluidStorage().getFluidAmount()).getValueAsString(true);
		String capacityAtPos = new MetricConverter(capability.get().getFluidStorage().getCapacity()).getValueAsString(true);
		String gainedPerTick = GuiTextUtilities.formatFluidRateToString(capability.get().getFluidStorage().getFilledPerTick()).getString();
		String drainedPerTick = GuiTextUtilities.formatFluidRateToString(capability.get().getFluidStorage().getDrainedPerTick()).getString();

		output.add(new TextComponent(String.format("Pipe Contains: %1$smB of %2$s out of a maximum of %3$smB.", storedFluidAtPos,
				capability.get().getFluidStorage().getFluid().getDisplayName().getString(), capacityAtPos)));
		output.add(new TextComponent(String.format("Gained: %1$smB Drained: %2$s", drainedPerTick, gainedPerTick)));
	}

	@Nullable
	public List<CachedFluidDestination> getInterfaceForDesination(DestinationWrapper wrapper) {
		// Allocate the output.
		List<CachedFluidDestination> output = new ArrayList<CachedFluidDestination>();

		// Iterate through all the connected cables.
		for (BlockPos cablePos : wrapper.getConnectedCables().keySet()) {
			// Get the cable pos.
			Direction connectedSide = wrapper.getConnectedCables().get(cablePos);

			// Skip NON tile entity destinations.
			if (!wrapper.hasTileEntity()) {
				continue;
			}

			if (wrapper.supportsType(ModCableDestinations.Fluid.get())) {
				IFluidHandler handler = wrapper.getTileEntity().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, connectedSide).orElse(null);
				if (handler != null) {
					output.add(new CachedFluidDestination(connectedSide, Network.getGraph().getCables().get(cablePos), wrapper.getPos()));
				}
			}
		}

		return output;
	}

	protected void propagatePressure(BlockPos pos, float pressure, Set<BlockPos> visited) {
		if (pressure <= 0 || visited.contains(pos)) {
			return;
		}

		Optional<FluidCableCapability> capability = getFluidCableCapability(pos);
		if (capability.isEmpty()) {
			return;
		}

		capability.get().setPressure(capability.get().getPressure() + calculateDeltaPressure(capability.get().getPressure(), pressure));
		visited.add(pos);

		for (FluidCableCapability adjacent : getAdjacentCableProxies(pos)) {
			propagatePressure(adjacent.getPos(), pressure - 1, visited);
		}
	}

	protected float calculateDeltaPressure(float currentPressure, float targetPressure) {
		float deltaPressure = targetPressure - currentPressure;
		if (deltaPressure == 0) {
			return 0;
		}

		int sign = (int) Math.signum(deltaPressure);
		float absoluteDeltaPressure = Math.abs(deltaPressure);
		float deltaToApply = Math.min(absoluteDeltaPressure, PRESSURE_BALANCING_RATE);
		return deltaToApply * sign;
	}

	public int fill(BlockPos fromPos, FluidStack resource, FluidAction action) {
		Optional<FluidCableCapability> capability = this.getFluidCableCapability(fromPos);
		if (capability.isEmpty()) {
			return 0;
		}

		int filled = capability.get().fill(resource, 0, action);
		propagatePressure(fromPos, 32, new HashSet<BlockPos>());
		return filled;
	}

	public FluidStack supply(BlockPos fromPos, int amount, FluidAction action) {
		if (amount == 0) {
			return FluidStack.EMPTY;
		}

		Optional<FluidCableCapability> capability = this.getFluidCableCapability(fromPos);
		if (capability.isEmpty()) {
			return FluidStack.EMPTY;
		}

		return capability.get().drain(amount, action);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {

	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		return super.writeToNbt(tag);
	}

}
