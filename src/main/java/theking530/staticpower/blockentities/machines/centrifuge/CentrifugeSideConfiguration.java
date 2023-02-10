package theking530.staticpower.blockentities.machines.centrifuge;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class CentrifugeSideConfiguration extends DefaultMachineNoFacePreset {
	public static final CentrifugeSideConfiguration INSTANCE = new CentrifugeSideConfiguration();

	public CentrifugeSideConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			addPossibleConfiguration(side, MachineSideMode.Output2);
			addPossibleConfiguration(side, MachineSideMode.Output3);
			addPossibleConfiguration(side, MachineSideMode.Output4);
		}

		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Output2);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Output3);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output4);
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);

		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Never);
		clearPossibleConfigurations(BlockSide.FRONT);
	}
}
