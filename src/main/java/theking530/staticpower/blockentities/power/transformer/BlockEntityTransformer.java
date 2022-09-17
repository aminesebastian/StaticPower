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
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.SDMath;
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
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityTransformer>(
			(allocator, pos, state) -> new BlockEntityTransformer(allocator, pos, state), ModBlocks.TransformerBasic);

	public final PowerStorageComponent powerStorage;
	protected final PowerDistributionComponent powerDistributor;

	private final StaticVoltageRange possibleOutputVoltageRange;
	private final double maximumPossibleOutputCurrent;

	public BlockEntityTransformer(BlockEntityTypeAllocator<BlockEntityTransformer> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);
		
		// Capture the current and voltage ranges.
		possibleOutputVoltageRange = getTierObject().powerConfiguration.getTransformerVoltageRange();
		maximumPossibleOutputCurrent = getTierObject().powerConfiguration.defaultMaximumPowerOutput.get();

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier()) {
			@Override
			public double addPower(Direction side, PowerStack stack, boolean simulate) {
				return transformAndSupplyPower(side, stack, simulate);
			}
		}.setSideConfiguration(ioSideConfiguration));
		powerStorage.setCapacity(0);
		powerStorage.setInputVoltageRange(getTierObject().powerConfiguration.getTransformerVoltageRange());

		if (getTier() == StaticPowerTiers.CREATIVE) {
			powerStorage.setMaximumOutputPower(100);
			powerStorage.setOutputVoltage(StaticPowerVoltage.MEDIUM);
		} else {
			powerStorage.setOutputVoltage(possibleOutputVoltageRange.minimumVoltage());
			powerStorage.setMaximumOutputPower(Math.floor(maximumPossibleOutputCurrent / 2));
		}
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {

		}
	}

	public double transformAndSupplyPower(Direction side, PowerStack stack, boolean simulate) {
		if (ioSideConfiguration.getWorldSpaceDirectionConfiguration(side).isInputMode() && stack.getCurrentType() == CurrentType.ALTERNATING) {
			double voltageSign = stack.getVoltage() < 0 ? -1 : 1;
			double power = Math.min(stack.getPower(), powerStorage.getOutputVoltage() * powerStorage.getMaximumPowerOutput());
			PowerStack transformedStack = new PowerStack(power, powerStorage.getOutputVoltage() * voltageSign, CurrentType.ALTERNATING);
			return powerDistributor.manuallyDistributePower(powerStorage, transformedStack, simulate);
		}
		return 0;
	}

	public void setOutputVoltage(StaticPowerVoltage voltage) {
		if (possibleOutputVoltageRange.isVoltageInRange(voltage)) {
			powerStorage.setOutputVoltage(voltage);
		}
	}

	public void addMaximumOutputPowerDelta(double deltaCurrent) {
		double newCurrent = SDMath.clamp(powerStorage.getMaximumPowerOutput() + deltaCurrent, 0, maximumPossibleOutputCurrent);
		powerStorage.setMaximumOutputPower(newCurrent);
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
