package theking530.staticpower.blockentities.components.control.sideconfiguration.presets;

import java.util.Map;

import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class FrontBackInputOutputOnly extends SideConfigurationPreset {
	public static final FrontBackInputOutputOnly INSTANCE = new FrontBackInputOutputOnly();

	protected FrontBackInputOutputOnly() {
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input);
		addPossibleConfiguration(BlockSide.FRONT, MachineSideMode.Output);

		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output);
		addPossibleConfiguration(BlockSide.BACK, MachineSideMode.Input);
	}

	public void modifyAfterChange(BlockSide changedSide, Map<BlockSide, MachineSideMode> configuration) {
		MachineSideMode frontMode = configuration.get(BlockSide.FRONT);
		MachineSideMode backMode = configuration.get(BlockSide.BACK);

		if (frontMode != backMode) {
			return;
		}

		if (changedSide == BlockSide.FRONT) {
			if (frontMode.isInputMode()) {
				configuration.put(BlockSide.BACK, MachineSideMode.Output);
			} else if (frontMode.isOutputMode()) {
				configuration.put(BlockSide.BACK, MachineSideMode.Input);
			}
		} else if (changedSide == BlockSide.BACK) {
			if (backMode.isInputMode()) {
				configuration.put(BlockSide.FRONT, MachineSideMode.Output);
			} else if (backMode.isOutputMode()) {
				configuration.put(BlockSide.FRONT, MachineSideMode.Input);
			}
		}
	}
}
