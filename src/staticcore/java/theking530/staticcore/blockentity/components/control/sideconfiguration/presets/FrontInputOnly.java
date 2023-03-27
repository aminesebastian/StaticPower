package theking530.staticcore.blockentity.components.control.sideconfiguration.presets;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class FrontInputOnly extends SideConfigurationPreset {
	public static final FrontInputOnly INSTANCE = new FrontInputOnly();

	protected FrontInputOnly() {
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input);
		addPossibleConfiguration(BlockSide.FRONT, MachineSideMode.Disabled);
	}
}
