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

	protected void updateNetworkValues(World world) {
		// Set the current signal strengths to the previous map and create a new map for
		// the current values.
		HashMap<String, Integer> previous = currentSignalStrengths;
		currentSignalStrengths = new HashMap<String, Integer>();

		// Capture the signal for each cable and keep the maximum.
		isEnabled = false;
		updateAllConnectedBlocks(world);

		for (ServerCable cable : getNetwork().getGraph().getCables().values().stream().collect(Collectors.toList())) {
			String selector = cable.getStringProperty(RedstoneCableComponent.SELECTOR_KEY);
			int strength = getStrongestSignal(world, selector, cable.getPos());
			currentSignalStrengths.put(selector, Math.max(getStrengthForSelector(cable, currentSignalStrengths), strength));
		}

		// Update the naked wire to always be the max of all signals.
		currentSignalStrengths.put("naked", currentSignalStrengths.values().stream().max(Integer::compare).get());

		isEnabled = true;
			updateAllConnectedBlocks(world);

	}

	private int getStrongestSignal(World world, String selector, BlockPos pos) {
		int power = 0;
		for (Direction dir : Direction.values()) {
			BlockPos targetPos = pos.offset(dir);

			// If this is another cable in ANOTHER network but of a different selector, also
			// skip it.
			if (CableNetworkManager.get(world).isTrackingCable(targetPos)) {
				if (CableNetworkManager.get(world).getCable(targetPos).containsProperty(RedstoneCableComponent.SELECTOR_KEY)) {
					String otherSelector = CableNetworkManager.get(world).getCable(targetPos).getStringProperty(RedstoneCableComponent.SELECTOR_KEY);
					if (!selector.equals("naked") && !otherSelector.equals("naked") && !otherSelector.equals(selector)) {
						continue;
					}
				}
			}

			power = Math.max(power, world.getRedstonePower(targetPos, dir.getOpposite()));
		}
		return power;
	}

	public int getCurrentSignalStrength(String selector) {
		return !isEnabled ? 0 : currentSignalStrengths.containsKey(selector) ? currentSignalStrengths.get(selector) : 0;
	}

	protected int getStrengthForSelector(ServerCable cable, HashMap<String, Integer> valueMap) {
		String selector = cable.getStringProperty(RedstoneCableComponent.SELECTOR_KEY);
		return valueMap.containsKey(selector) ? valueMap.get(selector) : 0;
	}

	protected void updateAllConnectedBlocks(World world) {
		for (ServerCable cable : getNetwork().getGraph().getCables().values().stream().collect(Collectors.toList())) {
			if (!neighborNotifyEvent(world, cable.getPos(), world.getBlockState(cable.getPos()))) {
				world.notifyNeighborsOfStateChange(cable.getPos(), world.getBlockState(cable.getPos()).getBlock());
			}
		}
	}

	protected boolean neighborNotifyEvent(World world, @Nonnull BlockPos pos, @Nullable BlockState state) {
		return net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(world, pos, state, java.util.EnumSet.allOf(Direction.class), false).isCanceled();
	}
}
