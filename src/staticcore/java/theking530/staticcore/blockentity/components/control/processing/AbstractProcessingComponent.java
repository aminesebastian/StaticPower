package theking530.staticcore.blockentity.components.control.processing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.team.ITeamOwnable;
import theking530.staticcore.StaticCore;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.processing.ProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.blockentity.components.team.TeamComponent;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.ProductionTokenContainer;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.productivity.product.power.PowerProducer;
import theking530.staticcore.teams.ServerTeam;
import theking530.staticcore.utilities.math.SDMath;
import theking530.staticcore.world.WorldUtilities;

public abstract class AbstractProcessingComponent<T extends AbstractProcessingComponent<?, K>, K extends AbstractProcessingComponentSyncPacket>
		extends AbstractBlockEntityComponent {
	private static final int SYNC_PACKET_UPDATE_RADIUS = 32;
	private static final int SYNC_UPDATE_DELTA_THRESHOLD = 20;

	/**
	 * The productivity container used to manage production tokens.
	 */
	private ProductionTokenContainer productionTokenContainer;

	/**
	 * A container for all the products that are going to be produced by the time
	 * that this processing component finishes production.
	 */
	@UpdateSerialize
	private final ProcessingContainer processingContainer;

	/**
	 * This should be the default processing time for this component before any
	 * modifications are added (from upgrades/power satisfaction/etc).
	 */
	@UpdateSerialize
	private int baseProcessingTime;

	/**
	 * This is the timer keeping track of how far along we are in production.
	 */
	@UpdateSerialize
	private Timer processingTimer;
	/**
	 * This indicates the last processing time value for which we issued a sync
	 * packet to the client.
	 */
	private int lastSyncProcessingTime;
	/**
	 * Indicates whether or not this processing component performed any processing
	 * on the last tick.
	 */
	private boolean performedWorkLastTick;

	/**
	 * Indicates whether or not processing has been paused by an external input.
	 */
	private boolean processingPaused;

	/**
	 * If true, this component will set the IS_ON blockstate property on the block
	 * of the tile entity it controls whenever it is processing.
	 */
	@SaveSerialize
	private boolean shouldControlOnBlockState;
	/**
	 * This is the state this processing component is currently in.
	 */
	@UpdateSerialize
	private ProcessingCheckState currentProcessingState;
	/**
	 * Internal value used to smooth out the blockstate control. This exists so we
	 * don't enter a flickering state between ON and OFF.
	 */
	private int blockStateOffTimer;

	/**
	 * Defaulted to 0, this timer tracks the amount of time after it is determined
	 * we can start processing before we start processing. For example, once we
	 * determine we can start smelting an ore, we then wait for this timer to elapse
	 * before we actually start smelting it. This gives users a little buffer room
	 * before production starts in case they accidentally started it.
	 */
	private Timer preProductionTimer;

	/**
	 * The amount of experience that has been accrued during processing.
	 */
	@SaveSerialize
	private float accumulatedExperience;

	public AbstractProcessingComponent(String name, int processingTime) {
		super(name);
		this.productionTokenContainer = new ProductionTokenContainer();
		this.processingContainer = new ProcessingContainer();
		this.baseProcessingTime = processingTime;
		this.shouldControlOnBlockState = false;

		this.currentProcessingState = ProcessingCheckState.idle();
		this.performedWorkLastTick = false;

		this.preProductionTimer = new Timer(0);
		this.processingTimer = new Timer(processingTime);
	}

	@Override
	public void onRegistered(BlockEntityBase owner) {
		super.onRegistered(owner);
		productionTokenContainer.setPowerProducerId(new PowerProducer(owner.getBlockState().getBlock()));
	}

	public void preProcessUpdate() {
		// Do nothing on the client.
		if (getLevel().isClientSide()) {
			return;
		}

		if (baseProcessingTime > 0) {
			processingTimer.setMaxTime(modifyProcessingTime(baseProcessingTime));
		} else {
			processingTimer.setMaxTime(0);
		}

		sendSynchronizationPacket();

		// Process.
		boolean performedWork = performWork();
		if (!performedWork) {
			productionTokenContainer.invalidateProductionTokens();
		}

		// Check for changing to the off state.
		if (!performedWork) {
			// If the block state is on, and processing is false, start the blockStateOff
			// timer. If it elapses, set the block state to off. Otherwise, start
			// incrementing it.
			if (isBlockStateOn()) {
				if (blockStateOffTimer > 20) {
					setBlockStateOnValue(false);
					blockStateOffTimer = 0;
				} else {
					blockStateOffTimer++;
				}
			}
		} else {
			// Reset the off timer.
			blockStateOffTimer = 0;
			// Update the block's on state.
			setBlockStateOnValue(true);
		}

		performedWorkLastTick = performedWork;
	}

	protected boolean performWork() {
		try {
			if (processingPaused) {
				return false;
			}

			if (currentProcessingState.isIdle()
					|| (currentProcessingState.isError() && processingTimer.getCurrentTime() == 0)) {
				currentProcessingState = canStartProcessing();

				if (currentProcessingState.isOk()) {
					currentProcessingState = ProcessingCheckState.pending();
				} else {
					if (!currentProcessingState.isError()) {
						// Only clear the processing state to idle if the non-ok state was not an error.
						// We still want to surface the error if there was one.
						resetToIdle();
					}
					return false;
				}
			}

			if (currentProcessingState.isPending()) {
				// If we have a preproduction timer, start ticking that down.
				// Only call #canStartProcessing again if we have a preProduction timer and it
				// has elapsed, otherwise we don't need to because we're still in the same
				// iteration where we called it above.
				if (hasPreProductionTimer()) {
					if (preProductionTimer.increment()) {
						ProcessingCheckState pendingCheck = canStartProcessing();
						if (!pendingCheck.isOk()) {
							resetToIdle();
							return false;
						}
					} else {
						return false;
					}
				}

				currentProcessingState = ProcessingCheckState.ok();
				preProductionTimer.reset();
				onProcessingStarted(processingContainer);
			}

			// Check if we can continue processing if we made it this far.
			currentProcessingState = canContinueProcessing();
			if (!currentProcessingState.isOk()) {
				if (currentProcessingState.isError()) {
					onProcessingPausedDueToError(processingContainer);
				} else if (currentProcessingState.isCancel()) {
					onProcessingCanceled(processingContainer);
					resetToIdle();
				}
				return false;
			} else {
				if (!processingTimer.hasElapsed()) {
					processingTimer.increment();
					onProcessingProgressMade(processingContainer);

					// Update all the production statistics.
					ITeamOwnable teamComp = getBlockEntity().getComponent(TeamComponent.class);
					if (teamComp != null && teamComp.getOwningTeam() != null) {
						updateProductionRates(teamComp);
					}

					// If we still have more processing to do, return with true.
					if (!processingTimer.hasElapsed()) {
						return true;
					}
				}
			}

			// If we made it this far, we can continue processing.
			// Now we want to check if we can complete processing.
			currentProcessingState = canCompleteProcessing();
			if (!currentProcessingState.isOk()) {
				if (currentProcessingState.isError()) {
					onProcessingPausedDueToError(processingContainer);
				} else if (currentProcessingState.isCancel()) {
					onProcessingCanceled(processingContainer);
					resetToIdle();
				}
				return false;
			} else {
				onProcessingCompleted(processingContainer);
				ITeamOwnable teamComp = getBlockEntity().getComponent(TeamComponent.class);
				if (teamComp != null && teamComp.getOwningTeam() != null) {
					recordProductionCompletedStatistics(teamComp);
				}
				resetToIdle();
				return true;
			}
		} catch (Exception e) {
			StaticCore.LOGGER.error(
					String.format("An error occured when attempting to process with component: %1$s at position: %2$s.",
							getComponentName(), getPos()),
					e);
			resetToIdle();
			return false;
		}
	}

	protected void resetToIdle() {
		currentProcessingState = ProcessingCheckState.idle();
		processingTimer.reset();
		preProductionTimer.reset();
		processingContainer.clear();
	}

	protected int modifyProcessingTime(int defaultProcessingTime) {
		return defaultProcessingTime;
	}

	protected abstract ProcessingCheckState canStartProcessing();

	protected abstract ProcessingCheckState canContinueProcessing();

	protected abstract ProcessingCheckState canCompleteProcessing();

	protected void onProcessingStarted(ProcessingContainer processingContainer) {

	}

	protected void onProcessingProgressMade(ProcessingContainer processingContainer) {

	}

	protected void onProcessingCanceled(ProcessingContainer processingContainer) {

	}

	protected void onProcessingPausedDueToError(ProcessingContainer processingContainer) {

	}

	protected void onProcessingCompleted(ProcessingContainer processingContainer) {

	}

	public boolean isProcessingPaused() {
		return processingPaused;
	}

	public void pauseProcessing() {
		this.processingPaused = true;
	}

	public void resumeProcessing() {
		this.processingPaused = false;
	}

	public final Timer getProcessingTimer() {
		return processingTimer;
	}

	public boolean hasProcessingStarted() {
		return processingTimer.getCurrentTime() > 0;
	}

	public final int getBaseProcessingTime() {
		return baseProcessingTime;
	}

	public boolean performedWorkLastTick() {
		return performedWorkLastTick;
	}

	@SuppressWarnings("unchecked")
	public T setBaseProcessingTime(int baseProcessingTime) {
		this.baseProcessingTime = baseProcessingTime;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T setProcessingTicksPerGameTick(int processingTicksPerGameTick) {
		processingTimer.setRate(processingTicksPerGameTick);
		return (T) this;
	}

	public final boolean shouldControlBlockState() {
		return shouldControlOnBlockState;
	}

	@SuppressWarnings("unchecked")
	public T setShouldControlOnBlockState(boolean shouldControlOnBlockState) {
		this.shouldControlOnBlockState = shouldControlOnBlockState;
		return (T) this;
	}

	public final ProcessingCheckState getProcessingState() {
		return currentProcessingState;
	}

	@SuppressWarnings("resource")
	protected void setBlockStateOnValue(boolean on) {
		if (!getLevel().isClientSide && shouldControlOnBlockState) {
			BlockState currentState = getLevel().getBlockState(getPos());
			if (currentState.hasProperty(StaticCoreBlock.IS_ON)) {
				if (currentState.getValue(StaticCoreBlock.IS_ON) != on) {
					getLevel().setBlock(getPos(), currentState.setValue(StaticCoreBlock.IS_ON, on), 2);
				}
			}
		}
	}

	public boolean isBlockStateOn() {
		if (!shouldControlOnBlockState) {
			return false;
		}
		BlockState currentState = getLevel().getBlockState(getPos());
		if (currentState.hasProperty(StaticCoreBlock.IS_ON)) {
			return currentState.getValue(StaticCoreBlock.IS_ON);
		}
		return false;
	}

	public boolean hasPreProductionTimer() {
		return preProductionTimer.getMaxTime() > 0;
	}

	public Timer getPreProductionTimer() {
		return preProductionTimer;
	}

	@SuppressWarnings("unchecked")
	public T setPreProductionTime(int ticks) {
		this.preProductionTimer.setMaxTime(ticks);
		return (T) this;
	}

	protected void recordProductionCompletedStatistics(ITeamOwnable teamComp) {
		for (ProductType<?> productType : processingContainer.getInputs().getProductTypes()) {
			recordProductsConsumedOfType(teamComp, productType);
		}

		for (ProductType<?> productType : processingContainer.getOutputs().getProductTypes()) {
			recordProductsProducedOfType(teamComp, productType);
		}
	}

	private <W extends ProductType<G>, G> void recordProductsConsumedOfType(ITeamOwnable teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getInputs().getProductsOfType(type)) {
			recordProductConsumed(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void recordProductConsumed(ITeamOwnable teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH
				|| product.getCaptureType() == CaptureType.COUNT_ONLY && !product.isTemplateProduct()) {
			getProductionToken(type).consumed((ServerTeam) teamComp.getOwningTeam(), product.getProduct(),
					product.getAmount());
		}
	}

	private <W extends ProductType<G>, G> void recordProductsProducedOfType(ITeamOwnable teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getOutputs().getProductsOfType(type)) {
			recordProductProduced(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void recordProductProduced(ITeamOwnable teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH
				|| product.getCaptureType() == CaptureType.COUNT_ONLY && !product.isTemplateProduct()) {
			getProductionToken(type).produced((ServerTeam) teamComp.getOwningTeam(), product.getProduct(),
					product.getAmount());
		}
	}

	protected void updateProductionRates(ITeamOwnable teamComp) {
		for (ProductType<?> productType : processingContainer.getInputs().getProductTypes()) {
			updateInputProductionRatesOfType(teamComp, productType);
		}

		for (ProductType<?> productType : processingContainer.getOutputs().getProductTypes()) {
			updateOutputProductionRatesOfType(teamComp, productType);
		}
	}

	private <W extends ProductType<G>, G> void updateInputProductionRatesOfType(ITeamOwnable teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getInputs().getProductsOfType(type)) {
			updateInputProductionRate(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void updateInputProductionRate(ITeamOwnable teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH || product.getCaptureType() == CaptureType.RATE_ONLY) {
			getProductionToken(type).setConsumptionPerSecond((ServerTeam) teamComp.getOwningTeam(),
					product.getProduct(), product.getAmount() * (1.0 / (getProcessingTimer().getMaxTime() / 20.0)),
					product.getAmount() * (1.0 / (getBaseProcessingTime() / 20.0)));
		}
	}

	private <W extends ProductType<G>, G> void updateOutputProductionRatesOfType(ITeamOwnable teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getOutputs().getProductsOfType(type)) {
			updateOutputProductionRate(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void updateOutputProductionRate(ITeamOwnable teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH || product.getCaptureType() == CaptureType.RATE_ONLY) {
			getProductionToken(type).setProductionPerSecond((ServerTeam) teamComp.getOwningTeam(), product.getProduct(),
					product.getAmount() * (1.0 / (getProcessingTimer().getMaxTime() / 20.0)),
					product.getAmount() * (1.0 / (getBaseProcessingTime() / 20.0)));
		}
	}

	protected ProcessingContainer getProcessingContainer() {
		return this.processingContainer;
	}

	public IReadOnlyProcessingContainer getProcessingInputs() {
		return processingContainer.getInputs();
	}

	public IReadOnlyProcessingContainer getProcessingOutputs() {
		return processingContainer.getOutputs();
	}

	public float getAccumulatedExperience() {
		return accumulatedExperience;
	}

	public void clearAccumulatedExperience() {
		accumulatedExperience = 0.0f;
	}

	public void applyExperience(Player player) {
		if (getAccumulatedExperience() > 0) {
			int points = (int) getAccumulatedExperience();
			float chanceOfAnotherPoint = getAccumulatedExperience() - points;
			if (chanceOfAnotherPoint > 0) {
				if (SDMath.diceRoll(chanceOfAnotherPoint)) {
					points += 1;
				}
			}

			WorldUtilities.dropExperience(getLevel(), getPos(), points);
			clearAccumulatedExperience();
		}
	}

	public PowerProducer getPowerProducerId() {
		return productionTokenContainer.getPowerProducerId();

	}

	public <G> ProductionTrackingToken<G> getProductionToken(ProductType<G> productType) {
		return productionTokenContainer.getProductionToken(productType);
	}

	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		productionTokenContainer.invalidateProductionTokens();

		// Drop any items in the output container.
		if (getProcessingContainer().getInputs().hasProductsOfType(StaticCoreProductTypes.Item.get())) {
			for (ProcessingProduct<?, ItemStack> wrapper : getProcessingContainer().getInputs()
					.getProductsOfType(StaticCoreProductTypes.Item.get())) {
				WorldUtilities.dropItem(getLevel(), getPos(), wrapper.getProduct().copy());
			}
		}

		processingContainer.clear();
	}

	@SuppressWarnings("unchecked")
	public final void recieveClientSynchronizeData(AbstractProcessingComponentSyncPacket packet) {
		handleClientSynchronizeData((K) packet);
	}

	protected final void sendSynchronizationPacket() {
		if (getLevel().isClientSide()) {
			StaticCore.LOGGER.warn("#synchronizeToClient (called at %1$s) should only be called from the server!",
					getPos().toString());
			return;
		}

		boolean shouldSync = Math
				.abs(lastSyncProcessingTime - this.processingTimer.getCurrentTime()) >= SYNC_UPDATE_DELTA_THRESHOLD;
		shouldSync |= lastSyncProcessingTime == 0 && processingTimer.getCurrentTime() != 0;
		shouldSync |= processingTimer.getCurrentTime() == 0 && lastSyncProcessingTime != 0;

		if (shouldSync) {
			lastSyncProcessingTime = processingTimer.getCurrentTime();
			K msg = createSynchronizationPacket();
			StaticCoreMessageHandler.sendMessageToPlayerInArea(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, getLevel(),
					getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		}
	}

	protected abstract K createSynchronizationPacket();

	protected void handleClientSynchronizeData(K packet) {
		this.processingTimer = packet.getProccesingTime();
		this.currentProcessingState = packet.getProcessingState();
		this.performedWorkLastTick = packet.getPerformedWorkLastTick();
	}

}
