package theking530.staticpower.blockentities.nonpowered.blastfurnace;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class BlastFurnaceSideConfiguration extends DefaultMachineNoFacePreset {
	public static final BlastFurnaceSideConfiguration INSTANCE = new BlastFurnaceSideConfiguration();

	public BlastFurnaceSideConfiguration() {
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input);
	}
}
