package theking530.staticpower.blockentities.components.control.sideconfiguration;

import java.util.HashMap;

import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class DefaultSideConfiguration {
	private HashMap<BlockSide, Boolean> enabledSides;
	private HashMap<BlockSide, MachineSideMode> defaultSideModes;

	public DefaultSideConfiguration() {
		enabledSides = new HashMap<BlockSide, Boolean>();
		defaultSideModes = new HashMap<BlockSide, MachineSideMode>();

		// Set the default values.
		for (BlockSide side : BlockSide.values()) {
			enabledSides.put(side, false);
			defaultSideModes.put(side, MachineSideMode.Never);
		}
	}

	public DefaultSideConfiguration setSide(BlockSide side, boolean enabled, MachineSideMode mode) {
		enabledSides.put(side, enabled);
		defaultSideModes.put(side, mode);
		return this;
	}

	public boolean getSideDefaultEnabled(BlockSide side) {
		return enabledSides.get(side);
	}

	public MachineSideMode getSideDefaultMode(BlockSide side) {
		return defaultSideModes.get(side);
	}

	public DefaultSideConfiguration copy() {
		DefaultSideConfiguration output = new DefaultSideConfiguration();

		// Copy over all configurations.
		for (BlockSide side : BlockSide.values()) {
			output.enabledSides.put(side, enabledSides.get(side));
			output.defaultSideModes.put(side, defaultSideModes.get(side));
		}

		return output;
	}
}
