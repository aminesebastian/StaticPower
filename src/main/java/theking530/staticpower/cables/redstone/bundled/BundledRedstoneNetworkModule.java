package theking530.staticpower.cables.redstone.bundled;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import theking530.staticcore.cablenetwork.CableNetwork;
import theking530.staticcore.cablenetwork.CableNetworkManager;
import theking530.staticcore.cablenetwork.ServerCable;
import theking530.staticcore.cablenetwork.modules.CableNetworkModule;
import theking530.staticcore.cablenetwork.modules.CableNetworkModuleTypes;
import theking530.staticcore.cablenetwork.scanning.NetworkMapper;
import theking530.staticpower.cables.redstone.AbstractRedstoneNetworkModule;
import theking530.staticpower.cables.redstone.RedstoneCableConfiguration;
import theking530.staticpower.cables.redstone.basic.RedstoneCableComponent;
import theking530.staticpower.cables.redstone.basic.RedstoneNetworkModule;

public class BundledRedstoneNetworkModule extends AbstractRedstoneNetworkModule {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(BundledRedstoneNetworkModule.class);

	public BundledRedstoneNetworkModule() {
		super(CableNetworkModuleTypes.BUNDLED_REDSTONE_NETWORK_MODULE);
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
		// Stop providing power.
		stopProvidingPower();

		// Reset all cached signals.
		resetSignals();

		List<RedstoneNetworkModule> hitModules = new ArrayList<RedstoneNetworkModule>();
		for (Map.Entry<ServerCable, CableConfigurationWrapper> entry : inputCables.entrySet()) {
			// Look around the cable.
			for (Direction dir : Direction.values()) {
				if (!entry.getKey().isDisabledOnSide(dir) && !entry.getValue().configuration.getSideConfig(dir).isOutputSide()) {
					// Get the target position.
					BlockPos targetPos = entry.getKey().getPos().relative(dir);
					// If this position contains a cable, let's see if its a valid redstone cable.
					if (CableNetworkManager.get(world).isTrackingCable(targetPos)) {
						// Get the cable at the target location.
						ServerCable targetCable = CableNetworkManager.get(world).getCable(targetPos);

						// Make sure the cable we're hitting is not disabled or an input.
						if (!targetCable.isDisabledOnSide(dir.getOpposite())) {
							// If that cable supports a regular redstone network, get the instance of that
							// network.
							if (CableNetworkModuleTypes.doesNetworkSupportRedstoneAnyRedstoneModule(targetCable.getNetwork())) {
								// Then, capture all the selectors on that network (and keep the max value).
								for (CableNetworkModule module : targetCable.getNetwork().getModules()) {
									if (module instanceof RedstoneNetworkModule) {
										RedstoneNetworkModule redstoneModule = (RedstoneNetworkModule) module;
										hitModules.add(redstoneModule);

										RedstoneCableConfiguration configuration = new RedstoneCableConfiguration();
										configuration.deserializeNBT(targetCable.getDataTag().getCompound(RedstoneCableComponent.CONFIGURATION_KEY));

										if (!configuration.getSideConfig(dir.getOpposite()).isInputSide()) {
											for (String selector : redstoneModule.getAllSupportedSelectors()) {
												signals.addSignal(selector, redstoneModule.getNetworkSignalStrength(selector));
											}
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// Update all connected modules.
		for (RedstoneNetworkModule module : hitModules) {
			module.addSignalFromBundledCable(signals);
		}

		// Begin providing power again.
		startProvidingPower();
		if (!signals.equals(getPreviousSignals())) {
			updateAllCables(world, mapper);
		}
	}
}
