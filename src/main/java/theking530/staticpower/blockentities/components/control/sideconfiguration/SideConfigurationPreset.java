package theking530.staticpower.blockentities.components.control.sideconfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class SideConfigurationPreset {
	private HashMap<BlockSide, SideConfiguration> configuration;
	private HashMap<BlockSide, Set<MachineSideMode>> possibleConfigurations;

	public SideConfigurationPreset() {

		// Set the default values.
		for (BlockSide side : BlockSide.values()) {
			configuration.put(side, new SideConfiguration(side, MachineSideMode.Never, false));
			possibleConfigurations.put(side, new HashSet<>());
		}
	}

	public SideConfigurationPreset setSide(BlockSide side, boolean enabled, MachineSideMode mode) {
		configuration.get(side).setEnabled(enabled);
		configuration.get(side).setMode(mode);
		possibleConfigurations.get(side).add(mode);
		return this;
	}

	public SideConfigurationPreset addPossibleConfiguration(BlockSide side, MachineSideMode mode) {
		possibleConfigurations.get(side).add(mode);
		return this;
	}

	public boolean getSideDefaultEnabled(BlockSide side) {
		return configuration.get(side).isEnabled();
	}

	public MachineSideMode getSideDefaultMode(BlockSide side) {
		return configuration.get(side).getMode();
	}

	public boolean validate(BlockSide side, SideConfiguration config) {
		SideConfiguration sideConfig = configuration.get(side);
		if (!sideConfig.isEnabled()) {
			return false;
		}

		return possibleConfigurations.get(side).contains(config.getMode());
	}

	public void modifyAfterChange(SideConfigurationComponent configurationComponent) {

	}

	public SideConfigurationPreset copy() {
		SideConfigurationPreset output = new SideConfigurationPreset();

		// Copy over all configurations.
		for (BlockSide side : BlockSide.values()) {
			output.setSide(side, getSideDefaultEnabled(side), getSideDefaultMode(side));
			for (MachineSideMode mode : possibleConfigurations.get(side)) {
				output.addPossibleConfiguration(side, mode);
			}
		}

		return output;
	}
}
