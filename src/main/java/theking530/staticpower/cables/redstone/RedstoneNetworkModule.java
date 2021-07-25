package theking530.staticpower.cables.redstone;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;

public class RedstoneNetworkModule extends AbstractCableNetworkModule {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(RedstoneNetworkModule.class);
	private boolean canProvidePower = true;
	private HashMap<String, Integer> currentSignalStrengths;
	private HashMap<BlockPos, CableSignalWrapper> cableWrappers;
	private NetworkMapper latestMapper;

	public RedstoneNetworkModule(ResourceLocation name) {
		super(name);
		currentSignalStrengths = new HashMap<String, Integer>();
	}

	@Override
	public void getReaderOutput(List<ITextComponent> components) {

	}

	@Override
	public void tick(World world) {
		if (latestMapper != null) {
			updateNetworkValues(world, latestMapper);
			// latestMapper = null;
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		latestMapper = mapper;
	}

	protected void updateNetworkValues(World world, NetworkMapper mapper) {
		HashMap<String, Integer> previous = currentSignalStrengths;
		currentSignalStrengths = new HashMap<String, Integer>();

		System.out.println(getType());
		
		// Create a new cable wrappers map.
		cableWrappers = new HashMap<BlockPos, CableSignalWrapper>();

		// Capture the signal for each cable and keep the maximum.
		canProvidePower = false;

		updateAllConnectedBlocks(world, getNetwork(), mapper);

		// Iterate through all the cables and capture the recieved redstone values.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Get the configuration for the cable
			RedstoneCableConfiguration configuration = getSideConfiguration(cable);

			// Create a wrapper for this cable.
			CableSignalWrapper wrapper = new CableSignalWrapper(cable);
			cableWrappers.put(cable.getPos(), wrapper);

			// Check all sides of the cable, and capture the max input for that side's
			// selector.
			for (Direction dir : Direction.values()) {
				int signal = getSignal(world, cable, configuration, dir);
				String selector = configuration.getSideConfig(dir).getSelector();
				wrapper.addSignal(selector, signal);
			}
		}

		// Now do a second pass.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Get the configuration for the cable
			RedstoneCableConfiguration configuration = getSideConfiguration(cable);

			// Create a wrapper for this cable.
			CableSignalWrapper wrapper = new CableSignalWrapper(cable);
			cableWrappers.put(cable.getPos(), wrapper);

			// Check all sides of the cable, and capture the max input for that side's
			// selector.
			for (Direction dir : Direction.values()) {
				int signal = getSignal(world, cable, configuration, dir);
				String selector = configuration.getSideConfig(dir).getSelector();
				wrapper.addSignal(selector, signal);
			}
		}

		// Capture the max signals.
		for (CableSignalWrapper wrapper : cableWrappers.values()) {
			for (String selector : wrapper.signals.keySet()) {
				currentSignalStrengths.put(selector, Math.max(getStrengthForSelector(selector, currentSignalStrengths), wrapper.signals.get(selector)));
			}
		}

		// Update the naked wire to always be the max of all signals.
		if (currentSignalStrengths.size() > 0) {
			currentSignalStrengths.put("naked", currentSignalStrengths.values().stream().max(Integer::compare).get());
		} else {
			currentSignalStrengths.put("naked", 0);
		}

		// Print out the signals.
		for (String selector : currentSignalStrengths.keySet()) {
			System.out.println(String.format("Selector: %1$s Power: %2$d", selector, currentSignalStrengths.get(selector)).toString());
		}

		// Re-enable the network.
		canProvidePower = true;

		// Only trigger an update if the signals have changed.
		if (checkIfValuesChanged(currentSignalStrengths, previous)) {
			updateAllConnectedBlocks(world, mapper);
		}
		updateAllConnectedBlocks(world, mapper);
	}

	protected void updateAllConnectedBlocks(World world, NetworkMapper mapper) {
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// If the event is not cancelled, iterate through all the sides of the cable and
			// notify updates. This must be done for ALL sides, even those that are input
			// only as they may have been output last tick.
			if (neighborNotifyEvent(world, cable.getPos(), world.getBlockState(cable.getPos()))) {
				for (Direction dir : Direction.values()) {
					BlockPos updatePos = cable.getPos().offset(dir);
					world.neighborChanged(updatePos, world.getBlockState(cable.getPos()).getBlock(), cable.getPos());

					// We must also notify all blocks that are touching any blocks we're touching.
					if (!neighborNotifyEvent(world, updatePos, world.getBlockState(updatePos))) {
						for (Direction dir2 : Direction.values()) {
							world.neighborChanged(updatePos.offset(dir2), world.getBlockState(updatePos).getBlock(), updatePos);
						}
					}
				}
			}
		}
	}

	protected int getSignal(World world, ServerCable cable, RedstoneCableConfiguration configuration, Direction side) {
		// Skip any sides that are not configuration as an input, or are disabled.
		if (cable.isDisabledOnSide(side) || !configuration.getSideConfig(side).isInputSide()) {
			return 0;
		}

		// Get the target position.
		BlockPos targetPos = cable.getPos().offset(side);
		String selector = configuration.getSideConfig(side).getSelector();

		// If this is another cable in ANOTHER network but of a different selector, also
		// skip it.
		if (CableNetworkManager.get(world).isTrackingCable(targetPos)) {
			if (CableNetworkManager.get(world).getCable(targetPos).containsProperty(RedstoneCableComponent.CONFIGURATION_KEY)) {
				// Get the configuration for the other cable
				RedstoneCableConfiguration otherConfiguration = getSideConfiguration(CableNetworkManager.get(world).getCable(targetPos));
				String otherSelector = otherConfiguration.getSideConfig(side.getOpposite()).getSelector();
				if (!selector.equals("naked") && !otherSelector.equals("naked") && !otherSelector.equals(selector)) {
					return 0;
				}
			}
		}

		// Get the redstone power.
		int power = 0;
		if (cableWrappers.containsKey(targetPos)) {
			if (cableWrappers.get(targetPos).signals.containsKey(selector)) {
				power = cableWrappers.get(targetPos).signals.get(selector);
			}
		}
		return Math.max(power, world.getRedstonePower(targetPos, side));
	}

	public int getNetworkSignalStrength(String selector) {
		int strength = currentSignalStrengths.containsKey(selector) ? currentSignalStrengths.get(selector) : 0;
		return canProvidePower ? strength : 0;
	}

	protected boolean checkIfValuesChanged(HashMap<String, Integer> current, HashMap<String, Integer> previous) {
		// Check if the number of signals changed.
		if (previous.size() != current.size()) {
			return true;
		}

		// Check to see if any values in the new set are missing in the previous one or
		// have a different value.
		for (String selector : previous.keySet()) {
			if (!current.containsKey(selector) || current.get(selector) != previous.get(selector)) {
				return true;
			}
		}

		// Check to see if any values in the previous set are missing in the new one or
		// have a different value.
		for (String selector : current.keySet()) {
			if (!previous.containsKey(selector) || previous.get(selector) != previous.get(selector)) {
				return true;
			}
		}

		// If the above all passed, nothing changed.
		return false;
	}

	protected int getStrengthForSelector(String selector, HashMap<String, Integer> valueMap) {
		return valueMap.containsKey(selector) ? valueMap.get(selector) : 0;
	}

	protected RedstoneCableConfiguration getSideConfiguration(ServerCable cable) {
		RedstoneCableConfiguration configuration = new RedstoneCableConfiguration();
		configuration.deserializeNBT(cable.getTagProperty(RedstoneCableComponent.CONFIGURATION_KEY));
		return configuration;
	}

	protected static boolean neighborNotifyEvent(World world, @Nonnull BlockPos pos, @Nullable BlockState state) {
		return !net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(world, pos, state, java.util.EnumSet.allOf(Direction.class), false).isCanceled();
	}

	protected void updateAllCables(World world, CableNetwork network, NetworkMapper mapper) {
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			updateBlock(world, cable.getPos(), cable.getPos());
		}
	}

	protected void updateAllConnectedBlocks(World world, CableNetwork network, NetworkMapper mapper) {
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// If the event is not cancelled, iterate through all the sides of the cable and
			// notify updates.
			if (neighborNotifyEvent(world, cable.getPos(), world.getBlockState(cable.getPos()))) {
				for (Direction dir : Direction.values()) {
					BlockPos updatePos = cable.getPos().offset(dir);
					updateBlock(world, cable.getPos(), updatePos);

					// We must also notify all blocks that are touching any blocks we're touching.
					if (neighborNotifyEvent(world, updatePos, world.getBlockState(updatePos))) {
						for (Direction dir2 : Direction.values()) {
							updateBlock(world, updatePos, updatePos.offset(dir2));
						}
					}
				}
			}
		}
	}

	protected void updateBlock(World world, BlockPos sourcePos, BlockPos targetPos) {
		// Skip cables in this network.
		if (!getNetwork().getGraph().getCables().containsKey(targetPos)) {
			world.neighborChanged(targetPos, world.getBlockState(targetPos).getBlock(), sourcePos);
		}
	}

	protected class CableSignalWrapper {
		public final ServerCable cable;
		private final HashMap<String, Integer> signals;

		public CableSignalWrapper(ServerCable cable) {
			this.cable = cable;
			signals = new HashMap<String, Integer>();
		}

		public void addSignal(String selector, int power) {
			if (!signals.containsKey(selector)) {
				signals.put(selector, power);
			} else {
				signals.put(selector, Math.max(signals.get(selector), power));
			}
		}
	}
}
