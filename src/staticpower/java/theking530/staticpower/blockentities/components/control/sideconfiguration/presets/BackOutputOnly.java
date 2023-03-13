package theking530.staticpower.blockentities.components.control.sideconfiguration.presets;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class BackOutputOnly extends SideConfigurationPreset {
	public static final BackOutputOnly INSTANCE = new BackOutputOnly();

	protected BackOutputOnly() {
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output);
		addPossibleConfiguration(BlockSide.BACK, MachineSideMode.Disabled);
	}
}
