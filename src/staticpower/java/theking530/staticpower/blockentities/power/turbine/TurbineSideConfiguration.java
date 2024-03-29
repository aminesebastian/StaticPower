package theking530.staticpower.blockentities.power.turbine;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class TurbineSideConfiguration extends SideConfigurationPreset {
	public static final TurbineSideConfiguration INSTANCE = new TurbineSideConfiguration();

	protected TurbineSideConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			setDefaultOnSide(side, MachineSideMode.Output);
			addPossibleConfiguration(side, MachineSideMode.Disabled);
		}
		
		clearPossibleConfigurations(BlockSide.TOP);
		setDefaultOnSide(BlockSide.TOP, MachineSideMode.Never);
		
		clearPossibleConfigurations(BlockSide.BOTTOM);
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Never);
	}
}
