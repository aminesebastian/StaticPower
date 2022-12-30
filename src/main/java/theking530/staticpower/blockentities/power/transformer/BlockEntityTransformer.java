package theking530.staticpower.blockentities.power.transformer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.api.energy.transformation.PowerTransformDirection;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityTransformer extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> BASIC_STEP_UP_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>(
			"transformer_step_up_basic", (allocator, pos, state) -> new BlockEntityTransformer(PowerTransformDirection.STEP_UP, allocator, pos, state),
			ModBlocks.TransformerStepUpBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> BASIC_STEP_DOWN_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>(
			"transformer_step_down_basic", (allocator, pos, state) -> new BlockEntityTransformer(PowerTransformDirection.STEP_DOWN, allocator, pos, state),
			ModBlocks.TransformerStepDownBasic);

	public final PowerStorageComponent powerStorage;
	protected final PowerDistributionComponent powerDistributor;

	public final PowerTransformDirection direction;
	private final StaticVoltageRange possibleOutputVoltageRange;

	public BlockEntityTransformer(PowerTransformDirection direction, BlockEntityTypeAllocator<BlockEntityTransformer> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		this.direction = direction;

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);

		// Capture the current and voltage ranges.
		possibleOutputVoltageRange = getTierObject().powerConfiguration.getTransformerVoltageRange();

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(Direction side, PowerStack stack, boolean simulate) {
				return transformAndSupplyPower(side, stack, simulate);
			}
		}.setSideConfiguration(ioSideConfiguration));
		powerStorage.setCapacity(0);
		powerStorage.setInputVoltageRange(getTierObject().powerConfiguration.getTransformerVoltageRange());
		powerStorage.setOutputCurrentType(CurrentType.ALTERNATING);

		if (getTier() == StaticPowerTiers.CREATIVE) {
			powerStorage.setOutputVoltage(StaticPowerVoltage.LOW);
		} else {
			powerStorage.setOutputVoltage(possibleOutputVoltageRange.minimumVoltage());
		}
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {

		}
		powerStorage.setMaximumOutputPower(10000);
	}

	public double transformAndSupplyPower(Direction side, PowerStack stack, boolean simulate) {
		// Do nothing if this side is not an input side OR the supplied stack is not
		// alternating.
		if (!ioSideConfiguration.getWorldSpaceDirectionConfiguration(side).isInputMode() || stack.getCurrentType() != CurrentType.ALTERNATING) {
			return 0.0;
		}

		StaticPowerVoltage inputVoltageClass = StaticPowerVoltage.getVoltageClass(stack.getVoltage());
		StaticPowerVoltage outputVoltageClass = StaticPowerVoltage.getVoltageClass(powerStorage.getOutputVoltage());

		// Do nothing if we want to step up and the input voltage is higher than the
		// output voltage.
		if (direction == PowerTransformDirection.STEP_UP && inputVoltageClass.isGreaterThan(outputVoltageClass)) {
			return 0.0;
		}
		// Do nothing if we want to step down and the input voltage is lower than the
		// output voltage.
		if (direction == PowerTransformDirection.STEP_DOWN && inputVoltageClass.isLessThan(outputVoltageClass)) {
			return 0.0;
		}

		double voltageSign = stack.getVoltage() < 0 ? -1 : 1;
		double power = Math.min(stack.getPower(), powerStorage.getMaximumPowerOutput());
		PowerStack transformedStack = new PowerStack(power, powerStorage.getOutputVoltage() * voltageSign, CurrentType.ALTERNATING);
		return powerDistributor.manuallyDistributePower(powerStorage, transformedStack, simulate);
	}

	public void setOutputVoltage(StaticPowerVoltage voltage) {
		if (possibleOutputVoltageRange.isVoltageInRange(voltage)) {
			powerStorage.setOutputVoltage(voltage);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTransformer(windowId, inventory, this);
	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (side != BlockSide.BACK && side != BlockSide.FRONT) {
			return mode == MachineSideMode.Never;
		}
		return mode == MachineSideMode.Output || mode == MachineSideMode.Input;
	}
}
