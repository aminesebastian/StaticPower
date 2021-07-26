package theking530.staticpower.cables.redstone.basic;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.cables.network.NetworkMapper;
import theking530.staticpower.cables.network.ServerCable;
import theking530.staticpower.cables.redstone.AbstractRedstoneNetworkModule;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
import theking530.staticpower.cables.redstone.bundled.BundledRedstoneNetworkModule;

public class RedstoneNetworkModule extends AbstractRedstoneNetworkModule {
	private HashMap<BlockPos, CableSignalWrapper> cableWrappers;

	public RedstoneNetworkModule(ResourceLocation name) {
		super(name);
	}

	@Override
	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {
		super.onNetworkGraphUpdated(mapper, startingPosition);
		scheduleRescan("Network Graph Updated");
	}

	@Override
	public void recieveCrossNetworkUpdate(CableNetwork sendingNetwork, Set<CableNetwork> previousNetworks) {
		scheduleRescan("Cross Network Update");
	}

	@Override
	public void updateNetworkValues(World world, NetworkMapper mapper) {
		resetSignals();

		// Create a new cable wrappers map.
		cableWrappers = new HashMap<BlockPos, CableSignalWrapper>();

		// Iterate through all the cables and capture the recieved redstone values.
		for (ServerCable cable : mapper.getDiscoveredCables()) {
			// Get the configuration for the cable
			RedstoneCableConfiguration configuration = getConfigurationForCable(cable);

			// Create a wrapper for this cable.
			CableSignalWrapper wrapper = new CableSignalWrapper(cable);

			// Check all sides of the cable, and capture the max input for that side's
			// selector.
			for (Direction dir : Direction.values()) {
				int signal = getSignal(world, cable, configuration, dir);
				String selector = configuration.getSideConfig(dir).getSelector();
				wrapper.addSignal(selector, signal);
			}

			cableWrappers.put(cable.getPos(), wrapper);
		}

		// Capture the max signals.
		for (CableSignalWrapper wrapper : cableWrappers.values()) {
			for (String selector : wrapper.signals.keySet()) {
				signals.addSignal(selector, wrapper.signals.get(selector));
			}
		}

		// Update all connected cables.
		if (!signals.equals(getPreviousSignals())) {
			updateAllConnectedBlocks(world, mapper);
		}
	}

	protected int getSignal(World world, ServerCable cable, RedstoneCableConfiguration configuration, Direction side) {
		// Skip any sides that are not configuration as an input, or are disabled.
		if (cable.isDisabledOnSide(side) || !configuration.getSideConfig(side).isInputSide()) {
			return 0;
		}

		stopProvidingPower();
		//updateBlock(world, cable.getPos(), cable.getPos().offset(side));

		// Get the target position.
		BlockPos targetPos = cable.getPos().offset(side);
		String selector = configuration.getSideConfig(side).getSelector();
		int power = 0;
		boolean checkWorld = true;

		try {
			// If this is another cable in ANOTHER network but of a different selector, also
			// skip it.
			if (CableNetworkManager.get(world).isTrackingCable(targetPos)) {
				ServerCable otherCable = CableNetworkManager.get(world).getCable(targetPos);
				if (otherCable.containsProperty(RedstoneCableComponent.CONFIGURATION_KEY)) {
					// Get the configuration for the other cable
					RedstoneCableConfiguration otherConfiguration = getConfigurationForCable(otherCable);
					String otherSelector = otherConfiguration.getSideConfig(side.getOpposite()).getSelector();
					if (!selector.equals("naked") && !otherSelector.equals("naked") && !otherSelector.equals(selector)) {
						checkWorld = false;
					}
				} else if (otherCable.getNetwork().hasModule(CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE)) {
					power = otherCable.getNetwork().<BundledRedstoneNetworkModule>getModule(CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE).getNetworkSignalStrength(selector);
					checkWorld = false;
				}
			}
		} catch (Exception e) {
			System.out.println("WTF");
		}

		// Get the redstone power in the world if requested.
		if (checkWorld) {
			power = world.getRedstonePower(targetPos, side);
			if (cableWrappers.containsKey(targetPos)) {
				if (cableWrappers.get(targetPos).signals.containsKey(selector)) {
					power = Math.max(power, cableWrappers.get(targetPos).signals.get(selector));
				}
			}
		}

		startProvidingPower();
		return power;
	}
}
