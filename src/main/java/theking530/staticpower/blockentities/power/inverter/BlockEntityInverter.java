package theking530.staticpower.blockentities.power.inverter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityInverter extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityInverter> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityInverter>("inverted",
			(allocator, pos, state) -> new BlockEntityInverter(allocator, pos, state), ModBlocks.Inverter);

	public final PowerStorageComponent powerStorage;
	private final PowerDistributionComponent powerDistributor;

	public BlockEntityInverter(BlockEntityTypeAllocator<BlockEntityInverter> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				double transfered = transferPower(stack, simulate);

				powerStorage.setCapacity(transfered);
				super.addPower(new PowerStack(transfered, stack.getVoltage(), stack.getCurrentType()), simulate);
				super.drainPower(transfered, simulate);
				powerStorage.setCapacity(0);

				return transfered;
			}
		}.setSideConfiguration(ioSideConfiguration));

		powerStorage.setInputVoltageRange(StaticVoltageRange.ANY_VOLTAGE);
		powerStorage.setOutputVoltage(StaticPowerVoltage.ZERO);

		powerStorage.setInputCurrentTypes(CurrentType.DIRECT);
		powerStorage.setOutputCurrentType(CurrentType.ALTERNATING);

		powerStorage.setMaximumInputPower(Double.MAX_VALUE);
		powerStorage.setMaximumOutputPower(Double.MAX_VALUE);

		powerStorage.setCapacity(0);
	}

	@Override
	public void process() {
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (stack.getCurrentType() == CurrentType.DIRECT) {
			powerStorage.setOutputVoltage(stack.getVoltage());
			PowerStack alternatingVersion = new PowerStack(stack.getPower(), stack.getVoltage(), CurrentType.ALTERNATING);
			return powerDistributor.manuallyDistributePower(powerStorage, alternatingVersion, simulate);
		}
		return 0;
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.BACK && side != BlockSide.FRONT) {
			return mode == MachineSideMode.Never;
		}
		return mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}

	@Override
	protected void onSidesConfigUpdate(BlockSide side, MachineSideMode newMode) {
		super.onSidesConfigUpdate(side, newMode);
		MachineSideMode frontMode = ioSideConfiguration.getBlockSideConfiguration(BlockSide.FRONT);
		MachineSideMode backMode = ioSideConfiguration.getBlockSideConfiguration(BlockSide.BACK);

		if (frontMode != backMode) {
			return;
		}

		if (side == BlockSide.FRONT) {
			if (frontMode.isInputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.BACK, MachineSideMode.Output);
			} else if (frontMode.isOutputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.BACK, MachineSideMode.Input);
			}
		} else if (side == BlockSide.BACK) {
			if (backMode.isInputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.FRONT, MachineSideMode.Output);
			} else if (backMode.isOutputMode()) {
				ioSideConfiguration.setBlockSpaceConfiguration(BlockSide.FRONT, MachineSideMode.Input);
			}
		}
	}
}
