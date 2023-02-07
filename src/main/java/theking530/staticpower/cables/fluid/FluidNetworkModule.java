package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class FluidNetworkModule extends CableNetworkModule {
	protected record CachedFluidDestination(Direction connectedSide, Cable cable, BlockPos desintationPos) {
	}

	private static final List<Direction> HORIZONTAL_FLOW_PRIORITY = new ArrayList<>();
	private static final List<Direction> FLOW_DIRECTION_PRIORITY = new ArrayList<>();
	static {
		HORIZONTAL_FLOW_PRIORITY.add(Direction.EAST);
		HORIZONTAL_FLOW_PRIORITY.add(Direction.WEST);
		HORIZONTAL_FLOW_PRIORITY.add(Direction.NORTH);
		HORIZONTAL_FLOW_PRIORITY.add(Direction.SOUTH);

		FLOW_DIRECTION_PRIORITY.add(Direction.DOWN);
		FLOW_DIRECTION_PRIORITY.addAll(HORIZONTAL_FLOW_PRIORITY);
		FLOW_DIRECTION_PRIORITY.add(Direction.UP);
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
		// We want to slowly move the target fluid pressure back to 0.
		for (FluidCableCapability cable : fluidCapabilities) {
			cable.setHeadPressure(0);
		}
	}

	@Override
	public void tick(Level world) {
		getNetwork().getWorld().getProfiler().push("FluidNetworkModule.SimulatingFlow");
		{
			getNetwork().getWorld().getProfiler().push("FluidNetworkModule.UpdatingCablePressures");
			updatePressures();
			getNetwork().getWorld().getProfiler().pop();
		}
		{
			getNetwork().getWorld().getProfiler().push("FluidNetworkModule.Balancing");
			simulateBalancing();
			getNetwork().getWorld().getProfiler().pop();
		}
		{
			getNetwork().getWorld().getProfiler().push("FluidNetworkModule.Distribution");
			distributeToDestinations();
			getNetwork().getWorld().getProfiler().pop();
		}
		getNetwork().getWorld().getProfiler().pop();
	}

	public static float getFlowRateForFluid(FluidStack stack) {
		if (stack.isEmpty()) {
			return 1;
		}

		float waterViscocity = Fluids.WATER.getFluidType().getViscosity();
		float ratio = waterViscocity / stack.getFluid().getFluidType().getViscosity();
		return ratio * 2;
	}

	public int fill(BlockPos cablePos, FluidStack resource, FluidAction action) {
		return fill(cablePos, resource, 16, action);
	}

	public int fill(BlockPos cablePos, FluidStack resource, float pressure, FluidAction action) {
		Optional<FluidCableCapability> cable = getFluidCableCapability(cablePos);
		if (cable.isEmpty()) {
			return 0;
		}

		FluidStack maxInput = resource.copy();
		maxInput.setAmount(Math.min(maxInput.getAmount(), cable.get().getCapacity()));
		return simulateFlowFrom(maxInput, pressure, cable.get(), action);
	}

	public FluidStack supply(BlockPos cablePos, int amount, FluidAction action) {
		Optional<FluidCableCapability> cable = getFluidCableCapability(cablePos);
		if (cable.isEmpty()) {
			return FluidStack.EMPTY;
		}
		return cable.get().drain(amount, action);
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		fluidCapabilities.clear();
		destinations.clear();

		for (Cable cable : Network.getGraph().getCables().values()) {
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

		output.add(Component.literal(String.valueOf(capability.get().getHeadPressure())));
		output.addAll(GuiFluidBarUtilities.getTooltip(capability.get().getFluidAmount(), capability.get().getCapacity(), capability.get().getFluid()));
	}

	protected Optional<FluidCableCapability> getFluidCableCapability(BlockPos pos) {
		Cable cable = Network.getGraph().getCables().get(pos);
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

	@Nullable
	protected CachedFluidDestination getInterfaceForDesination(Cable connectedCable, DestinationWrapper wrapper) {
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
			IFluidHandler handler = wrapper.getTileEntity().getCapability(ForgeCapabilities.FLUID_HANDLER, connectedSide).orElse(null);
			if (handler != null) {
				return new CachedFluidDestination(connectedSide, Network.getGraph().getCables().get(connectedCable.getPos()), wrapper.getPos());
			}
		}
		return null;
	}

	protected int simulateFlowFrom(FluidStack fluid, float pressure, FluidCableCapability cable, FluidAction action) {
		return propagateFlow(fluid.copy(), cable, pressure, action, new HashSet<FluidCableCapability>());
	}

	private int propagateFlow(FluidStack fluid, FluidCableCapability cable, float incomingPressure, FluidAction action, Set<FluidCableCapability> visited) {
		// Tries to add the cable to the set. If it fails, that means it was already
		// there, return early.
		if (!visited.add(cable)) {
			return 0;
		}

		cable.setHeadPressure(incomingPressure);

		int filled = 0;

		Map<Direction, FluidCableCapability> adjacents = getAdjacentFluidCapabilities(cable.getPos());

		// Only fill ourselves first if we don't have a cable below us.
		if (!adjacents.containsKey(Direction.DOWN)) {
			// The pressure is factored in during the simulation, so when we execute assume
			// full pressure.
			FluidStack maxFluid = getMaximumFlowFluidStack(fluid, cable, action == FluidAction.EXECUTE ? FluidCableCapability.MAX_PIPE_PRESSURE : incomingPressure);
			if (maxFluid.isEmpty()) {
				return 0;
			}

			filled += cable.fill(maxFluid, action);
		}

		// This fluid gets passed through all the recursive calls directly.
		// It is shrunk whenever it is used.
		fluid.shrink(filled);

		if (adjacents.size() > 0 && !fluid.isEmpty()) {
			for (Direction flowDir : FLOW_DIRECTION_PRIORITY) {
				if (fluid.isEmpty() || !adjacents.containsKey(flowDir)) {
					continue;
				}

				// If there is a cable above us, only supply to it if we're already full.
				if (flowDir == Direction.UP && cable.getFluidAmount() != cable.getCapacity()) {
					continue;
				}

				FluidCableCapability adjacent = adjacents.get(flowDir);
				float pressureDelta = flowDir == Direction.DOWN ? 2.0f : flowDir == Direction.UP ? -2.0f : -1f;
				int supplied = propagateFlow(fluid, adjacent, incomingPressure + pressureDelta, action, visited);
				filled += supplied;
			}
		}

		// When there is a cable below us, only fill if it is already full.
		if (adjacents.containsKey(Direction.DOWN) && !fluid.isEmpty()) {
			FluidCableCapability cableBelow = adjacents.get(Direction.DOWN);
			if (cableBelow.getFluidAmount() == cableBelow.getCapacity()) {
				FluidStack maxFluid = getMaximumFlowFluidStack(fluid, cable, action == FluidAction.EXECUTE ? FluidCableCapability.MAX_PIPE_PRESSURE : incomingPressure);
				filled += cable.fill(maxFluid, action);
			}
		}

		return filled;
	}

	private FluidStack getMaximumFlowFluidStack(FluidStack fluid, FluidCableCapability cable, float pressure) {
		if (fluid.isEmpty() || pressure == 0) {
			return FluidStack.EMPTY;
		}

		// Multiply the pressure by 2.
		float pressureRatio = (pressure * 2) / FluidCableCapability.MAX_PIPE_PRESSURE;
		int maxAmount = (int) (fluid.getAmount() * getFlowRateForFluid(fluid) * pressureRatio);
		maxAmount = (int) SDMath.clamp(pressureRatio * maxAmount, 0, fluid.getAmount());

		// Since we know there is fluid to be flowed, and the pressure is non zero, then
		// the maxAmount being zero implies that the ratio * fluidAmount rounded down to
		// zero. Force this value to be the remaining fluid and continue.
		if (maxAmount == 0) {
			maxAmount = fluid.getAmount();
		}

		FluidStack output = fluid.copy();
		output.setAmount(maxAmount);
		return output;
	}

	private void distributeToDestinations() {
		for (FluidCableCapability cable : fluidCapabilities) {
			if (cable.isEmpty()) {
				continue;
			}

			Map<Direction, CachedFluidDestination> adjacentDestinations = getAdjacentCableDestinations(cable.getPos());
			if (adjacentDestinations.isEmpty()) {
				continue;
			}

			Map<Direction, Float> supplyAmounts = new HashMap<>();
			float totalSupplied = 0;
			for (Direction dir : Direction.values()) {
				int supplied = distributeOnCableSide(cable, adjacentDestinations, cable.getFluidAmount(), dir, FluidAction.SIMULATE);
				supplyAmounts.put(dir, (float) supplied);
				totalSupplied += supplied;
			}
			int initialFluidAmount = cable.getFluidAmount();
			for (Direction dir : supplyAmounts.keySet()) {
				float supplyRatio = supplyAmounts.get(dir) / totalSupplied;
				int toSupplyAmount = (int) (initialFluidAmount * supplyRatio);
				if (toSupplyAmount == 0 && supplyRatio > 0) {
					toSupplyAmount = cable.getFluidAmount();
				}

				distributeOnCableSide(cable, adjacentDestinations, toSupplyAmount, dir, FluidAction.EXECUTE);
			}
		}
	}

	private int distributeOnCableSide(FluidCableCapability cable, Map<Direction, CachedFluidDestination> adjacentDestinations, int maxFluidAmount, Direction side,
			FluidAction action) {
		if (cable.isEmpty() || !adjacentDestinations.containsKey(side) || maxFluidAmount == 0) {
			return 0;
		}

		IFluidHandler handler = getFluidHandler(adjacentDestinations.get(side)).orElse(null);
		if (handler != null) {
			int maxSupply = Math.min(cable.getFluidAmount(), cable.getTransferRate());
			maxSupply = Math.min(maxSupply, maxFluidAmount);

			FluidStack drained = cable.drain(maxSupply, FluidAction.SIMULATE);
			int filled = handler.fill(drained, action);
			if (action == FluidAction.EXECUTE) {
				cable.drain(filled, FluidAction.EXECUTE);
			}
			return filled;
		}
		return 0;
	}

	private LazyOptional<IFluidHandler> getFluidHandler(CachedFluidDestination destination) {
		return getNetwork().getWorld().getBlockEntity(destination.desintationPos()).getCapability(ForgeCapabilities.FLUID_HANDLER, destination.connectedSide());
	}

	private void updatePressures() {
		for (FluidCableCapability cable : fluidCapabilities) {
			cable.updateHeadPressure();
		}
	}

	private void simulateBalancing() {
		getNetwork().getWorld().getProfiler().push("FluidNetworkModule.Balancing");

		for (FluidCableCapability cable : fluidCapabilities) {
			if (cable.isEmpty()) {
				continue;
			}

			Map<Direction, FluidCableCapability> adjacents = getAdjacentFluidCapabilities(cable.getPos());

			// First try to push all the fluid downwards.
			if (adjacents.containsKey(Direction.DOWN)) {
				FluidStack simDrained = cable.drain(cable.getTransferRate(), FluidAction.SIMULATE);
				int filled = adjacents.get(Direction.DOWN).fill(simDrained, FluidAction.EXECUTE);
				cable.drain(filled, FluidAction.EXECUTE);
			}

			if (cable.isEmpty()) {
				continue;
			}

			List<FluidCableCapability> cablesToBalance = new ArrayList<>();
			cablesToBalance.add(cable);

			float totalFluid = cable.getFluidAmount();
			float totalCapacity = cable.getCapacity();
			for (Entry<Direction, FluidCableCapability> adjacent : adjacents.entrySet()) {
				if (adjacent.getKey() == Direction.UP) {
					continue;
				}
				totalFluid += adjacent.getValue().getFluidAmount();
				totalCapacity += adjacent.getValue().getCapacity();
				cablesToBalance.add(adjacent.getValue());
			}

			float targetRatio = totalFluid / totalCapacity;

			FluidStack toDistribute = FluidStack.EMPTY;
			for (FluidCableCapability adjacent : cablesToBalance) {
				int targetValue = (int) (targetRatio * adjacent.getCapacity());
				if (targetValue == 0 && targetRatio > 0) {
					targetValue = 1;
				}

				int delta = adjacent.getFluidAmount() - targetValue;

				if (delta > 0) {
					if (toDistribute.isEmpty()) {
						toDistribute = adjacent.getFluid().copy();
						toDistribute.setAmount(delta);
					} else {
						toDistribute.grow(delta);
					}
				}
			}

			int initialToDistributeAmount = toDistribute.getAmount();

			for (FluidCableCapability adjacent : cablesToBalance) {
				int targetValue = (int) (targetRatio * adjacent.getCapacity());
				if (targetValue == 0 && targetRatio > 0) {
					targetValue = 1;
				}

				int delta = adjacent.getFluidAmount() - targetValue;
				int maxDelta = (int) (delta * 0.1f);
				if (maxDelta == 0 && Math.abs(delta) > 0) {
					maxDelta = delta;
				}

				if (toDistribute.isEmpty()) {
					break;
				}

				if (delta < 0) {
					int maxCanSupply = Math.min(toDistribute.getAmount(), -maxDelta);
					FluidStack toSupply = cable.getFluid().copy();
					toSupply.setAmount(maxCanSupply);
					int filled = adjacent.fill(toSupply, FluidAction.EXECUTE);
					toDistribute.shrink(filled);
				}
			}

			int usedFluid = initialToDistributeAmount - toDistribute.getAmount();

			for (FluidCableCapability adjacent : cablesToBalance) {
				int fluidAmount = adjacent.getFluidAmount();
				int toDrain = Math.min(fluidAmount, usedFluid);
				adjacent.drain(toDrain, FluidAction.EXECUTE);
				usedFluid -= toDrain;

				if (usedFluid == 0) {
					break;
				}
			}
		}

		getNetwork().getWorld().getProfiler().pop();
	}

}
