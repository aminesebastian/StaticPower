package theking530.staticpower.blockentities.nonpowered.experiencehopper;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.control.sideconfiguration.presets.DefaultMachineNoFacePreset;

public class ExperienceHopperPreset extends DefaultMachineNoFacePreset {
	public static final ExperienceHopperPreset INSTANCE = new ExperienceHopperPreset();

	public ExperienceHopperPreset() {
		setDefaultOnSide(BlockSide.BOTTOM, MachineSideMode.Output);
	}
}
