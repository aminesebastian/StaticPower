package theking530.staticpower.blockentities.nonpowered.cokeoven;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class CokeOvenSideConfiguration extends DefaultMachineNoFacePreset {
	public static final CokeOvenSideConfiguration INSTANCE = new CokeOvenSideConfiguration();

	public CokeOvenSideConfiguration() {
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Output);

		for (BlockSide side : BlockSide.values()) {
			addPossibleConfiguration(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Output);
		}
	}
}
