package theking530.staticpower.blockentities.power.resistor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityResistor extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityResistor> TYPE = new BlockEntityTypeAllocator<BlockEntityResistor>("resistor",
			(allocator, pos, state) -> new BlockEntityResistor(allocator, pos, state), ModBlocks.Resistor1W, ModBlocks.Resistor5W, ModBlocks.Resistor10W, ModBlocks.Resistor25W,
			ModBlocks.Resistor50W, ModBlocks.Resistor100W, ModBlocks.Resistor250W, ModBlocks.Resistor500W, ModBlocks.Resistor1KW);

	public final PowerStorageComponent powerStorage;
	private final PowerDistributionComponent powerDistributor;

	public BlockEntityResistor(BlockEntityTypeAllocator<BlockEntityResistor> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				return transferPower(stack, simulate);
			}
		}.setSideConfiguration(ioSideConfiguration));

		powerStorage.setInputVoltageRange(StaticVoltageRange.ANY_VOLTAGE);
		powerStorage.setOutputVoltage(StaticPowerVoltage.ZERO);

		powerStorage.setInputCurrentTypes(CurrentType.ALTERNATING);
		powerStorage.setOutputCurrentType(CurrentType.DIRECT);

		powerStorage.setMaximumInputPower(StaticPowerEnergyUtilities.getMaximumPower());
		powerStorage.setMaximumOutputPower(StaticPowerEnergyUtilities.getMaximumPower());

		powerStorage.setCapacity(0);
	}

	@Override
	public void process() {

	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (getLevel().isClientSide()) {
			return 0;
		}

		PowerStack limitedTransfer = stack.copy();
		limitedTransfer.setPower(Math.min(stack.getPower(), getPowerLimit()));

		double transfered = powerDistributor.manuallyDistributePower(powerStorage, limitedTransfer, simulate);
		if (!simulate) {
			powerStorage.getEnergyTracker().powerAdded(new PowerStack(transfered, stack.getVoltage(), stack.getCurrentType()));
			powerStorage.getEnergyTracker().powerDrained(transfered);
			powerStorage.setOutputVoltage(stack.getVoltage());
		}
		return transfered;
	}

	private double getPowerLimit() {
		BlockResistor block = (BlockResistor) getBlockState().getBlock();
		if (block == null) {
			return 0;
		}
		return block.getPowerLimit();
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
