package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class AllSidesOutput extends SideConfigurationPreset {
	public static final AllSidesOutput INSTANCE = new AllSidesOutput();

	public AllSidesOutput() {
		for (BlockSide side : BlockSide.values()) {
			setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Output);
			addPossibleConfiguration(side, MachineSideMode.Disabled);
		}
	}
}
