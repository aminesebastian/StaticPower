package theking530.staticpower.blockentities.machines.pump;

import java.util.Map;

import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationPreset;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class PumpSideConfiguration extends SideConfigurationPreset {
	public static final PumpSideConfiguration INSTANCE = new PumpSideConfiguration();

	protected PumpSideConfiguration() {
		setDefaultOnSide(BlockSide.FRONT, MachineSideMode.Input);
		addPossibleConfiguration(BlockSide.FRONT, MachineSideMode.Output);

		setDefaultOnSide(BlockSide.BACK, MachineSideMode.Output);
		addPossibleConfiguration(BlockSide.BACK, MachineSideMode.Input);

		for (BlockSide side : BlockSide.values()) {
			if (side == BlockSide.FRONT || side == BlockSide.BACK) {
				continue;
			}
			setDefaultOnSide(side, MachineSideMode.Input);
			addPossibleConfiguration(side, MachineSideMode.Disabled);
		}
	}

	public void modifyAfterChange(BlockSide changedSide, Map<BlockSide, MachineSideMode> configuration) {
		if (changedSide != BlockSide.FRONT && changedSide != BlockSide.BACK) {
			return;
		}

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
