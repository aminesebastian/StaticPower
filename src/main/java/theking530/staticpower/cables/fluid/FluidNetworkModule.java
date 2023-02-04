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
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.cablenetwork.Cable;
import theking530.staticcore.cablenetwork.data.DestinationWrapper;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticcore.gui.widgets.valuebars.GuiFluidBarUtilities;
import theking530.staticpower.init.cables.ModCableCapabilities;
import theking530.staticpower.init.cables.ModCableDestinations;
import theking530.staticpower.init.cables.ModCableModules;

public class FluidNetworkModule extends CableNetworkModule {
	protected record CachedFluidDestination(Direction connectedSide, Cable cable, BlockPos desintationPos) {
	}

	private static final List<Direction> FLOW_DIRECTION_PRIORITY = new ArrayList<>();
	static {
		FLOW_DIRECTION_PRIORITY.add(Direction.DOWN);
		FLOW_DIRECTION_PRIORITY.add(Direction.EAST);
		FLOW_DIRECTION_PRIORITY.add(Direction.WEST);
		FLOW_DIRECTION_PRIORITY.add(Direction.NORTH);
		FLOW_DIRECTION_PRIORITY.add(Direction.DOWN);
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
			cable.setTargetPressure(0);
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

	public int fill(BlockPos cablePos, Direction fromDirection, FluidStack resource, FluidAction action) {
		Optional<FluidCableCapability> cable = getFluidCableCapability(cablePos);
		if (cable.isEmpty()) {
			return 0;
		}

		return simulateFlowFrom(resource, 16, cable.get(), action);
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

		output.add(Component.literal(String.valueOf(capability.get().getPressure())));
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
		return propagateFlow(fluid, cable, pressure, action, new HashSet<FluidCableCapability>());
	}

	private int propagateFlow(FluidStack fluid, FluidCableCapability cable, float incomingPressure, FluidAction action, Set<FluidCableCapability> visited) {
		// Tries to add the cable to the set. If it fails, that means it was already
		// there, return early.
		if (!visited.add(cable)) {
			return 0;
		}

		cable.setTargetPressure(incomingPressure);

		if (incomingPressure <= 0) {
			return 0;
		}

		if (fluid.isEmpty()) {
			return 0;
		}

		float pressureRatio = incomingPressure / FluidCableCapability.MAX_PIPE_PRESSURE;
		int maxAmount = action == FluidAction.EXECUTE ? fluid.getAmount() : (int) (pressureRatio * Math.min(fluid.getAmount(), fluid.getAmount()));

		FluidStack maxFluid = fluid.copy();
		maxFluid.setAmount(maxAmount);

		if (fluid.getAmount() > 0 && maxAmount == 0) {
			maxFluid.setAmount(1);
		}

		if (maxFluid.getAmount() == 0) {
			return 0;
		}

		int filled = cable.fill(maxFluid, action);
		Map<Direction, FluidCableCapability> adjacents = getAdjacentFluidCapabilities(cable.getPos());

		if (adjacents.size() > 0) {
			FluidStack remainingFluid = fluid.copy();
			remainingFluid.shrink(filled);
			if (!remainingFluid.isEmpty()) {
				for (Direction flowDir : FLOW_DIRECTION_PRIORITY) {
					if (!adjacents.containsKey(flowDir)) {
						continue;
					}

					if (flowDir == Direction.UP && cable.getFluidAmount() != cable.getCapacity()) {
						continue;
					}

					FluidCableCapability adjacent = adjacents.get(flowDir);
					if (flowDir == Direction.DOWN) {
						filled += propagateFlow(remainingFluid, adjacent, incomingPressure + 1, action, visited);
					} else {
						filled += propagateFlow(remainingFluid, adjacent, incomingPressure - 1, action, visited);
					}
				}
			}
		}

		fluid.shrink(filled);
		return filled;
	}

	private void distributeToDestinations() {
		for (FluidCableCapability cable : fluidCapabilities) {
			Map<Direction, CachedFluidDestination> destinations = this.getAdjacentCableDestinations(cable.getPos());

			for (Direction destDir : FLOW_DIRECTION_PRIORITY) {
				if (!destinations.containsKey(destDir)) {
					continue;
				}

				CachedFluidDestination dest = destinations.get(destDir);
				FluidStack maxSupply = cable.getFluid().copy();
				if (maxSupply.isEmpty()) {
					continue;
				}

				IFluidHandler handler = getNetwork().getWorld().getBlockEntity(dest.desintationPos()).getCapability(ForgeCapabilities.FLUID_HANDLER, dest.connectedSide())
						.orElse(null);
				if (handler != null) {
					maxSupply.setAmount(Math.min(maxSupply.getAmount(), cable.getTransferRate()));
					int filled = handler.fill(maxSupply, FluidAction.EXECUTE);
					cable.drain(filled, FluidAction.EXECUTE);
				}
			}
		}
	}

	private void updatePressures() {
		for (FluidCableCapability cable : fluidCapabilities) {
			cable.updatePressure();
		}
	}

	private void simulateBalancing() {
		getNetwork().getWorld().getProfiler().push("FluidNetworkModule.InterpolatingFlowRate");

		getNetwork().getWorld().getProfiler().pop();

		getNetwork().getWorld().getProfiler().push("FluidNetworkModule.Balancing");

		for (FluidCableCapability cable : fluidCapabilities) {
			if (cable.isEmpty()) {
				continue;
			}

			Map<Direction, FluidCableCapability> adjacents = getAdjacentFluidCapabilities(cable.getPos());
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
