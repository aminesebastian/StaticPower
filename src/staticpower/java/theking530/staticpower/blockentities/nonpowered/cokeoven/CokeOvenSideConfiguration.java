package theking530.staticpower.blockentities.nonpowered.cokeoven;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class CokeOvenSideConfiguration extends DefaultMachineNoFacePreset {
	public static final CokeOvenSideConfiguration INSTANCE = new CokeOvenSideConfiguration();

	public CokeOvenSideConfiguration() {
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input);
	}
}
