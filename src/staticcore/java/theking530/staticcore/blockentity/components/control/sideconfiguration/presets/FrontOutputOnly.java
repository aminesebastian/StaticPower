package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class FrontOutputOnly extends SideConfigurationPreset {
	public static final FrontOutputOnly INSTANCE = new FrontOutputOnly();

	protected FrontOutputOnly() {
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Output);
		addPossibleConfiguration(BlockSide.FRONT, MachineSideMode.Disabled);
	}
}
