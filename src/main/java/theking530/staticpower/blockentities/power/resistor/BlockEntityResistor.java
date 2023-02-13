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
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.presets.FrontBackInputOutputOnly;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityResistor extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityResistor> TYPE = new BlockEntityTypeAllocator<BlockEntityResistor>("resistor",
			(allocator, pos, state) -> new BlockEntityResistor(allocator, pos, state), ModBlocks.Resistors.values());

	public final PowerStorageComponent powerStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	private final PowerDistributionComponent powerDistributor;

	public BlockEntityResistor(BlockEntityTypeAllocator<BlockEntityResistor> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Add the power distributor.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", FrontBackInputOutputOnly.INSTANCE));
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
}
