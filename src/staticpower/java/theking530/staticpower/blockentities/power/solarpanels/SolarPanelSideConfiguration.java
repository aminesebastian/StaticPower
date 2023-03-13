package theking530.staticpower.blockentities.power.solarpanels;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class SolarPanelSideConfiguration extends SideConfigurationPreset {
	public static final SolarPanelSideConfiguration INSTANCE = new SolarPanelSideConfiguration();

	protected SolarPanelSideConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			setDefaultOnSide(side, MachineSideMode.Never);
			clearPossibleConfigurations(side);
		}
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
	}
}
