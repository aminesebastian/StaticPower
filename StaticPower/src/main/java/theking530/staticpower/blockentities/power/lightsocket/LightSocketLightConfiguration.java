package theking530.staticpower.blockentities.power.lightsocket;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class LightSocketLightConfiguration extends SideConfigurationPreset {
	public static final LightSocketLightConfiguration INSTANCE = new LightSocketLightConfiguration();

	protected LightSocketLightConfiguration() {
		for (BlockSide side : BlockSide.values()) {
			setDefaultOnSide(side, MachineSideMode.Never);
		}
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Input);
	}
}
