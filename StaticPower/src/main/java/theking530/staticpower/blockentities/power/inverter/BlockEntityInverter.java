package theking530.staticpower.blockentities.power.inverter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.FrontBackInputOutputOnly;
import theking530.staticcore.blockentity.components.energy.PowerDistributionComponent;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.TieredPowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityInverter extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityInverter> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityInverter>("inverted",
			(allocator, pos, state) -> new BlockEntityInverter(allocator, pos, state), ModBlocks.Inverter);

	public final PowerStorageComponent powerStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	private final PowerDistributionComponent powerDistributor;

	public BlockEntityInverter(BlockEntityTypeAllocator<BlockEntityInverter> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Add the power distributor.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", FrontBackInputOutputOnly.INSTANCE));
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new TieredPowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				return transferPower(stack, simulate);
			}
		}.setSideConfiguration(ioSideConfiguration));

		powerStorage.setInputVoltageRange(StaticVoltageRange.ANY_VOLTAGE);
		powerStorage.setOutputVoltage(StaticPowerVoltage.ZERO);

		powerStorage.setInputCurrentTypes(CurrentType.DIRECT);
		powerStorage.setOutputCurrentType(CurrentType.ALTERNATING);

		powerStorage.setMaximumInputPower(StaticPowerEnergyUtilities.getMaximumPower());
		powerStorage.setMaximumOutputPower(StaticPowerEnergyUtilities.getMaximumPower());

		powerStorage.setCapacity(0);
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (getLevel().isClientSide()) {
			return 0;
		}
		if (stack.getCurrentType() == CurrentType.DIRECT) {
			PowerStack alternatingVersion = new PowerStack(stack.getPower(), stack.getVoltage(), CurrentType.ALTERNATING);

			double transfered = powerDistributor.manuallyDistributePower(powerStorage, alternatingVersion, simulate);
			if (!simulate) {
				powerStorage.getEnergyTracker().powerAdded(new PowerStack(transfered, stack.getVoltage(), stack.getCurrentType()));
				powerStorage.getEnergyTracker().powerDrained(transfered);
				powerStorage.setOutputVoltage(stack.getVoltage());
			}
			return transfered;
		}
		return 0;
	}
}
