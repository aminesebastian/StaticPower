package theking530.staticpower.blockentities.machines.refinery.fluidinput;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class RefineryFluidInputSideConfiguration extends SideConfigurationPreset {
	public static final RefineryFluidInputSideConfiguration INSTANCE = new RefineryFluidInputSideConfiguration();

	public RefineryFluidInputSideConfiguration() {

		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Input3);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Input3);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Input3);

		for (BlockSide side : BlockSide.values()) {
			addPossibleConfiguration(side, MachineSideMode.Disabled);
			addPossibleConfiguration(side, MachineSideMode.Output2);
			addPossibleConfiguration(side, MachineSideMode.Output3);
			addPossibleConfiguration(side, MachineSideMode.Input2);
			addPossibleConfiguration(side, MachineSideMode.Input3);
		}
	}
}
