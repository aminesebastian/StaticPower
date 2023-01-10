package theking530.staticpower.blockentities.power.circuit_breaker;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
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

public class BlockEntityCircuitBreaker extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCircuitBreaker> TYPE_5_A = new BlockEntityTypeAllocator<BlockEntityCircuitBreaker>("circuit_braker_5",
			(allocator, pos, state) -> new BlockEntityCircuitBreaker(allocator, pos, state), ModBlocks.CircuitBreaker5A);

	public final PowerStorageComponent powerStorage;
	private final PowerDistributionComponent powerDistributor;

	private float resetProgress;
	private long lastUntripProgressTick;

	private boolean isTripped;

	public BlockEntityCircuitBreaker(BlockEntityTypeAllocator<BlockEntityCircuitBreaker> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		resetProgress = 0;
		isTripped = false;

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				double transferedPower = transferPower(stack, simulate);
				if (!simulate) {
					setCapacity(transferedPower);
					super.addPower(new PowerStack(transferedPower, stack.getVoltage(), stack.getCurrentType()), simulate);
					drainPower(transferedPower, simulate);
					setCapacity(0);
				}
				return transferedPower;
			}
		}.setSideConfiguration(ioSideConfiguration));

		powerStorage.setInputVoltageRange(StaticVoltageRange.ANY_VOLTAGE);
		powerStorage.setOutputVoltage(StaticPowerVoltage.ZERO);

		powerStorage.setInputCurrentTypes(CurrentType.ALTERNATING);
		powerStorage.setOutputCurrentType(CurrentType.DIRECT);

		powerStorage.setMaximumInputPower(Double.MAX_VALUE);
		powerStorage.setMaximumOutputPower(Double.MAX_VALUE);

		powerStorage.setCapacity(0);

		isTripped = false;
	}

	@Override
	public void process() {
		if (resetProgress > 0) {
			long ticksSinceLastResetProgress = getLevel().getGameTime() - lastUntripProgressTick;
			if (!isTripped || ticksSinceLastResetProgress > 5) {
				resetProgress = 0;
				getLevel().playSound(null, getBlockPos(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.BLOCKS, 0.5f, 1.0f);
			}
		}
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (isTripped) {
			return 0;
		}

		double simulatedTransfer = powerDistributor.manuallyDistributePower(powerStorage, stack, true);
		if (simulate) {
			return simulatedTransfer;
		}

		double transferedCurrent = simulatedTransfer / stack.getVoltage().getValue();
		if (transferedCurrent > getTripCurrent()) {
			isTripped = true;
			lastUntripProgressTick = getLevel().getGameTime();
			getLevel().playSound(null, getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.5f, 1.25f);
			getLevel().playSound(null, getBlockPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.5f, 1.25f);
			return 0;
		}

		return powerDistributor.manuallyDistributePower(powerStorage, stack, false);
	}

	public boolean resetTrippedState(Player player) {
		if (isTripped) {
			isTripped = false;
			resetProgress = 0;
			getLevel().playSound(null, getBlockPos(), SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.25f, 1.25f);
			getLevel().playSound(null, getBlockPos(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 1.0f, 0.75f);
			return true;
		}
		return false;
	}

	private double getTripCurrent() {
		BlockCircuitBreaker block = (BlockCircuitBreaker) getBlockState().getBlock();
		if (block == null) {
			return 0;
		}
		return block.getTripCurrent();
	}

	@Override
	public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
		if (getLevel().isClientSide()) {
			return InteractionResult.CONSUME;
		}

		if (isTripped) {
			if (resetProgress <= 0) {
				getLevel().playSound(null, getBlockPos(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.BLOCKS, 1.5f, 1.0f);
			}

			resetProgress += 0.1f;
			lastUntripProgressTick = getLevel().getGameTime();
			if (resetProgress >= 1) {
				resetTrippedState(player);
			}
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
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
