package theking530.staticpower.blockentities.power.circuit_breaker;

import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.presets.FrontBackInputOutputOnly;
import theking530.staticcore.blockentity.components.energy.PowerDistributionComponent;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticpower.blockentities.components.TieredPowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityCircuitBreaker extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityCircuitBreaker> TYPE = new BlockEntityTypeAllocator<BlockEntityCircuitBreaker>(
			"circuit_braker", (allocator, pos, state) -> new BlockEntityCircuitBreaker(allocator, pos, state),
			ModBlocks.CircuitBreakers.values());

	public static final int MAX_RESET_DELAY = 5;
	public static final int POST_RESET_TRANSFER_DELAY = 20;

	public final PowerStorageComponent powerStorage;
	public final SideConfigurationComponent ioSideConfiguration;
	private final PowerDistributionComponent powerDistributor;

	private int postResetTransferDelayRemaining;
	private float resetProgress;
	private long lastUntripProgressTick;

	public BlockEntityCircuitBreaker(BlockEntityTypeAllocator<BlockEntityCircuitBreaker> allocator, BlockPos pos,
			BlockState state) {
		super(allocator, pos, state);

		resetProgress = 0;

		// Add the power distributor.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				FrontBackInputOutputOnly.INSTANCE));
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new TieredPowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
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
		if (resetProgress > 0) {
			long ticksSinceLastResetProgress = getLevel().getGameTime() - lastUntripProgressTick;
			if (!isTripped() || ticksSinceLastResetProgress > MAX_RESET_DELAY) {
				resetProgress = 0;
				getLevel().playSound(null, getBlockPos(), SoundEvents.CROSSBOW_LOADING_END, SoundSource.BLOCKS, 0.5f,
						1.0f);
			}
		}

		if (postResetTransferDelayRemaining > 0) {
			postResetTransferDelayRemaining--;
		}
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (getLevel().isClientSide()) {
			return 0;
		}

		if (postResetTransferDelayRemaining > 0) {
			return 0;
		}

		if (isTripped()) {
			return 0;
		}

		double simulatedTransfer = powerDistributor.manuallyDistributePower(powerStorage, stack, true);
		if (simulate) {
			return simulatedTransfer;
		}

		double transferedCurrent = simulatedTransfer / stack.getVoltage().getValue();
		if (transferedCurrent > getTripCurrent()) {
			trip(false);
			return 0;
		}

		double transfered = powerDistributor.manuallyDistributePower(powerStorage, stack, simulate);
		if (!simulate) {
			powerStorage.getEnergyTracker().powerTransfered(stack.copyWithPower(transfered));
			powerStorage.setOutputVoltage(stack.getVoltage());
		}
		return transfered;
	}

	public boolean isTripped() {
		return this.getBlockState().getValue(BlockCircuitBreaker.TRIPPED);
	}

	public boolean resetTrippedState() {
		if (getLevel().isClientSide()) {
			return false;
		}

		if (isTripped()) {
			resetProgress = 0;
			postResetTransferDelayRemaining = POST_RESET_TRANSFER_DELAY;
			getLevel().playSound(null, getBlockPos(), SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.25f, 1.25f);
			getLevel().setBlock(getBlockPos(), this.getBlockState().setValue(BlockCircuitBreaker.TRIPPED, false), 2);
			return true;
		}
		return false;
	}

	public void trip(boolean manual) {
		resetProgress = 0;
		lastUntripProgressTick = getLevel().getGameTime();
		getLevel().playSound(null, getBlockPos(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1.25f, 1.25f);
		getLevel().setBlock(getBlockPos(), this.getBlockState().setValue(BlockCircuitBreaker.TRIPPED, true), 2);

		if (!manual) {
			getLevel().playSound(null, getBlockPos(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 0.25f, 1.25f);
			((ServerLevel) getLevel()).sendParticles(ParticleTypes.POOF, getBlockPos().getX() + 0.5,
					getBlockPos().getY() + 0.75, getBlockPos().getZ() + 0.5, 10, 0.1, 0.1, 0.1, 0);
		}
	}

	private double getTripCurrent() {
		BlockCircuitBreaker block = (BlockCircuitBreaker) getBlockState().getBlock();
		if (block == null) {
			return 0;
		}
		return block.getTripCurrent();
	}

	@Override
	public InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (getLevel().isClientSide()) {
			return InteractionResult.CONSUME;
		}

		if (player.isCrouching()) {
			if (!isTripped()) {
				resetProgress = 0;
				trip(true);
			}
		} else {
			if (isTripped()) {
				if (resetProgress <= 0) {
					getLevel().playSound(null, getBlockPos(), SoundEvents.CROSSBOW_LOADING_START, SoundSource.BLOCKS,
							1.0f, 0.75f);
				}

				float red = SDMath.lerp(1, 0, resetProgress);
				float green = SDMath.lerp(0, 1, resetProgress);

				((ServerLevel) getLevel()).sendParticles(new DustParticleOptions(new Vector3f(red, green, 0.25f), 1.0f),
						getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.9, getBlockPos().getZ() + 0.5, 1, 0.1, 0.1,
						0.1, 0);

				// To prevent the sound from going too long.
				if (resetProgress < 0.6f) {
					getLevel().playSound(null, getBlockPos(), SoundEvents.CROSSBOW_LOADING_MIDDLE, SoundSource.BLOCKS,
							0.1f, SDMath.lerp(0.75f, 1.25f, resetProgress));
				}

				resetProgress += 0.15f;
				lastUntripProgressTick = getLevel().getGameTime();
				if (resetProgress >= 1) {
					resetTrippedState();
				}
				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}
}
