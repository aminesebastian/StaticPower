package theking530.staticpower.blockentities.machines.mixer;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.control.sideconfiguration.presets.DefaultMachinePreset;

public class MixerSideConfiguration extends DefaultMachinePreset {
	public static final MixerSideConfiguration INSTANCE = new MixerSideConfiguration();

	public MixerSideConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			if (side == BlockSide.FRONT) {
				continue;
			}
			addPossibleConfiguration(side, MachineSideMode.Disabled);
			addPossibleConfiguration(side, MachineSideMode.Output);
			addPossibleConfiguration(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Input2);
			addPossibleConfiguration(side, MachineSideMode.Input3);
		}

		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input3);
	}
}
