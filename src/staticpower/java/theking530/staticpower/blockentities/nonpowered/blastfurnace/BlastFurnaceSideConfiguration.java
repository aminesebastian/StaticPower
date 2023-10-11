package theking530.staticpower.blockentities.nonpowered.blastfurnace;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class BlastFurnaceSideConfiguration extends DefaultMachineNoFacePreset {
	public static final BlastFurnaceSideConfiguration INSTANCE = new BlastFurnaceSideConfiguration();

	public BlastFurnaceSideConfiguration() {
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input3);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Input3);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input3);

		for (BlockSide side : BlockSide.values()) {
			if (side == BlockSide.FRONT) {
				continue;
			}
			addPossibleConfiguration(side, MachineSideMode.Output);
			setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);
			setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input2);
			setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input3);
		}
	}
}
