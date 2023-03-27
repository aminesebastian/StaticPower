package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class DefaultMachineNoFacePreset extends DefaultMachinePreset {
	public static final DefaultMachineNoFacePreset INSTANCE = new DefaultMachineNoFacePreset();

	public DefaultMachineNoFacePreset() {
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Never);
		clearPossibleConfigurations(BlockSide.FRONT);
	}
}
