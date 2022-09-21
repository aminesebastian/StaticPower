package theking530.staticpower.cables.redstone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.network.modules.CableNetworkModule;
import theking530.staticpower.cables.network.scanning.NetworkMapper;
import theking530.staticpower.cables.redstone.basic.RedstoneCableComponent;

public abstract class AbstractRedstoneNetworkModule extends CableNetworkModule {
	protected Map<ServerCable, CableConfigurationWrapper> outputCables;
	protected Map<ServerCable, CableConfigurationWrapper> inputCables;
	protected SignalContainer signals;
	protected SignalContainer previousSignals;
	protected NetworkMapper lastNetworkMap;
	private boolean canProvidePower;
	private boolean shouldRescanConnections;
	@SuppressWarnings("unused")
	private String lastRescanSource;

	public AbstractRedstoneNetworkModule(ResourceLocation type) {
		super(type);
		signals = new SignalContainer();
		canProvidePower = true;
		outputCables = new HashMap<ServerCable, CableConfigurationWrapper>();
		inputCables = new HashMap<ServerCable, CableConfigurationWrapper>();
	}

	@Override
	public void tick(Level world) {
		if (shouldRescanConnections) {
			captureInputOutputCables(world, lastNetworkMap);
			updateNetworkValues(world, lastNetworkMap);
			shouldRescanConnections = false;
		}
	}

	public int getNetworkSignalStrength(String selector) {
		return canProvidePower() ? signals.getSignal(selector) : 0;
	}

	public Set<String> getAllSupportedSelectors() {
		return signals.getAllSupportedSelectors();
	}

	public void addSignalFromBundledCable(SignalContainer signals) {
		signals.addOtherContainer(signals);
	}

	public void stopProvidingPower() {
		canProvidePower = false;
	}

	public void startProvidingPower() {
		canProvidePower = true;
	}

	public boolean canProvidePower() {
		return canProvidePower;
	}

	public void scheduleRescan(String cause) {
		this.shouldRescanConnections = true;
		this.lastRescanSource = cause;
	}

	public abstract void updateNetworkValues(Level world, NetworkMapper mapper);

	@Override
	public void getReaderOutput(List<Component> components) {

	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		lastNetworkMap = mapper;
		//System.out.println("Updating graph of type: " + getType() + " from position: " + startingPosition);
	}

	protected static boolean neighborNotifyEvent(Level world, @Nonnull BlockPos pos, @Nullable BlockState state) {
		return !net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(world, pos, state, java.util.EnumSet.allOf(Direction.class), false).isCanceled();
	}

	protected SignalContainer resetSignals() {
		previousSignals = signals;
		signals = new SignalContainer();
		return signals;
	}

	protected SignalContainer getPreviousSignals() {
		return previousSignals;
	}

	protected RedstoneCableConfiguration getConfigurationForCable(ServerCable cable) {
		RedstoneCableConfiguration configuration = new RedstoneCableConfiguration();
		configuration.deserializeNBT(cable.getDataTag().getCompound(RedstoneCableComponent.CONFIGURATION_KEY));
		return configuration;
	}

	protected void updateAllCables(Level world, NetworkMapper mapper) {
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			updateAroundCable(world, cable);
		}
	}

	@SuppressWarnings("deprecation")
	protected void updateAroundCable(Level world, ServerCable cable) {
		CableNetworkManager manager = CableNetworkManager.get(world);
		try {
			// Skip cables in this network.
			if (neighborNotifyEvent(world, cable.getPos(), world.getBlockState(cable.getPos()))) {
				for (Direction dir : Direction.values()) {
					BlockPos updatePos = cable.getPos().relative(dir);

					// Skip cables part of networks we already updated.
					if (manager.isTrackingCable(updatePos)) {
						manager.getCable(updatePos).getNetwork().recieveCrossNetworkUpdate(getNetwork(), null);
						continue;
					}

					if (!world.getBlockState(updatePos).isAir()) {
						updateBlock(world, cable.getPos(), updatePos);

						// We must also notify all blocks that are touching any blocks we're touching.
						if (!neighborNotifyEvent(world, updatePos, world.getBlockState(updatePos))) {
							for (Direction dir2 : Direction.values()) {
								BlockPos updatePos2 = updatePos.relative(dir2);
								// Skip cables part of networks we already updated.
								if (manager.isTrackingCable(updatePos2)) {
									manager.getCable(updatePos).getNetwork().recieveCrossNetworkUpdate(getNetwork(), null);
									continue;
								}

								if (!world.getBlockState(updatePos2).isAir()) {
									updateBlock(world, cable.getPos(), updatePos2);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("How is this possible!?");
		}
	}

	protected void updateBlock(Level world, BlockPos sourcePos, BlockPos targetPos) {
		// Skip cables in this network.
		if (!getNetwork().getGraph().getCables().containsKey(targetPos)) {
			world.neighborChanged(targetPos, world.getBlockState(sourcePos).getBlock(), sourcePos);
		}
	}

	protected void captureInputOutputCables(Level world, NetworkMapper mapper) {
		outputCables.clear();
		inputCables.clear();

		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Get the configuration for the cable
			RedstoneCableConfiguration configuration = getConfigurationForCable(cable);

			// Check if this cable has inputs or outputs.
			for (Direction side : Direction.values()) {
				// If this is NOT an input side, and its not disabled, and we don't have an
				// entry on this, add it to the output cables.
				if (!configuration.getSideConfig(side).isInputSide() && !cable.isDisabledOnSide(side) && !outputCables.containsKey(cable)) {
					outputCables.put(cable, new CableConfigurationWrapper(cable, configuration));
				}
				// If this is NOT an output side, and its not disabled, and we don't have an
				// entry on this, add it to the input cables.
				if (!configuration.getSideConfig(side).isOutputSide() && !cable.isDisabledOnSide(side) && !outputCables.containsKey(cable)) {
					inputCables.put(cable, new CableConfigurationWrapper(cable, configuration));
				}
			}
		}
	}

	public class CableConfigurationWrapper {
		public final ServerCable cable;
		public final RedstoneCableConfiguration configuration;

		public CableConfigurationWrapper(ServerCable cable, RedstoneCableConfiguration configuration) {
			this.cable = cable;
			this.configuration = configuration;
		}
	}

	public class SignalContainer {
		private final HashMap<String, Integer> signals;

		public SignalContainer() {
			signals = new HashMap<String, Integer>();
		}

		public int getSignal(String selector) {
			return signals.containsKey(selector) ? signals.get(selector) : 0;
		}

		public void addSignal(String selector, int signal) {
			// Add the signal.
			if (!signals.containsKey(selector)) {
				signals.put(selector, signal);
			} else {
				signals.put(selector, Math.max(signals.get(selector), signal));
			}

			// Also, update the naked signal.
			if (!signals.containsKey("naked")) {
				signals.put("naked", signal);
			} else {
				signals.put("naked", Math.max(signal, signals.get("naked")));
			}
		}

		public void addOtherContainer(SignalContainer other) {
			for (String selector : other.signals.keySet()) {
				addSignal(selector, other.getSignal(selector));
			}
		}

		public Set<String> getAllSupportedSelectors() {
			return signals.keySet();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SignalContainer)) {
				return false;
			}

			SignalContainer other = (SignalContainer) obj;

			// Check if the number of signals changed.
			if (other.signals.size() != signals.size()) {
				return false;
			}

			// Check to see if any values in the new set are missing in the previous one or
			// have a different value.
			for (String selector : other.signals.keySet()) {
				if (!signals.containsKey(selector) || signals.get(selector) != other.signals.get(selector)) {
					return false;
				}
			}

			// Check to see if any values in the previous set are missing in the new one or
			// have a different value.
			for (String selector : signals.keySet()) {
				if (!other.signals.containsKey(selector) || signals.get(selector) != other.signals.get(selector)) {
					return true;
				}
			}

			// If the above all passed, nothing changed.
			return true;
		}
	}
}
