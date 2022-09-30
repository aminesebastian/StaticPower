package theking530.staticpower.cables.fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Tuple;
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
import theking530.staticpower.init.ModCableDestinations;
import theking530.staticpower.init.ModCableModules;
import theking530.staticpower.utilities.MetricConverter;
import theking530.staticpower.utilities.NBTUtilities;

public class FluidNetworkModule extends CableNetworkModule {
	protected record CachedFluidDestination(IFluidHandler handler, ServerCable cable, BlockPos desintationPos) {
	}

	private int totalCapacity;
	private final Map<BlockPos, List<CachedFluidDestination>> destinations;
	private final Map<BlockPos, FluidCableProxy> cableProxies;

	public FluidNetworkModule() {
		super(ModCableModules.Fluid.get());
		destinations = new HashMap<>();
		cableProxies = new HashMap<>();
	}

	@Override
	public void preWorldTick(Level world) {
		// Balance the fluid amounts in the cables.
		for (FluidCableProxy firstProxy : cableProxies.values()) {
			firstProxy.clearSuppliedFromDirections();
			firstProxy.fluidStorage.captureFluidMetrics();
		}
		balanceCables();
	}

	@Override
	public void tick(Level world) {

	}

	protected void balanceCables() {
		// Sort so we flow from high to low pressure.
		List<FluidCableProxy> storedCables = cableProxies.values().stream().sorted((first, second) -> first.getStored().getAmount() - second.getStored().getAmount()).toList();
		for (FluidCableProxy firstProxy : storedCables) {
			// Skip empty cables.
			if (firstProxy.getStored().isEmpty() || firstProxy.getPressure() == 0) {
				continue;
			}
			tickCable(firstProxy);
		}
	}

	protected List<Tuple<Direction, Boolean>> getCableSidesToDistributeTo(FluidCableProxy cableProxy) {
		List<Tuple<Direction, Boolean>> sidesToDistributeTo = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			// Don't supply to a direction you just recieved fluid from.
			if (cableProxy.wasSuppliedFromDirection(dir.getOpposite())) {
				continue;
			}

			BlockPos testPos = cableProxy.getPos().relative(dir);
			if (getFluidProxyAtLocation(testPos) != null) {
				// Skip disabled sides.
				if (Network.getGraph().getCables().get(testPos).isDisabledOnSide(dir)) {
					continue;
				}

				FluidCableProxy adjacentCable = getFluidProxyAtLocation(testPos);
				if (adjacentCable.getPressure() >= cableProxy.getPressure()) {
					continue;
				}

				if (!adjacentCable.fluidStorage.isFluidValid(cableProxy.getStored())) {
					continue;
				}
				sidesToDistributeTo.add(new Tuple<Direction, Boolean>(dir, false));
			} else if (destinations.containsKey(testPos)) {
				List<CachedFluidDestination> adjacentDestinations = destinations.get(testPos);
				if (adjacentDestinations.size() > 0) {
					sidesToDistributeTo.add(new Tuple<Direction, Boolean>(dir, true));
				}
			}
		}
		return sidesToDistributeTo;
	}

	protected void tickCable(FluidCableProxy cableProxy) {
		List<Tuple<Direction, Boolean>> sidesToDistributeTo = getCableSidesToDistributeTo(cableProxy);
		int sumOfCables = (int) sidesToDistributeTo.stream().filter(x -> !x.getB()).count();
		for (Tuple<Direction, Boolean> dir : sidesToDistributeTo) {
			BlockPos testPos = cableProxy.getPos().relative(dir.getA());

			// If the side contains a cable, balance to it.
			// If the side is a destination, supply to it.
			if (getFluidProxyAtLocation(testPos) != null) {
				FluidCableProxy adjacentCable = getFluidProxyAtLocation(testPos);
				int deltaFirstToSecond = cableProxy.getStored().getAmount() - adjacentCable.getStored().getAmount();
				int sign = (int) Math.signum(deltaFirstToSecond);
				int absoluteDelta = Math.abs(deltaFirstToSecond);
				int amountToBalance = (absoluteDelta / sumOfCables);
				amountToBalance = Math.min(absoluteDelta, amountToBalance);
				amountToBalance *= sign;

				if (amountToBalance > 0) {
					FluidStack drained = cableProxy.drain(amountToBalance, FluidAction.EXECUTE);
					adjacentCable.fill(drained, adjacentCable.getPressure(), FluidAction.EXECUTE);
				}
				adjacentCable.setPressure(cableProxy.getPressure() - 1);

				// Leave early if we run out.
				if (cableProxy.getStored().isEmpty()) {
					break;
				}
			} else if (destinations.containsKey(testPos)) {
				List<CachedFluidDestination> adjacentDestinations = destinations.get(testPos);
				for (CachedFluidDestination dest : adjacentDestinations) {
					FluidCableProxy proxy = this.getFluidProxyAtLocation(dest.cable().getPos());
					if (proxy == null || proxy.getStored().isEmpty()) {
						continue;
					}

					FluidStack simulatedSupplied = supply(proxy.getPos(), proxy.getTransferRate(), FluidAction.SIMULATE);
					if (!simulatedSupplied.isEmpty()) {
						int supplied = dest.handler().fill(simulatedSupplied, FluidAction.EXECUTE);
						supply(proxy.getPos(), supplied, FluidAction.EXECUTE);
					}
				}
			}
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
		for (FluidCableProxy otherProxy : otherModule.cableProxies.values()) {
			cableProxies.put(otherProxy.getPos(), otherProxy);
		}
	}

	@Override
	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		totalCapacity = 0;
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			if (!cableProxies.containsKey(cable.getPos())) {
				cableProxies.put(cable.getPos(),
						new FluidCableProxy(cable.getPos(), cable.getDataTag().getInt(FluidCableProxy.FLUID_DEFAULT_CAPACITY_DATA_TAG_KEY),
								cable.getDataTag().getInt(FluidCableProxy.FLUID_TRANSFER_RATE_DATA_TAG_KEY),
								cable.getDataTag().getBoolean(FluidCableProxy.FLUID_CABLE_INDUSTRIAL_DATA_TAG_KEY)));
				totalCapacity += cableProxies.get(cable.getPos()).getCapacity();
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
					output.add(new CachedFluidDestination(handler, Network.getGraph().getCables().get(cablePos), wrapper.getPos()));
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
			int filled = proxyAtPos.fill(resource, 32.0f, action);
			return filled;
		}

		return 0;
	}

	public FluidStack supply(BlockPos fromPos, int amount, FluidAction action) {
		if (amount == 0) {
			return FluidStack.EMPTY;
		}

		FluidCableProxy proxyAtPos = this.getFluidProxyAtLocation(fromPos);
		if (proxyAtPos == null) {
			return FluidStack.EMPTY;
		}

		FluidStack drained = proxyAtPos.drain(amount, action);
		FluidStack output = drained.copy();

		if (action == FluidAction.EXECUTE) {
			// Drop the pressure by the percent of fluid provided.
			// If the cable goes empty, pressure goes to 0.
			if (proxyAtPos.getStored().getAmount() <= 0) {
				proxyAtPos.setPressure(0);
			} else {
				float providedPercentage = (float) drained.getAmount() / proxyAtPos.getCapacity();
				float pressure = (1 - providedPercentage) * proxyAtPos.getPressure();
				proxyAtPos.setPressure(pressure);
			}
		}

		return output;
	}
}
