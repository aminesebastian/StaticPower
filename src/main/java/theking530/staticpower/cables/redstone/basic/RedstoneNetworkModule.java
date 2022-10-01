package theking530.staticpower.cables.redstone.basic;

import java.util.Map;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleType;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticpower.cables.redstone.AbstractRedstoneNetworkModule;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
import theking530.staticpower.cables.redstone.bundled.BundledRedstoneNetworkModule;
import theking530.staticpower.init.cables.ModCableModules;

public class RedstoneNetworkModule extends AbstractRedstoneNetworkModule {

	public RedstoneNetworkModule(CableNetworkModuleType type) {
		super(type);
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
	public void updateNetworkValues(Level world, NetworkMapper mapper) {
		// Clear any existing signals.
		resetSignals();

		// Capture all eligible cables.
		captureInputOutputCables(world, mapper);

		// Read in all the input signals.
		for (Map.Entry<ServerCable, CableConfigurationWrapper> entry : inputCables.entrySet()) {
			// Check all sides of the cable, and capture the max input for that side's
			// selector.
			for (Direction dir : Direction.values()) {
				if (!entry.getKey().isDisabledOnSide(dir) && !entry.getValue().configuration.getSideConfig(dir).isOutputSide()) {
					int signal = getSignal(world, entry.getValue(), dir);
					String selector = entry.getValue().configuration.getSideConfig(dir).getSelector();
					signals.addSignal(selector, signal);
				}
			}
		}

		// Update all output cables.
		if (!signals.equals(getPreviousSignals())) {
			for (ServerCable cable : outputCables.keySet()) {
				updateAroundCable(world, cable);
			}
		}
	}

	protected int getSignal(Level world, CableConfigurationWrapper wrapper, Direction side) {
		stopProvidingPower();
		// updateBlock(world, cable.getPos(), cable.getPos().offset(side));

		// Get the target position.
		BlockPos targetPos = wrapper.cable.getPos().relative(side);
		String selector = wrapper.configuration.getSideConfig(side).getSelector();
		int power = 0;
		boolean checkWorld = true;

		try {
			// If this is another cable in ANOTHER network but of a different selector, also
			// skip it.
			if (CableNetworkManager.get(world).isTrackingCable(targetPos)) {
				ServerCable otherCable = CableNetworkManager.get(world).getCable(targetPos);
				if (otherCable.getDataTag().contains(RedstoneCableComponent.CONFIGURATION_KEY)) {
					// Get the configuration for the other cable
					RedstoneCableConfiguration otherConfiguration = getConfigurationForCable(otherCable);
					String otherSelector = otherConfiguration.getSideConfig(side.getOpposite()).getSelector();
					if (!selector.equals("naked") && !otherSelector.equals("naked") && !otherSelector.equals(selector)) {
						checkWorld = false;
					}
				} else if (otherCable.getNetwork().hasModule(ModCableModules.BundledRedstone.get())) {
					power = otherCable.getNetwork().<BundledRedstoneNetworkModule>getModule(ModCableModules.BundledRedstone.get()).getNetworkSignalStrength(selector);
					checkWorld = false;
				}
			}
		} catch (Exception e) {
			System.out.println("WTF");
		}

		// Get the redstone power in the world if requested.
		if (checkWorld) {
			power = world.getSignal(targetPos, side);
		}

		startProvidingPower();
		return power;
	}
}
