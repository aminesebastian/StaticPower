package theking530.staticpower.blockentities.machines.refinery.fluidio;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class RefineryFluidOutputSideConfiguration extends SideConfigurationPreset {
	public static final RefineryFluidOutputSideConfiguration INSTANCE = new RefineryFluidOutputSideConfiguration();

	public RefineryFluidOutputSideConfiguration() {

		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Output2);
		setDefaultOnSide(BlockSide.RIGHT, MachineSideMode.Output3);
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Output4);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Input2);
		setDefaultOnSide(BlockSide.LEFT, MachineSideMode.Output3);
		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output4);

		for (BlockSide side : BlockSide.values()) {
			addPossibleConfiguration(side, MachineSideMode.Disabled);
			addPossibleConfiguration(side, MachineSideMode.Output2);
			addPossibleConfiguration(side, MachineSideMode.Output3);
			addPossibleConfiguration(side, MachineSideMode.Output4);
		}
	}
}
