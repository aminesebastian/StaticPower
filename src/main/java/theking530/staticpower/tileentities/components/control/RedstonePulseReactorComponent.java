package theking530.staticpower.tileentities.components.control;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;

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
		if (getWorld().isRemote) {
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
			if (getWorld().getBlockState(getPos().offset(dir)).isAir()) {
				continue;
			}
			int value = getWorld().getRedstonePowerFromNeighbors(getPos().offset(dir));
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
			BlockState currentState = getWorld().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.get(StaticPowerMachineBlock.IS_ON) != true) {
					getWorld().setBlockState(getPos(), currentState.with(StaticPowerMachineBlock.IS_ON, true), 2);
				}
			}
		}
	}

	protected void clearState() {
		isTickingToEvent = false;
		tickTimer = 0;
		if (shouldControlBlockStateOnProperty) {
			BlockState currentState = getWorld().getBlockState(getPos());
			if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
				if (currentState.get(StaticPowerMachineBlock.IS_ON) != false) {
					getWorld().setBlockState(getPos(), currentState.with(StaticPowerMachineBlock.IS_ON, false), 2);
				}
			}
		}
	}
}
