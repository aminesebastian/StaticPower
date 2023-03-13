package theking530.staticpower.blockentities.components.control.sideconfiguration.presets;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class AllSidesInput extends SideConfigurationPreset {
	public static final AllSidesInput INSTANCE = new AllSidesInput();

	public AllSidesInput() {
		for (BlockSide side : BlockSide.values()) {
			setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Disabled);
		}
	}
}
