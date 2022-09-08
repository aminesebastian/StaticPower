package theking530.staticpower.blockentities.components.control;

import java.util.function.Supplier;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticpower.blockentities.components.AbstractTileEntityComponent;
import theking530.staticpower.blockentities.components.serialization.SaveSerialize;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;

public class RedstonePulseReactorComponent extends AbstractTileEntityComponent {
	private final Supplier<Boolean> onPulseEvent;
	private Supplier<Boolean> canProcess;
	@SaveSerialize
	private boolean shouldControlBlockStateOnProperty;
	@SaveSerialize
	private int tickTimer;
	@SaveSerialize
	private int postPulseDelay;
	@SaveSerialize
	private boolean isTickingToEvent;
	@SaveSerialize
	private boolean lastRedstoneWasHigh;

	public RedstonePulseReactorComponent(String name, int postPulseDelay, Supplier<Boolean> onPulseEvent) {
		super(name);
		this.onPulseEvent = onPulseEvent;
		this.postPulseDelay = postPulseDelay;
		this.shouldControlBlockStateOnProperty = false;
	}

	@SuppressWarnings("deprecation")
	public void preProcessUpdate() {
		// Only operate on the server.
		if (getLevel().isClientSide) {
			return;
		}

		// If the inventory is empty, cancel everything and return.
		if (canProcess != null && !canProcess.get() && isTickingToEvent) {
			clearState();
			return;
		}

		// Get the redstone level but skip empty blocks.
		int redstoneLevel = 0;
		for (Direction dir : Direction.values()) {
			if (getLevel().getBlockState(getPos().relative(dir)).isAir()) {
				continue;
			}
			int value = getLevel().getBestNeighborSignal(getPos().relative(dir));
			if (value > redstoneLevel) {
				redstoneLevel = value;
			}
		}

		// If there is a redstone signal, set initiate the event call.
		if (redstoneLevel > 1) {
			if (!lastRedstoneWasHigh) {
				initiateEventCall();
				lastRedstoneWasHigh = true;
			}
		} else {
			lastRedstoneWasHigh = false;
		}

		// If we are processing, clock up to the delay. If the drop timer elapsed, then
		// execute the event.
		if (isTickingToEvent) {
			if (tickTimer < postPulseDelay) {
				tickTimer++;
			} else {
				onPulseEvent.get();
				clearState();
			}
		}
	}

	public RedstonePulseReactorComponent setProcessingGate(Supplier<Boolean> canProcess) {
		this.canProcess = canProcess;
		return this;
	}

	public RedstonePulseReactorComponent shouldControlOnState(boolean shouldControl) {
		shouldControlBlockStateOnProperty = shouldControl;
		return this;
	}

	protected void initiateEventCall() {
		isTickingToEvent = true;
		tickTimer = 0;
		if (shouldControlBlockStateOnProperty) {
			BlockState currentState = getLevel().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.getValue(StaticPowerMachineBlock.IS_ON) != true) {
					getLevel().setBlock(getPos(), currentState.setValue(StaticPowerMachineBlock.IS_ON, true), 2);
				}
			}
		}
	}

	protected void clearState() {
		isTickingToEvent = false;
		tickTimer = 0;
		if (shouldControlBlockStateOnProperty) {
			BlockState currentState = getLevel().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.getValue(StaticPowerMachineBlock.IS_ON) != false) {
					getLevel().setBlock(getPos(), currentState.setValue(StaticPowerMachineBlock.IS_ON, false), 2);
				}
			}
		}
	}
}
