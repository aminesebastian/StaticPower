package theking530.staticpower.blockentities.machines.carpenter;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class CarpenterSideConfiguration extends DefaultMachineNoFacePreset {
	public static final CarpenterSideConfiguration INSTANCE = new CarpenterSideConfiguration();

	public CarpenterSideConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			if (side == BlockSide.FRONT) {
				continue;
			}
			addPossibleConfiguration(side, MachineSideMode.Disabled);
			addPossibleConfiguration(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Output);
			addPossibleConfiguration(side, MachineSideMode.Output2);
			addPossibleConfiguration(side, MachineSideMode.Output3);
		}

		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output2);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output3);
	}
}
