package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

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
import theking530.staticcore.cablenetwork.pathfinding.Path;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticpower.init.ModCableDestinations;
import theking530.staticpower.init.ModCableModules;
import theking530.staticpower.utilities.MetricConverter;

public class FluidNetworkModule extends CableNetworkModule {
	protected record CachedFluidDestination(IFluidHandler handler, ServerCable cable, BlockPos desintationPos) {
	}

	private FluidStack networkFluid;
	private int totalCapacity;
	private final Map<BlockPos, List<CachedFluidDestination>> destinations;
	private final Map<BlockPos, FluidCableProxy> cableProxies;

	public FluidNetworkModule() {
		super(ModCableModules.Fluid.get());
		networkFluid = FluidStack.EMPTY;
		destinations = new HashMap<>();
		cableProxies = new HashMap<>();
	}

	@Override
	public void preWorldTick(Level world) {
		for (FluidCableProxy firstProxy : cableProxies.values()) {
			firstProxy.clearSuppliedFromDirections();
		}

		// Balance the fluid amounts in the cables.
		balanceCables();
	}

	@Override
	public void tick(Level world) {
		supplyFluid();
	}

	protected void balanceCables() {
		// Sort so we flow from high to low pressure.
		List<FluidCableProxy> storedCables = cableProxies.values().stream().sorted((first, second) -> first.getStored() - second.getStored()).toList();
		for (FluidCableProxy firstProxy : storedCables) {
			if (networkFluid.isEmpty()) {
				break;
			}

			// Skip empty cables.
			if (firstProxy.getStored() <= 0) {
				continue;
			}

			// Create an order of directions such that we first move towards cables that
			// lead to a destination.
			List<Direction> outputDirections = new ArrayList<Direction>();
			Set<Direction> remainingDirections = new HashSet<Direction>();
			remainingDirections.addAll(Arrays.asList(Direction.values()));
			for (CachedFluidDestination dest : destinations.values().stream().flatMap(x -> x.stream()).toList()) {
				List<Path> paths = Network.getPathCache().getPaths(firstProxy.getPos(), dest.desintationPos(), getType());
				if (paths.isEmpty()) {
					continue;
				}

				int fillTest = dest.handler().fill(networkFluid.copy(), FluidAction.SIMULATE);
				if (fillTest <= 0) {
					continue;
				}

				Path path = paths.get(0);
				if (path.getEntries().length > 1) {
					Direction nextDir = path.getEntries()[1].getDirectionOfEntry();
					outputDirections.add(nextDir);
					remainingDirections.remove(nextDir);
				}
			}
			outputDirections.addAll(remainingDirections);

			// Then iterate through the prioritized directions and supply fluid.
			// If the direction leads to another pipe, balance the amount of fluids.
			// If the direction leads to a destination, supply the fluid.
			for (Direction dir : outputDirections) {
				// Don't supply to a direction you just recieved fluid from.
				if (firstProxy.wasSuppliedFromDirection(dir.getOpposite())) {
					continue;
				}

				BlockPos toBalancePos = firstProxy.getPos().relative(dir);
				FluidCableProxy secondProxy = cableProxies.get(toBalancePos);
				if (secondProxy == null) {
					continue;
				}

				int deltaFirstToSecond = firstProxy.getStored() - secondProxy.getStored();
				if (deltaFirstToSecond == 0) {
					continue;
				}

				int sign = (int) Math.signum(deltaFirstToSecond);
				int absoluteDelta = Math.abs(deltaFirstToSecond);
				int amountToBalance = Math.max(absoluteDelta - 1, 1);
				amountToBalance *= sign;

				if (amountToBalance > 0) {
					int drained = firstProxy.drain(amountToBalance, FluidAction.EXECUTE);
					secondProxy.fill(drained, FluidAction.EXECUTE);
					secondProxy.addSuppliedFromDirection(dir);
				}

				if (firstProxy.getStored() <= 0 || networkFluid.isEmpty()) {
					break;
				}
			}
		}
	}

	public void supplyFluid() {
		for (CachedFluidDestination dest : this.destinations.values().stream().flatMap(x -> x.stream()).toList()) {
			FluidCableProxy proxy = this.getFluidProxyAtLocation(dest.cable().getPos());
			if (proxy == null) {
				continue;
			}

			FluidStack simulatedDrained = supply(proxy.getPos(), proxy.getTransferRate(), FluidAction.SIMULATE);
			int provided = dest.handler().fill(simulatedDrained, FluidAction.EXECUTE);
			supply(proxy.getPos(), provided, FluidAction.EXECUTE);
		}
	}

	public FluidCableProxy getFluidProxyAtLocation(BlockPos pos) {
		if (cableProxies.containsKey(pos)) {
			return cableProxies.get(pos);
		}
		return null;
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
		FluidNetworkModule otherModule = other.getModule(ModCableModules.Fluid.get());
		networkFluid = otherModule.networkFluid;
	}

	@Override
	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {
		for (CableNetwork otherNetwork : newNetworks) {
			FluidNetworkModule otherModule = otherNetwork.getModule(ModCableModules.Fluid.get());
			otherModule.networkFluid = networkFluid;
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		totalCapacity = 0;
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			cableProxies.put(cable.getPos(), new FluidCableProxy(cable));
			totalCapacity += cableProxies.get(cable.getPos()).getCapacity();
		}

		for (ServerCable removed : mapper.getRemovedCables()) {
			cableProxies.remove(removed.getPos());
		}

		// Check for cached fluid destinations.
		destinations.clear();
		if (mapper.getDestinations().size() > 0) {
			mapper.getDestinations().forEach((pos, wrapper) -> {
				if (!CableNetworkManager.get(getNetwork().getWorld()).isTrackingCable(wrapper.getFirstConnectedCable())) {
					return;
				}

				// Add all the power interfaces.
				List<CachedFluidDestination> fluidDest = getInterfaceForDesination(wrapper);
				if (fluidDest.size() > 0) {
					destinations.put(pos, fluidDest);
				}
			});
		}
	}

	@Override
	public void getReaderOutput(List<Component> output, BlockPos pos) {
		String storedFluid = new MetricConverter(getStoredFluid().getAmount()).getValueAsString(true);
		String maximumFluid = new MetricConverter(totalCapacity).getValueAsString(true);

		String storedFluidAtPos = new MetricConverter(cableProxies.get(pos).getStored()).getValueAsString(true);
		String capacityAtPos = new MetricConverter(cableProxies.get(pos).getCapacity()).getValueAsString(true);

		output.add(new TextComponent(
				String.format("Network Contains: %1$smB of %2$s out of a maximum of %3$smB.", storedFluid, networkFluid.getDisplayName().getString(), maximumFluid)));
		output.add(new TextComponent(
				String.format("Pipe Contains: %1$smB of %2$s out of a maximum of %3$smB.", storedFluidAtPos, networkFluid.getDisplayName().getString(), capacityAtPos)));
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		networkFluid = FluidStack.loadFluidStackFromNBT(tag.getCompound("fluid"));
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		CompoundTag fluidTag = new CompoundTag();
		networkFluid.writeToNBT(fluidTag);
		tag.put("fluid", fluidTag);
		return tag;
	}

	public FluidStack getStoredFluid() {
		if (networkFluid.isEmpty()) {
			return FluidStack.EMPTY;
		}

		int stored = 0;
		for (FluidCableProxy proxy : this.cableProxies.values()) {
			stored += proxy.getStored();
		}

		FluidStack output = networkFluid.copy();
		output.setAmount(stored);
		return output;
	}

	public FluidStack getStoredFluid(BlockPos pos) {
		if (networkFluid.isEmpty()) {
			return FluidStack.EMPTY;
		}

		FluidCableProxy proxy = getFluidProxyAtLocation(pos);
		if (proxy == null) {
			return FluidStack.EMPTY;
		}

		FluidStack fluid = networkFluid.copy();
		fluid.setAmount(proxy.getStored());
		return fluid;
	}

	public int getCapacity(BlockPos pos) {
		FluidCableProxy proxy = getFluidProxyAtLocation(pos);
		if (proxy == null) {
			return 0;
		}

		return proxy.getCapacity();
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
					output.add(new CachedFluidDestination(handler, Network.getGraph().getCables().get(cablePos), wrapper.getPos()));
				}
			}
		}

		return output;
	}

	public boolean isFluidValid(FluidStack stack) {
		return networkFluid.isEmpty() || networkFluid.getAmount() == 0 || networkFluid.isFluidEqual(stack);
	}

	public int fill(BlockPos fromPos, FluidStack resource, FluidAction action) {
		FluidCableProxy proxyAtPos = this.getFluidProxyAtLocation(fromPos);
		if (proxyAtPos == null) {
			return 0;
		}

		if (isFluidValid(resource)) {
			int filled = proxyAtPos.fill(resource.getAmount(), action);
			if (filled > 0 && action == FluidAction.EXECUTE) {
				if (networkFluid.isEmpty()) {
					networkFluid = resource.copy();
				} else {
					networkFluid.grow(resource.getAmount());
				}
			}
			return filled;
		}

		return 0;
	}

	public FluidStack supply(BlockPos fromPos, int amount, FluidAction action) {
		if (networkFluid.isEmpty()) {
			return FluidStack.EMPTY;
		}

		FluidCableProxy proxyAtPos = this.getFluidProxyAtLocation(fromPos);
		if (proxyAtPos == null) {
			return FluidStack.EMPTY;
		}

		int drained = proxyAtPos.drain(amount, action);
		FluidStack output = networkFluid.copy();
		output.setAmount(drained);

		if (action == FluidAction.EXECUTE) {
			networkFluid.shrink(amount);
		}

		return output;
	}
}
