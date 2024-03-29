package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class AllSidesInput extends SideConfigurationPreset {
	public static final AllSidesInput INSTANCE = new AllSidesInput();

	public AllSidesInput() {
		for (BlockSide side : BlockSide.values()) {
			setDefaultOnSide(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Disabled);
		}
	}
}
