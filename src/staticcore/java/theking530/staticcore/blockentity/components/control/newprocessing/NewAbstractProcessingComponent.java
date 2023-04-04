package theking530.staticcore.blockentity.components.control.newprocessing;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.StaticCore;
import theking530.staticcore.block.StaticCoreBlock;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.processing.ProcessingCheckState;
import theking530.staticcore.blockentity.components.serialization.SaveSerialize;
import theking530.staticcore.blockentity.components.serialization.UpdateSerialize;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.productivity.ProductionTrackingToken;
import theking530.staticcore.productivity.product.ProductType;
import theking530.staticcore.utilities.math.SDMath;

public abstract class NewAbstractProcessingComponent<T extends NewAbstractProcessingComponent<T, K>, K extends NewProcessingComponentSyncPacket<K>>
		extends AbstractBlockEntityComponent {
	private static final int SYNC_PACKET_UPDATE_RADIUS = 32;
	private static final int SYNC_UPDATE_DELTA_THRESHOLD = 20;

	private final Map<ProductType<?>, ProductionTrackingToken<?>> productionTokens;

	@UpdateSerialize
	private int defaultMaxProcessingTime;
	@UpdateSerialize
	private int currentProcessingTimer;
	@SaveSerialize
	private int tickDownRate;
	private int lastSyncProcessingTime;

	@SaveSerialize
	private boolean shouldControlOnBlockState;
	@UpdateSerialize
	private ProcessingCheckState currentProcessingState;
	private int blockStateOffTimer;

	public NewAbstractProcessingComponent(String name, int processingTime) {
		super(name);
		this.productionTokens = new HashMap<ProductType<?>, ProductionTrackingToken<?>>();
		this.defaultMaxProcessingTime = processingTime;
		this.shouldControlOnBlockState = false;
		this.currentProcessingState = ProcessingCheckState.cancel();
	}

	public void preProcessUpdate() {
		// Do nothing on the client.
		if (getLevel().isClientSide()) {
			return;
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
			if (getIsOnBlockState()) {
				if (blockStateOffTimer > 20) {
					setIsOnBlockState(false);
					blockStateOffTimer = 0;
				} else {
					blockStateOffTimer++;
				}
			}
		} else {
			// Reset the off timer.
			blockStateOffTimer = 0;
			// Update the block's on state.
			setIsOnBlockState(true);
		}

	}

	public boolean performWork() {
		if (currentProcessingState.isIdle()) {
			currentProcessingState = canStartProcessing();
			if (currentProcessingState.isOk()) {
				onProcessingStarted();
			} else if (currentProcessingState.isError()) {
				onProcessingPausedDueToError();
				return false;
			} else {
				// If we didn't receive an OK we still want to return early.
				currentProcessingState = ProcessingCheckState.idle();
				return false;
			}

			currentProcessingTimer = getMaxProcessingTime();
		}

		// Check if we can continue processing if we made it this far.
		currentProcessingState = canContinueProcessing();
		if (!currentProcessingState.isOk()) {
			if (currentProcessingState.isError()) {
				onProcessingPausedDueToError();
			} else if (currentProcessingState.isCancel()) {
				onProcessingCanceled();
				currentProcessingState = ProcessingCheckState.idle();
			}
			return false;
		} else {
			if (currentProcessingTimer < getMaxProcessingTime()) {
				currentProcessingTimer = SDMath.clamp(currentProcessingTimer + tickDownRate, 0, getMaxProcessingTime());
				return true;
			}
		}

		// If we made it this far, we can continue processing.
		// Now we want to check if we can complete processing.
		currentProcessingState = canCompleteProcessing();
		if (!currentProcessingState.isOk()) {
			if (currentProcessingState.isError()) {
				onProcessingPausedDueToError();
			} else if (currentProcessingState.isCancel()) {
				onProcessingCanceled();
				currentProcessingState = ProcessingCheckState.idle();
			}
			return false;
		} else {
			onProcessingCompleted();
			currentProcessingState = ProcessingCheckState.idle();
			return true;
		}
	}

	protected abstract ProcessingCheckState canStartProcessing();

	protected abstract ProcessingCheckState canContinueProcessing();

	protected abstract ProcessingCheckState canCompleteProcessing();

	protected abstract void onProcessingStarted();

	protected abstract void onProcessingCanceled();

	protected abstract void onProcessingPausedDueToError();

	protected abstract void onProcessingCompleted();

	public int getMaxProcessingTime() {
		return defaultMaxProcessingTime;
	}

	@SuppressWarnings("resource")
	protected void setIsOnBlockState(boolean on) {
		if (!getLevel().isClientSide && shouldControlOnBlockState) {
			BlockState currentState = getLevel().getBlockState(getPos());
			if (currentState.hasProperty(StaticCoreBlock.IS_ON)) {
				if (currentState.getValue(StaticCoreBlock.IS_ON) != on) {
					getLevel().setBlock(getPos(), currentState.setValue(StaticCoreBlock.IS_ON, on), 2);
				}
			}
		}
	}

	public boolean getIsOnBlockState() {
		if (!shouldControlOnBlockState) {
			return false;
		}
		BlockState currentState = getLevel().getBlockState(getPos());
		if (currentState.hasProperty(StaticCoreBlock.IS_ON)) {
			return currentState.getValue(StaticCoreBlock.IS_ON);
		}
		return false;
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
	}

	protected void invalidateProductionTokens() {
		for (ProductionTrackingToken<?> token : productionTokens.values()) {
			token.invalidate();
		}
		productionTokens.clear();
	}

	protected final void sendSynchronizationPacket() {
		if (getLevel().isClientSide()) {
			StaticCore.LOGGER.warn("#synchronizeToClient (called at %1$s) should only be called from the server!",
					getPos().toString());
			return;
		}

		boolean shouldSync = Math
				.abs(lastSyncProcessingTime - this.currentProcessingTimer) >= SYNC_UPDATE_DELTA_THRESHOLD;
		shouldSync |= lastSyncProcessingTime == 0 && currentProcessingTimer != 0;
		shouldSync |= currentProcessingTimer == 0 && lastSyncProcessingTime != 0;

		if (shouldSync) {
			lastSyncProcessingTime = currentProcessingTimer;
			K msg = createSynchronizationPacket();
			StaticCoreMessageHandler.sendMessageToPlayerInArea(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL, getLevel(),
					getPos(), SYNC_PACKET_UPDATE_RADIUS, msg);
		}
	}

	protected abstract K createSynchronizationPacket();

	public abstract void recieveClientSynchronizeData(K packet);
}
