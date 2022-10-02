package theking530.staticpower.cables.fluid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
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

	// Map of cable pos to connected destinations.
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
			world.getProfiler().push("FluidNetworkModule.Balancing.Fluid");
			simulateFlow();
			world.getProfiler().pop();
		}
		world.getProfiler().pop();

	}

	@Override
	public void tick(Level world) {
	}

	protected void simulateFlow() {
		// Sort so we flow from high to low pressure.
		if (!fluidCapabilities.isEmpty()) {
			List<FluidCableCapability> storedCables = fluidCapabilities.stream()
					.sorted((first, second) -> (int) Math.signum(second.getFluidStorage().getFluidAmount() - first.getFluidStorage().getFluidAmount())).toList();

			simulate(storedCables.get(0), new HashSet<BlockPos>());

			for (FluidCableCapability cap : fluidCapabilities) {
				cap.getFluidStorage().captureFluidMetrics();
			}
		}
	}

	protected void simulate(FluidCableCapability cable, Set<BlockPos> visited) {
		if (visited.contains(cable.getPos())) {
			return;
		}
		visited.add(cable.getPos());

		Map<Direction, FluidCableCapability> adjacents = getAdjacentFluidCapabilities(cable.getPos());
		if (adjacents.isEmpty()) {
			return;
		}

		// If the cable is empty, just recurse and leave.
		if (cable.getFluidStorage().isEmpty()) {
			for (Direction dir : Direction.values()) {
				if (adjacents.containsKey(dir)) {
					simulate(adjacents.get(dir), visited);
				}
			}
			return;
		}

		Map<Direction, CachedFluidDestination> destinations = getAdjacentCableDestinations(cable.getPos());

		// Get the total amount of fluid we could possibly output.
		FluidStack maxOutput = cable.getFluidStorage().getFluid().copy();
		float totalPotentialOutput = 0;
		Map<Direction, Integer> outputRatios = new HashMap<>();
		for (Direction dir : Direction.values()) {
			if (!visited.contains(cable.getPos().relative(dir))) {
				int simulateSupply = 0;
				if (destinations.containsKey(dir)) {
					simulateSupply = supplyToDestination(cable, cable.getTransferRate(), destinations.get(dir), FluidAction.SIMULATE);
					outputRatios.put(dir, simulateSupply);
				} else if (adjacents.containsKey(dir)) {
					simulateSupply = adjacents.get(dir).fill(dir, 16, maxOutput, FluidAction.SIMULATE);
					outputRatios.put(dir, simulateSupply);
				}
				totalPotentialOutput += simulateSupply;
			}
		}

		int initialAmount = cable.getFluidStorage().getFluidAmount();
		for (Direction dir : Direction.values()) {
			if (!outputRatios.containsKey(dir)) {
				continue;
			}

			float supplyRatio = outputRatios.get(dir) / totalPotentialOutput;
			int maxSupply = (int) (initialAmount * supplyRatio);
			if (maxSupply == 0 && supplyRatio != 0 && cable.getFluidStorage().getFluidAmount() > 0) {
				maxSupply = cable.getFluidStorage().getFluidAmount();
			}

			if (destinations.containsKey(dir)) {
				if (!cable.getFluidStorage().isEmpty()) {
					supplyToDestination(cable, maxSupply, destinations.get(dir), FluidAction.EXECUTE);
				}
			} else if (adjacents.containsKey(dir)) {
				FluidCableCapability adjacent = adjacents.get(dir);
				if (!visited.contains(adjacent.getPos())) {
					int delta = cable.getFluidStorage().getFluidAmount() - adjacent.getFluidStorage().getFluidAmount();
					if (delta > 0) {
						delta = Math.min(delta, maxSupply);

						FluidStack simulatedDrain = cable.drain(delta, FluidAction.SIMULATE);
						int supplied = adjacent.fill(dir, 16, simulatedDrain, FluidAction.EXECUTE);
						cable.drain(supplied, FluidAction.EXECUTE);
					}
					simulate(adjacent, visited);
				}
			}
		}
	}

	protected int supplyToDestination(FluidCableCapability cable, int maxSupply, CachedFluidDestination destination, FluidAction action) {
		if (cable.getFluidStorage().isEmpty()) {
			return 0;
		}

		IFluidHandler handler = cable.getOwningCable().getWorld().getBlockEntity(destination.desintationPos())
				.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.connectedSide()).orElse(null);
		if (handler != null) {
			// Create the stack to supply.
			FluidStack toSupply = cable.getFluidStorage().getFluid().copy();
			toSupply.setAmount(Math.min(toSupply.getAmount(), cable.getTransferRate()));
			toSupply.setAmount(Math.min(toSupply.getAmount(), maxSupply));

			int simulatedFill = handler.fill(toSupply, FluidAction.SIMULATE);
			FluidStack actualDrain = cable.drain(simulatedFill, action);
			return handler.fill(actualDrain, action);
		}
		return 0;
	}

	public int fill(BlockPos fromPos, Direction fromSide, FluidStack resource, FluidAction action) {
		Optional<FluidCableCapability> capability = this.getFluidCableCapability(fromPos);
		if (capability.isEmpty()) {
			return 0;
		}
		int filled = capability.get().fill(fromSide, 32, resource, action);
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

	public Optional<FluidCableCapability> getFluidCableCapability(BlockPos pos) {
		ServerCable cable = Network.getGraph().getCables().get(pos);
		if (cable == null) {
			return Optional.empty();
		}

		return cable.getCapability(ModCableCapabilities.Fluid.get());
	}

	protected Map<Direction, FluidCableCapability> getAdjacentFluidCapabilities(BlockPos pos) {
		Map<Direction, FluidCableCapability> output = new HashMap<>();
		for (Direction dir : Direction.values()) {
			BlockPos testPos = pos.relative(dir);
			Optional<FluidCableCapability> adjacentCable = getFluidCableCapability(testPos);
			if (adjacentCable.isEmpty()) {
				continue;
			}

			if (adjacentCable.get().getOwningCable().isDisabledOnSide(dir.getOpposite())) {
				continue;
			}

			if (Network.getGraph().getCables().get(testPos).isDisabledOnSide(dir.getOpposite())) {
				continue;
			}

			output.put(dir, adjacentCable.get());
		}
		return output;
	}

	protected Map<Direction, CachedFluidDestination> getAdjacentCableDestinations(BlockPos pos) {
		List<CachedFluidDestination> adjacentDestinations = destinations.get(pos);
		if (adjacentDestinations == null || adjacentDestinations.isEmpty()) {
			return new HashMap<>();
		}

		Map<Direction, CachedFluidDestination> adjacencyMap = new HashMap<>();
		for (CachedFluidDestination adjacent : adjacentDestinations) {
			adjacencyMap.put(adjacent.connectedSide().getOpposite(), adjacent);
		}
		return adjacencyMap;
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		fluidCapabilities.clear();
		destinations.clear();

		for (ServerCable cable : Network.getGraph().getCables().values()) {
			Optional<FluidCableCapability> capability = cable.getCapability(ModCableCapabilities.Fluid.get());
			if (capability.isPresent()) {
				fluidCapabilities.add(capability.get());

				// Cache all the destinations adjacent to this cable.
				List<CachedFluidDestination> fluidDest = new LinkedList<>();
				for (Direction dir : Direction.values()) {
					BlockPos testPos = cable.getPos().relative(dir);

					if (mapper.getDestinations().containsKey(testPos)) {
						CachedFluidDestination dest = getInterfaceForDesination(cable, mapper.getDestinations().get(testPos));
						if (dest != null) {
							fluidDest.add(dest);
						}
					}
				}
				destinations.put(cable.getPos(), fluidDest);
			}
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
	public CachedFluidDestination getInterfaceForDesination(ServerCable connectedCable, DestinationWrapper wrapper) {
		if (!wrapper.getConnectedCables().containsKey(connectedCable.getPos())) {
			return null;
		}
		// Skip NON tile entity destinations.
		if (!wrapper.hasTileEntity()) {
			return null;
		}

		// Get the cable pos.
		Direction connectedSide = wrapper.getConnectedCables().get(connectedCable.getPos());

		if (wrapper.supportsType(ModCableDestinations.Fluid.get())) {
			IFluidHandler handler = wrapper.getTileEntity().getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, connectedSide).orElse(null);
			if (handler != null) {
				return new CachedFluidDestination(connectedSide, Network.getGraph().getCables().get(connectedCable.getPos()), wrapper.getPos());
			}
		}
		return null;
	}

	protected float calculateDeltaPressure(float currentPressure, float targetPressure) {
		float deltaPressure = targetPressure - currentPressure;
		if (deltaPressure == 0) {
			return 0;
		}

		int sign = (int) Math.signum(deltaPressure);
		float absoluteDeltaPressure = Math.abs(deltaPressure);
		float deltaToApply = Math.min(absoluteDeltaPressure, 1);
		return deltaToApply * sign;
	}
}
