package theking530.staticpower.blockentities.machines.refinery.condenser;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class RefineryCondenserConfiguration extends SideConfigurationPreset {
	public static final RefineryCondenserConfiguration INSTANCE = new RefineryCondenserConfiguration();

	public RefineryCondenserConfiguration() {

		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Output);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Never);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input);
	}
}
