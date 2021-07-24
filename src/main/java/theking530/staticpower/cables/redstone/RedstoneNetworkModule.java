package theking530.staticpower.cables.redstone;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.ServerCable;

public class RedstoneNetworkModule extends AbstractCableNetworkModule {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(RedstoneNetworkModule.class);
	private HashMap<String, Integer> currentSignalStrengths;
	private boolean isEnabled;

	public RedstoneNetworkModule(ResourceLocation name) {
		super(name);
		currentSignalStrengths = new HashMap<String, Integer>();
	}

	@Override
	public void getReaderOutput(List<ITextComponent> components) {

	}

	@Override
	public void tick(World world) {
		updateNetworkValues(world);
	}

	public int getCurrentSignalStrength(String selector) {
		return !isEnabled ? 0 : currentSignalStrengths.containsKey(selector) ? currentSignalStrengths.get(selector) : 0;
	}

	protected void updateNetworkValues(World world) {
		// Set the current signal strengths to the previous map and create a new map for
		// the current values.
		HashMap<String, Integer> previous = currentSignalStrengths;
		currentSignalStrengths = new HashMap<String, Integer>();

		// Capture the signal for each cable and keep the maximum.
		isEnabled = false;

		// Iterate through all the cables and capture the recieved redstone values.
		for (ServerCable cable : getNetwork().getGraph().getCables().values().stream().collect(Collectors.toList())) {
			// Get the configuration for the cable
			RedstoneCableConfiguration configuration = getSideConfiguration(cable);

			// Check all sides of the cable, and capture the max input for that side's
			// selector.
			for (Direction dir : Direction.values()) {
				int signal = getSignal(world, cable, configuration, dir);
				String selector = configuration.getSideConfig(dir).getSelector();
				currentSignalStrengths.put(selector, Math.max(getStrengthForSelector(selector, currentSignalStrengths), signal));
			}
		}

		// Update the naked wire to always be the max of all signals.
		if (currentSignalStrengths.size() > 0) {
			currentSignalStrengths.put("naked", currentSignalStrengths.values().stream().max(Integer::compare).get());
		} else {
			currentSignalStrengths.put("naked", 0);
		}

		// Re-enable the network.
		isEnabled = true;

		// Only trigger an update if the signals have changed.
		if (checkIfValuesChanged(currentSignalStrengths, previous)) {
			updateAllConnectedBlocks(world);
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
		return world.getRedstonePower(targetPos, side);
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

	protected void updateAllConnectedBlocks(World world) {
		for (ServerCable cable : getNetwork().getGraph().getCables().values().stream().collect(Collectors.toList())) {
			// If the event is not cancelled, iterate through all the sides of the cable and
			// notify updates. This must be done for ALL sides, even those that are input
			// only as they may have been output last tick.
			if (!neighborNotifyEvent(world, cable.getPos(), world.getBlockState(cable.getPos()))) {
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

	protected RedstoneCableConfiguration getSideConfiguration(ServerCable cable) {
		RedstoneCableConfiguration configuration = new RedstoneCableConfiguration();
		configuration.deserializeNBT(cable.getTagProperty(RedstoneCableComponent.CONFIGURATION_KEY));
		return configuration;
	}

	protected boolean neighborNotifyEvent(World world, @Nonnull BlockPos pos, @Nullable BlockState state) {
		return net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(world, pos, state, java.util.EnumSet.allOf(Direction.class), false).isCanceled();
	}
}
