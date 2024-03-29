package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class BottomOutputOnly extends SideConfigurationPreset {
	public static final BottomOutputOnly INSTANCE = new BottomOutputOnly();

	protected BottomOutputOnly() {
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		addPossibleConfiguration(BlockSide.BOTTOM, MachineSideMode.Disabled);
	}
}
