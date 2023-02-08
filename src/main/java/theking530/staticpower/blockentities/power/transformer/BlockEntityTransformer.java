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
import theking530.api.energy.transformation.PowerTransformDirection;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationPresets;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityTransformer extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> BASIC_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>("transformer_basic",
			(allocator, pos, state) -> new BlockEntityTransformer(allocator, pos, state), ModBlocks.TransformerBasic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> ADVANCED_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>("transformer_advanced",
			(allocator, pos, state) -> new BlockEntityTransformer(allocator, pos, state), ModBlocks.TransformerStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> STATIC_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>("transformer_static",
			(allocator, pos, state) -> new BlockEntityTransformer(allocator, pos, state), ModBlocks.TransformerStatic);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> ENERGIZED_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>("transformer_energized",
			(allocator, pos, state) -> new BlockEntityTransformer(allocator, pos, state), ModBlocks.TransformerEnergized);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityTransformer> LUMUM_TRANSFORMER = new BlockEntityTypeAllocator<BlockEntityTransformer>("transformer_lumum",
			(allocator, pos, state) -> new BlockEntityTransformer(allocator, pos, state), ModBlocks.TransformerLumum);

	public final PowerStorageComponent powerStorage;
	protected final PowerDistributionComponent powerDistributor;
	protected int transformerRatio;

	public BlockEntityTransformer(BlockEntityTypeAllocator<BlockEntityTransformer> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setPreset(SideConfigurationPresets.FRONT_BACK_INPUT_OUTPUT, true);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(Direction side, PowerStack stack, boolean simulate) {
				return transformAndSupplyPower(side, stack, simulate);
			}
		}.setSideConfiguration(ioSideConfiguration));
		powerStorage.setInputVoltageRange(getTierObject().powerConfiguration.getTransformerVoltageRange());
		powerStorage.setOutputVoltage(StaticPowerVoltage.ZERO);

		powerStorage.setInputCurrentTypes(CurrentType.ALTERNATING);
		powerStorage.setOutputCurrentType(CurrentType.ALTERNATING);

		powerStorage.setMaximumInputPower(StaticPowerEnergyUtilities.getMaximumPower());
		powerStorage.setMaximumOutputPower(StaticPowerEnergyUtilities.getMaximumPower());

		powerStorage.setCapacity(0);

		transformerRatio = getTierObject().powerConfiguration.transfomerRatio.get();
	}

	public double transformAndSupplyPower(Direction side, PowerStack stack, boolean simulate) {
		if (getLevel().isClientSide()) {
			return 0;
		}
		// Do nothing if this side is not an input side OR the supplied stack is not
		// alternating.
		if (!ioSideConfiguration.getWorldSpaceDirectionConfiguration(side).isInputMode() || stack.getCurrentType() != CurrentType.ALTERNATING) {
			return 0.0;
		}

		StaticPowerVoltage inputVoltageClass = stack.getVoltage();

		BlockSide inputSide = SideConfigurationUtilities.getBlockSide(side, getFacingDirection());
		boolean isInputOnShortSide = inputSide == BlockSide.FRONT;
		PowerTransformDirection direction = isInputOnShortSide ? PowerTransformDirection.STEP_UP : PowerTransformDirection.STEP_DOWN;

		StaticPowerVoltage outputVoltageClass;
		if (direction == PowerTransformDirection.STEP_UP) {
			outputVoltageClass = inputVoltageClass.upgrade(transformerRatio);

			// Do nothing if we want to step up and the input voltage is higher than the
			// output voltage.
			if (inputVoltageClass.isGreaterThan(outputVoltageClass)) {
				return 0.0;
			}
		} else {
			outputVoltageClass = inputVoltageClass.downgrade(transformerRatio);

			// Do nothing if we want to step down and the input voltage is lower than the
			// output voltage.
			if (inputVoltageClass.isLessThan(outputVoltageClass)) {
				return 0.0;
			}
		}

		double power = Math.min(stack.getPower(), powerStorage.getMaximumPowerOutput());
		PowerStack transformedStack = new PowerStack(power, outputVoltageClass, CurrentType.ALTERNATING);

		double transfered = powerDistributor.manuallyDistributePower(powerStorage, transformedStack, simulate);
		if (!simulate) {
			powerStorage.getEnergyTracker().powerAdded(new PowerStack(transfered, stack.getVoltage(), stack.getCurrentType()));
			powerStorage.getEnergyTracker().powerDrained(transfered);
			powerStorage.setOutputVoltage(outputVoltageClass);
		}
		return transfered;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerTransformer(windowId, inventory, this);
	}

	public int getTransformerRatio() {
		return transformerRatio;
	}

	public void setTransformerRatio(int transformerRatio) {
		this.transformerRatio = transformerRatio;
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
