package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
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
import theking530.staticpower.init.ModCableDestinations;
import theking530.staticpower.init.ModCableModules;
import theking530.staticpower.utilities.MetricConverter;
import theking530.staticpower.utilities.NBTUtilities;

public class FluidNetworkModule extends CableNetworkModule {
	protected record CachedFluidDestination(Direction connectedSide, ServerCable cable, BlockPos desintationPos) {
	}

	private static float PRESSURE_BALANCING_RATE = 1f;

	private final Map<BlockPos, List<CachedFluidDestination>> destinations;
	private final Map<BlockPos, FluidCableProxy> cableProxies;

	public FluidNetworkModule() {
		super(ModCableModules.Fluid.get());
		destinations = new HashMap<>();
		cableProxies = new HashMap<>();
	}

	@Override
	public void preWorldTick(Level world) {
		balancePressures();
		balanceCables();
	}

	@Override
	public void tick(Level world) {
		supplyFluid();
		for (FluidCableProxy cableProxy : cableProxies.values()) {
			cableProxy.fluidStorage.captureFluidMetrics();
		}
	}

	protected void balancePressures() {
		// First, establish the low pressure cables.
		for (CachedFluidDestination cableDestination : destinations.values().stream().flatMap(x -> x.stream()).toList()) {
			FluidCableProxy proxy = this.getFluidProxyAtLocation(cableDestination.cable().getPos());
			if (proxy == null) {
				continue;
			}

			IFluidHandler handler = Network.getWorld().getBlockEntity(cableDestination.desintationPos())
					.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, cableDestination.connectedSide()).orElse(null);
			if (handler == null) {
				continue;
			}

			if (handler.fill(new FluidStack(Fluids.WATER, 100), FluidAction.SIMULATE) > 0) {
				proxy.setPressure(proxy.getPressure() + calculateDeltaPressure(proxy.getPressure(), 0));
			}
		}

		// Then average out the rest.
		for (FluidCableProxy cableProxy : cableProxies.values()) {
			float totalPressure = cableProxy.getPressure();
			int count = 1;
			for (FluidCableProxy adjacent : this.getAdjacentCableProxies(cableProxy.getPos())) {
				totalPressure += adjacent.getPressure();
				count++;
			}
			cableProxy.setPressure(cableProxy.getPressure() + calculateDeltaPressure(cableProxy.getPressure(), totalPressure / count));

		}
	}

	protected List<FluidCableProxy> getAdjacentCableProxies(BlockPos pos) {
		List<FluidCableProxy> output = new LinkedList<FluidCableProxy>();
		for (Direction dir : Direction.values()) {
			BlockPos testPos = pos.relative(dir);
			FluidCableProxy adjacentCable = getFluidProxyAtLocation(testPos);
			if (adjacentCable == null) {
				continue;
			}

			if (Network.getGraph().getCables().get(testPos).isDisabledOnSide(dir.getOpposite())) {
				continue;
			}

			output.add(adjacentCable);
		}
		return output;
	}

	protected void balanceCables() {
		// Sort so we flow from high to low pressure.
		List<FluidCableProxy> storedCables = cableProxies.values().stream().sorted((first, second) -> (int) (second.getPressure() - first.getPressure())).toList();

		for (FluidCableProxy cableProxy : storedCables) {
			if (cableProxy.getStored().isEmpty()) {
				continue;
			}

			// Capture the sides with cables.
			List<FluidCableProxy> adjacentCables = getAdjacentCableProxies(cableProxy.getPos());

			for (int i = 0; i < adjacentCables.size(); i++) {
				FluidCableProxy adjacentCable = adjacentCables.get(i);

				// Leave early if we've run out of fluid.
				if (cableProxy.getStored().isEmpty()) {
					break;
				}

				if (adjacentCable.getPressure() > cableProxy.getPressure()) {
					continue;
				}

				int deltaFirstToSecond = cableProxy.getStored().getAmount() - adjacentCable.getStored().getAmount();
				if (deltaFirstToSecond <= 0) {
					continue;
				}

				int sign = (int) Math.signum(deltaFirstToSecond);
				int absoluteDelta = Math.abs(deltaFirstToSecond);
				int amountToBalance = i == adjacentCables.size() - 1 ? absoluteDelta : absoluteDelta / adjacentCables.size(); // TODO: Fix the balancing around multiple branches so
																																// as to not waste any
				// thoroughput.
				amountToBalance *= sign;

				if (amountToBalance > 0) {
					FluidStack drained = cableProxy.drain(amountToBalance, FluidAction.EXECUTE);
					adjacentCable.fill(drained, adjacentCable.getPressure(), FluidAction.EXECUTE);
				}
			}
		}
	}

	protected void supplyFluid() {
		for (FluidCableProxy cableProxy : cableProxies.values()) {
			if (cableProxy.getStored().isEmpty()) {
				continue;
			}

			// Capture all the sides on this cable that have a destination.
			List<Direction> sidesWithDest = new LinkedList<>();
			for (Direction dir : Direction.values()) {
				BlockPos destPos = cableProxy.getPos().relative(dir);
				if (Network.getWorld().getBlockEntity(destPos) == null) {
					continue;
				}
				if (destinations.containsKey(destPos) && !Network.getGraph().getCables().get(cableProxy.getPos()).isDisabledOnSide(dir)) {
					sidesWithDest.add(dir);
				}
			}

			// Supply to the all the sides with a destination.
			supplyFluidToSides(cableProxy, sidesWithDest);
		}
	}

	protected void supplyFluidToSides(FluidCableProxy cableProxy, List<Direction> sidesWithDest) {
		if (sidesWithDest.isEmpty() || cableProxy.getStored().isEmpty()) {
			return;
		}

		// Determine the max amount of fluid we can supply.
		// Allocate a tracker to see how much is left to supply to account for non
		// integer division.
		FluidStack maxSupply = cableProxy.drain(cableProxy.getTransferRate(), FluidAction.SIMULATE);
		int maxPerDest = maxSupply.getAmount();
		if (maxPerDest >= sidesWithDest.size()) {
			// maxPerDest /= sidesWithDest.size();
		}

		// For each of those destinations, try to supply fluid.
		for (Direction sideWithDest : sidesWithDest) {
			if (cableProxy.getStored().isEmpty()) {
				break;
			}

			BlockPos destPos = cableProxy.getPos().relative(sideWithDest);
			List<CachedFluidDestination> blockDestinations = destinations.get(destPos);

			for (CachedFluidDestination dest : blockDestinations) {
				FluidStack toSupply = cableProxy.drain(maxPerDest, FluidAction.SIMULATE);
				supplyFluidToDestination(cableProxy, dest, toSupply);
			}
		}
	}

	protected FluidStack supplyFluidToDestination(FluidCableProxy cableProxy, CachedFluidDestination destination, FluidStack toSupply) {
		IFluidHandler handler = Network.getWorld().getBlockEntity(destination.desintationPos())
				.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, destination.connectedSide()).orElse(null);
		if (handler != null) {
			int filled = handler.fill(toSupply, FluidAction.EXECUTE);
			return cableProxy.drain(filled, FluidAction.EXECUTE);
		}
		return FluidStack.EMPTY;
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
		for (FluidCableProxy otherProxy : otherModule.cableProxies.values()) {
			cableProxies.put(otherProxy.getPos(), otherProxy);
		}
	}

	@Override
	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			if (!cableProxies.containsKey(cable.getPos())) {
				cableProxies.put(cable.getPos(),
						new FluidCableProxy(cable.getPos(), cable.getDataTag().getInt(FluidCableProxy.FLUID_DEFAULT_CAPACITY_DATA_TAG_KEY),
								cable.getDataTag().getInt(FluidCableProxy.FLUID_TRANSFER_RATE_DATA_TAG_KEY),
								cable.getDataTag().getBoolean(FluidCableProxy.FLUID_CABLE_INDUSTRIAL_DATA_TAG_KEY)));
			}
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

				List<CachedFluidDestination> fluidDest = getInterfaceForDesination(wrapper);
				if (fluidDest.size() > 0) {
					destinations.put(pos, fluidDest);
				}
			});
		}
	}

	@Override
	public void getReaderOutput(List<Component> output, BlockPos pos) {
		String storedFluidAtPos = new MetricConverter(cableProxies.get(pos).getStored().getAmount()).getValueAsString(true);
		String capacityAtPos = new MetricConverter(cableProxies.get(pos).getCapacity()).getValueAsString(true);
		String gainedPerTick = GuiTextUtilities.formatFluidRateToString(cableProxies.get(pos).fluidStorage.getFilledPerTick()).getString();
		String drainedPerTick = GuiTextUtilities.formatFluidRateToString(cableProxies.get(pos).fluidStorage.getDrainedPerTick()).getString();

		output.add(new TextComponent(String.format("Pipe Contains: %1$smB of %2$s out of a maximum of %3$smB.", storedFluidAtPos,
				cableProxies.get(pos).getStored().getDisplayName().getString(), capacityAtPos)));
		output.add(new TextComponent(String.format("Gained: %1$smB Drained: %2$s", drainedPerTick, gainedPerTick)));

	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		List<FluidCableProxy> newProxies = NBTUtilities.deserialize(tag.getList("proxies", Tag.TAG_COMPOUND), (proxyTag) -> {
			return FluidCableProxy.deserialize((CompoundTag) proxyTag);
		});

		cableProxies.clear();
		for (FluidCableProxy proxy : newProxies) {
			cableProxies.put(proxy.getPos(), proxy);
		}
	}

	@Override
	public CompoundTag writeToNbt(CompoundTag tag) {
		ListTag serializedProxies = NBTUtilities.serialize(cableProxies.values(), (proxy) -> {
			return proxy.serialize();
		});
		tag.put("proxies", serializedProxies);
		return tag;
	}

	public int getStoredFluid() {
		int stored = 0;
		for (FluidCableProxy proxy : this.cableProxies.values()) {
			stored += proxy.getStored().getAmount();
		}
		return stored;
	}

	public FluidStack getStoredFluid(BlockPos pos) {
		FluidCableProxy proxy = getFluidProxyAtLocation(pos);
		if (proxy == null) {
			return FluidStack.EMPTY;
		}

		return proxy.getStored();
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
					output.add(new CachedFluidDestination(connectedSide, Network.getGraph().getCables().get(cablePos), wrapper.getPos()));
				}
			}
		}

		return output;
	}

	public boolean isFluidValid(FluidStack stack, BlockPos pos) {
		FluidCableProxy proxyAtPos = this.getFluidProxyAtLocation(pos);
		if (proxyAtPos == null) {
			return false;
		}
		return proxyAtPos.fluidStorage.isFluidValid(stack);
	}

	public int fill(BlockPos fromPos, FluidStack resource, FluidAction action) {
		FluidCableProxy proxyAtPos = this.getFluidProxyAtLocation(fromPos);
		if (proxyAtPos == null) {
			return 0;
		}

		if (isFluidValid(resource, fromPos)) {
			int filled = proxyAtPos.fill(resource, 0, action);
			propagatePressure(fromPos, 32, new HashSet<BlockPos>());
			return filled;
		}

		return 0;
	}

	protected void propagatePressure(BlockPos pos, float pressure, Set<BlockPos> visited) {
		if (pressure <= 0 || visited.contains(pos)) {
			return;
		}

		FluidCableProxy proxy = getFluidProxyAtLocation(pos);
		proxy.setPressure(proxy.getPressure() + calculateDeltaPressure(proxy.getPressure(), pressure));
		visited.add(pos);

		for (FluidCableProxy adjacent : getAdjacentCableProxies(pos)) {
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
		float deltaToApply = Math.min(absoluteDeltaPressure, 1f);
		return deltaToApply * sign;
	}

	public FluidStack supply(BlockPos fromPos, int amount, FluidAction action) {
		if (amount == 0) {
			return FluidStack.EMPTY;
		}

		FluidCableProxy proxyAtPos = this.getFluidProxyAtLocation(fromPos);
		if (proxyAtPos == null) {
			return FluidStack.EMPTY;
		}

		return proxyAtPos.drain(amount, action);
	}
}
