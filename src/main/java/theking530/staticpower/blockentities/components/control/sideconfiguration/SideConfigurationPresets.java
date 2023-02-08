package theking530.staticpower.blockentities.components.control.sideconfiguration;

import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class SideConfigurationPresets {
	public static final SideConfigurationPreset DEFAULT_SIDE_CONFIGURATION = new SideConfigurationPreset();
	public static final SideConfigurationPreset ALL_SIDES_NEVER = new SideConfigurationPreset();
	public static final SideConfigurationPreset ALL_SIDES_OUTPUT = new SideConfigurationPreset();
	public static final SideConfigurationPreset ALL_SIDES_INPUT = new SideConfigurationPreset();
	public static final SideConfigurationPreset TOP_SIDE_ONLY_OUTPUT = new SideConfigurationPreset();
	public static final SideConfigurationPreset FRONT_BACK_INPUT_OUTPUT = new SideConfigurationPreset();
	public static final SideConfigurationPreset BACK_INPUT_ONLY = new SideConfigurationPreset();
	public static final SideConfigurationPreset BACK_OUTPUT_ONLY = new SideConfigurationPreset();

	static {
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.TOP, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.BACK, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.LEFT, true, MachineSideMode.Input);
		DEFAULT_SIDE_CONFIGURATION.setSide(BlockSide.RIGHT, true, MachineSideMode.Input);

		ALL_SIDES_NEVER.setSide(BlockSide.TOP, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.BACK, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		ALL_SIDES_NEVER.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);

		ALL_SIDES_OUTPUT.setSide(BlockSide.TOP, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.BOTTOM, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.FRONT, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.LEFT, true, MachineSideMode.Output);
		ALL_SIDES_OUTPUT.setSide(BlockSide.RIGHT, true, MachineSideMode.Output);

		ALL_SIDES_INPUT.setSide(BlockSide.TOP, true, MachineSideMode.Input);
		ALL_SIDES_INPUT.setSide(BlockSide.BOTTOM, true, MachineSideMode.Input);
		ALL_SIDES_INPUT.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		ALL_SIDES_INPUT.setSide(BlockSide.BACK, true, MachineSideMode.Input);
		ALL_SIDES_INPUT.setSide(BlockSide.LEFT, true, MachineSideMode.Input);
		ALL_SIDES_INPUT.setSide(BlockSide.RIGHT, true, MachineSideMode.Input);

		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.TOP, true, MachineSideMode.Output);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.BACK, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		TOP_SIDE_ONLY_OUTPUT.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);

		FRONT_BACK_INPUT_OUTPUT.setSide(BlockSide.TOP, false, MachineSideMode.Never);
		FRONT_BACK_INPUT_OUTPUT.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		FRONT_BACK_INPUT_OUTPUT.setSide(BlockSide.FRONT, true, MachineSideMode.Input);
		FRONT_BACK_INPUT_OUTPUT.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		FRONT_BACK_INPUT_OUTPUT.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		FRONT_BACK_INPUT_OUTPUT.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);

		BACK_INPUT_ONLY.setSide(BlockSide.TOP, false, MachineSideMode.Never);
		BACK_INPUT_ONLY.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		BACK_INPUT_ONLY.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		BACK_INPUT_ONLY.setSide(BlockSide.BACK, true, MachineSideMode.Input);
		BACK_INPUT_ONLY.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		BACK_INPUT_ONLY.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);

		BACK_OUTPUT_ONLY.setSide(BlockSide.TOP, false, MachineSideMode.Never);
		BACK_OUTPUT_ONLY.setSide(BlockSide.BOTTOM, false, MachineSideMode.Never);
		BACK_OUTPUT_ONLY.setSide(BlockSide.FRONT, false, MachineSideMode.Never);
		BACK_OUTPUT_ONLY.setSide(BlockSide.BACK, true, MachineSideMode.Output);
		BACK_OUTPUT_ONLY.setSide(BlockSide.LEFT, false, MachineSideMode.Never);
		BACK_OUTPUT_ONLY.setSide(BlockSide.RIGHT, false, MachineSideMode.Never);
	}

}
