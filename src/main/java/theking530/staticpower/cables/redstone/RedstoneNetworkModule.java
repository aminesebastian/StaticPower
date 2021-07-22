package theking530.staticpower.cables.redstone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.AbstractCableNetworkModule;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;

public class RedstoneNetworkModule extends AbstractCableNetworkModule {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(RedstoneNetworkModule.class);
	private HashMap<String, Integer> currentSignalStrengths;
	private boolean updating;
	private boolean shouldUpdate;

	public RedstoneNetworkModule() {
		super(CableNetworkModuleTypes.REDSTONE_NETWORK_MODULE);
		currentSignalStrengths = new HashMap<String, Integer>();
	}

	@Override
	public void getReaderOutput(List<ITextComponent> components) {

	}

	@Override
	public void tick(World world) {
		if (shouldUpdate) {
			updateNetworkValues(world, true);
			shouldUpdate = false;
		}
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper) {
		shouldUpdate = true;
	}

	protected void updateNetworkValues(World world, boolean forceUpdate) {
		if (updating) {
			return;
		}
		updating = true;
		
		// Set the current signal strengths to the previous map and create a new map for
		// the current values.
		HashMap<String, Integer> previous = currentSignalStrengths;
		currentSignalStrengths = new HashMap<String, Integer>();

		// Make a list of all the cables.
		List<ServerCable> cables = new ArrayList<ServerCable>();
		for (Map.Entry<BlockPos, ServerCable> pair : getNetwork().getGraph().getCables().entrySet()) {
			cables.add(pair.getValue());
		}

		// Pre-update the blocks with our signal strength == 0 to avoid feedback loops.
		cables.forEach(cable -> updateConnectedBlocks(cable.getPos()));

		// Keep track of the max for the naked cable.
		int max = 0;
		
		// We have to hit each block multiple times because a block may be exposed to multiple selectors.
		// This CAN be optimized.
		for (ServerCable cable : cables) {
			String selector = cable.getStringProperty(RedstoneCableComponent.SELECTOR_KEY);
			for (Direction dir : Direction.values()) {
				BlockPos targetPos = cable.getPos().offset(dir);
				if ( getNetwork().getGraph().getCables().containsKey(targetPos) || getNetwork().getWorld().isAirBlock(targetPos)) {
					continue;
				}

				// Update the values for the selector.
				int strength = world.getRedstonePower(targetPos, dir);
				max = Math.max(max, strength);
				currentSignalStrengths.put(selector, Math.max(getStrengthForSelector(cable, currentSignalStrengths), strength));
			}
		}
		
		// Update the naked wire to always be the max of all signals.
		currentSignalStrengths.put("naked", max);

		// Update each cable that matches the selector.
		cables.forEach(cable -> {
			int currentStrengths = getStrengthForSelector(cable, currentSignalStrengths);
			int previousStrength = getStrengthForSelector(cable, previous);
			if (currentStrengths != previousStrength || forceUpdate) {
				updateConnectedBlocks(cable.getPos());
			}
		});
		
		updating = false;
	}

	@Override
	public void onAddedToNetwork(CableNetwork other) {
		super.onAddedToNetwork(other);
	}

	@Override
	public void readFromNbt(CompoundNBT tag) {
	}

	@Override
	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return tag;
	}

	public int getCurrentSignalStrength(String selector) {
		return currentSignalStrengths.containsKey(selector) ? currentSignalStrengths.get(selector) : 0;
	}

	protected int getStrengthForSelector(ServerCable cable, HashMap<String, Integer> valueMap) {
		String selector = cable.getStringProperty(RedstoneCableComponent.SELECTOR_KEY);
		return valueMap.containsKey(selector) ? valueMap.get(selector) : 0;
	}

	protected void updateConnectedBlocks(BlockPos pos) {
		if (net.minecraftforge.event.ForgeEventFactory
				.onNeighborNotify(getNetwork().getWorld(), pos, getNetwork().getWorld().getBlockState(pos), java.util.EnumSet.allOf(Direction.class), false).isCanceled()) {
			return;
		}
		Block cableBlock = getNetwork().getWorld().getBlockState(pos).getBlock();
		for (Direction dir : Direction.values()) {
			if (!getNetwork().getGraph().getCables().containsKey(pos.offset(dir))) {
				getNetwork().getWorld().neighborChanged(pos.offset(dir), cableBlock, pos);
			}
		}
	}
}
