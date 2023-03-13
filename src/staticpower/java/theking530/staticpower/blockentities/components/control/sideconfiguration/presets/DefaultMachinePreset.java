package theking530.staticpower.blockentities.components.control.sideconfiguration.presets;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class DefaultMachinePreset extends SideConfigurationPreset {
	public static final DefaultMachinePreset INSTANCE = new DefaultMachinePreset();

	public DefaultMachinePreset() {
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Input);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input);

		for (BlockSide side : BlockSide.values()) {
			addPossibleConfiguration(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Output);
			addPossibleConfiguration(side, MachineSideMode.Disabled);
		}
	}
}
