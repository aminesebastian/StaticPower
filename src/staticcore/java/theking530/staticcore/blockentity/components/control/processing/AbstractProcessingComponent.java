package theking530.staticcore.blockentity.components.control.processing;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.StaticCore;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.oldprocessing.OldProcessingContainer.CaptureType;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.blockentity.components.team.TeamComponent;
import theking530.staticcore.init.StaticCoreProductTypes;
import theking530.staticcore.network.StaticCoreMessageHandler;
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
	 * A container for all of our productivity tokens.
	 */
	private final Map<ProductType<?>, ProductionTrackingToken<?>> productionTokens;
	/**
	 * A container for all the products that are going to be produced by the time
	 * that this processing component finishes production.
	 */
	@UpdateSerialize
	private final ProcessingContainer processingContainer;
	/**
	 * The power product id for the owning block. This is used to group similar
	 * machines in the production UI.
	 */
	private PowerProducer powerProducerId;

	/**
	 * This should be the default processing time for this component before any
	 * modifications are added (from upgrades/power satisfaction/etc).
	 */
	@UpdateSerialize
	private int defaultMaxProcessingTime;
	/**
	 * This is the actual max processing time that this component will tick up
	 * towards.
	 */
	@UpdateSerialize
	private int processingTime;
	/**
	 * Indicates for how many ticks this component has been processing. If this gets
	 * larger than {@link #processingTime}, the component will attempt to complete
	 * processing.
	 */
	@UpdateSerialize
	private int currentProcessingTime;
	/**
	 * How many ticks of progress is made per in-game tick. Usually this should be
	 * set to 1 but can be increased for faster processing.
	 */
	@SaveSerialize
	private int processingTicksPerGameTick;
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
	 * Defaulted to 0, this value is the amount of time after it is determined we
	 * can start processing before we start processing. For example, once we
	 * determine we can start smelting an ore, we then wait this amount of ticks
	 * before we actually start smelting it. This gives users a little buffer room
	 * before production starts in case they accidentally started it.
	 */
	private int preProductionTime;
	/**
	 * How long the spin up time has been progressing.
	 */
	private int currentPreProductionTime;

	public AbstractProcessingComponent(String name, int processingTime) {
		super(name);
		this.productionTokens = new HashMap<ProductType<?>, ProductionTrackingToken<?>>();
		this.processingContainer = new ProcessingContainer();
		this.defaultMaxProcessingTime = processingTime;
		this.shouldControlOnBlockState = false;

		this.currentProcessingState = ProcessingCheckState.idle();
		this.processingTicksPerGameTick = 1;
		this.performedWorkLastTick = false;
		this.preProductionTime = 0;
		this.currentPreProductionTime = 0;
	}

	@Override
	public void onRegistered(BlockEntityBase owner) {
		super.onRegistered(owner);
		powerProducerId = new PowerProducer(owner.getBlockState().getBlock());
	}

	public void preProcessUpdate() {
		// Do nothing on the client.
		if (getLevel().isClientSide()) {
			return;
		}

		if (defaultMaxProcessingTime > 0) {
			processingTime = modifyProcessingTime(defaultMaxProcessingTime);
		} else {
			processingTime = 0;
		}

		sendSynchronizationPacket();

		// Process.
		boolean performedWork = performWork();
		if (!performedWork) {
			invalidateProductionTokens();
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
			if (currentProcessingState.isIdle() || (currentProcessingState.isError() && getProcessingProgress() == 0)) {
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
					if (currentPreProductionTime < preProductionTime) {
						currentPreProductionTime++;
						return false;
					} else {
						ProcessingCheckState pendingCheck = canStartProcessing();
						if (!pendingCheck.isOk()) {
							resetToIdle();
							return false;
						}
					}
				}

				currentProcessingState = ProcessingCheckState.ok();
				currentPreProductionTime = 0;
				processingContainer.open();
				onProcessingStarted(processingContainer);
				processingContainer.close();
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
				if (currentProcessingTime < getProcessingTime()) {
					currentProcessingTime = SDMath.clamp(currentProcessingTime + processingTicksPerGameTick, 0,
							getProcessingTime());
					onProcessingProgressMade(processingContainer);

					// Update all the production statistics.
					TeamComponent teamComp = getBlockEntity().getComponent(TeamComponent.class);
					if (teamComp != null && teamComp.getOwningTeam() != null) {
						updateProductionRates(teamComp);
					}

					// If we still have more processing to do, return with true.
					if (currentProcessingTime < getProcessingTime()) {
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
				TeamComponent teamComp = getBlockEntity().getComponent(TeamComponent.class);
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
		currentProcessingTime = 0;
		currentPreProductionTime = 0;
		processingContainer.clear();
		processingContainer.close();
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

	public final int getProcessingTime() {
		return processingTime;
	}

	public final int getProcessingProgress() {
		return currentProcessingTime;
	}

	public final int getDefaultMaxProcessingTime() {
		return defaultMaxProcessingTime;
	}

	public boolean performedWorkLastTick() {
		return performedWorkLastTick;
	}

	public int getProgressScaled(int scaleValue) {
		return (int) (((float) (getProcessingProgress()) / getProcessingTime()) * scaleValue);
	}

	@SuppressWarnings("unchecked")
	public T setDefaultMaxProcessingTime(int defaultMaxProcessingTime) {
		this.defaultMaxProcessingTime = defaultMaxProcessingTime;
		return (T) this;
	}

	public final int getProcessingTicksPerGameTick() {
		return processingTicksPerGameTick;
	}

	@SuppressWarnings("unchecked")
	public T setProcessingTicksPerGameTick(int processingTicksPerGameTick) {
		this.processingTicksPerGameTick = processingTicksPerGameTick;
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

	public final int getCurrentProcessingTime() {
		return currentProcessingTime;
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
		return preProductionTime > 0;
	}

	public int getPreProductionTime() {
		return preProductionTime;
	}

	@SuppressWarnings("unchecked")
	public T setPreProductionTime(int ticks) {
		this.preProductionTime = ticks;
		return (T) this;
	}

	public int getCurrentPreProductionTime() {
		return currentPreProductionTime;
	}

	protected void recordProductionCompletedStatistics(TeamComponent teamComp) {
		for (ProductType<?> productType : processingContainer.geInputProductTypes()) {
			recordProductsConsumedOfType(teamComp, productType);
		}

		for (ProductType<?> productType : processingContainer.getOutputProductTypes()) {
			recordProductsProducedOfType(teamComp, productType);
		}
	}

	private <W extends ProductType<G>, G> void recordProductsConsumedOfType(TeamComponent teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getInputProductsOfType(type)) {
			recordProductConsumed(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void recordProductConsumed(TeamComponent teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH
				|| product.getCaptureType() == CaptureType.COUNT_ONLY && !product.isTemplateItem()) {
			getProductionToken(type).consumed((ServerTeam) teamComp.getOwningTeam(), product.getProduct(),
					product.getAmount());
		}
	}

	private <W extends ProductType<G>, G> void recordProductsProducedOfType(TeamComponent teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getOutputProductsOfType(type)) {
			recordProductProduced(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void recordProductProduced(TeamComponent teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH
				|| product.getCaptureType() == CaptureType.COUNT_ONLY && !product.isTemplateItem()) {
			getProductionToken(type).produced((ServerTeam) teamComp.getOwningTeam(), product.getProduct(),
					product.getAmount());
		}
	}

	protected void updateProductionRates(TeamComponent teamComp) {
		for (ProductType<?> productType : processingContainer.geInputProductTypes()) {
			updateInputProductionRatesOfType(teamComp, productType);
		}

		for (ProductType<?> productType : processingContainer.getOutputProductTypes()) {
			updateOutputProductionRatesOfType(teamComp, productType);
		}
	}

	private <W extends ProductType<G>, G> void updateInputProductionRatesOfType(TeamComponent teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getInputProductsOfType(type)) {
			updateInputProductionRate(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void updateInputProductionRate(TeamComponent teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH || product.getCaptureType() == CaptureType.RATE_ONLY) {
			getProductionToken(type).setConsumptionPerSecond((ServerTeam) teamComp.getOwningTeam(),
					product.getProduct(), product.getAmount() * (1.0 / (getProcessingTime() / 20.0)),
					product.getAmount() * (1.0 / (getDefaultMaxProcessingTime() / 20.0)));
		}
	}

	private <W extends ProductType<G>, G> void updateOutputProductionRatesOfType(TeamComponent teamComp, W type) {
		for (ProcessingProduct<?, G> product : processingContainer.getOutputProductsOfType(type)) {
			updateOutputProductionRate(teamComp, type, product);
		}
	}

	private <W extends ProductType<G>, G> void updateOutputProductionRate(TeamComponent teamComp, W type,
			ProcessingProduct<?, G> product) {
		if (product.getCaptureType() == CaptureType.BOTH || product.getCaptureType() == CaptureType.RATE_ONLY) {
			getProductionToken(type).setProductionPerSecond((ServerTeam) teamComp.getOwningTeam(), product.getProduct(),
					product.getAmount() * (1.0 / (getProcessingTime() / 20.0)),
					product.getAmount() * (1.0 / (getDefaultMaxProcessingTime() / 20.0)));
		}
	}

	protected ProcessingContainer getProcessingContainer() {
		return this.processingContainer;
	}

	public PowerProducer getPowerProducerId() {
		return this.powerProducerId;

	}

	@SuppressWarnings("unchecked")
	protected <G> ProductionTrackingToken<G> getProductionToken(ProductType<G> productType) {
		if (!productionTokens.containsKey(productType)) {
			productionTokens.put(productType, productType.getProductivityToken());
		}
		return (ProductionTrackingToken<G>) productionTokens.get(productType);
	}

	public void onOwningBlockEntityBroken(BlockState state, BlockState newState, boolean isMoving) {
		invalidateProductionTokens();

		// Drop any items in the output container.
		if (getProcessingContainer().hasInputProductsOfType(StaticCoreProductTypes.Item.get())) {
			for (ProcessingProduct<ProductType<ItemStack>, ItemStack> wrapper : getProcessingContainer()
					.getInputProductsOfType(StaticCoreProductTypes.Item.get())) {
				WorldUtilities.dropItem(getLevel(), getPos(), wrapper.getProduct().copy());
			}
		}

		processingContainer.clear();
	}

	protected void invalidateProductionTokens() {
		for (ProductionTrackingToken<?> token : productionTokens.values()) {
			token.invalidate();
		}
		productionTokens.clear();
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
				.abs(lastSyncProcessingTime - this.currentProcessingTime) >= SYNC_UPDATE_DELTA_THRESHOLD;
		shouldSync |= lastSyncProcessingTime == 0 && currentProcessingTime != 0;
		shouldSync |= currentProcessingTime == 0 && lastSyncProcessingTime != 0;

		if (shouldSync) {
			lastSyncProcessingTime = currentProcessingTime;
			K msg = createSynchronizationPacket();
			StaticCoreMessageHandler.sendMessageToPlayerInArea(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, getLevel(),
					getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		}
	}

	protected abstract K createSynchronizationPacket();

	protected void handleClientSynchronizeData(K packet) {
		this.currentProcessingTime = packet.getProccesingTime();
		this.processingTime = packet.getMaxProcessingTime();
		this.currentProcessingState = packet.getProcessingState();
		this.performedWorkLastTick = packet.getPerformedWorkLastTick();
	}

}
