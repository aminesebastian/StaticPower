package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class InputOnlyNoFace extends SideConfigurationPreset {
	public static final InputOnlyNoFace INSTANCE = new InputOnlyNoFace();

	protected InputOnlyNoFace() {
		for (BlockSide side : BlockSide.values()) {
			if (side == BlockSide.FRONT) {
				continue;
			}
			setDefaultOnSide(side, MachineSideMode.Input);
		}
	}
}
