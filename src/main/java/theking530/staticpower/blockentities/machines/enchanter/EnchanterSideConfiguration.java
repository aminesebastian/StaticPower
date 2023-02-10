package theking530.staticpower.blockentities.machines.enchanter;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.control.sideconfiguration.presets.DefaultMachinePreset;

public class EnchanterSideConfiguration extends DefaultMachinePreset {
	public static final EnchanterSideConfiguration INSTANCE = new EnchanterSideConfiguration();

	public EnchanterSideConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			if (side == BlockSide.FRONT) {
				continue;
			}
			addPossibleConfiguration(side, MachineSideMode.Disabled);
			addPossibleConfiguration(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Input2);
			addPossibleConfiguration(side, MachineSideMode.Output);
		}

		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output);
	}
}
