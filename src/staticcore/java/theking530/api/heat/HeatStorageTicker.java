package theking530.api.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.heat.IHeatStorage.HeatTransferAction;

public class HeatStorageTicker implements INBTSerializable<CompoundTag> {
	private int metldownTicks;
	private int remainingMeltdownTicks;
	private boolean hasMeltdownBehavior;
	private IHeatStorage storage;

	public HeatStorageTicker(IHeatStorage storage) {
		this(storage, 0);
	}

	public HeatStorageTicker(IHeatStorage storage, int metldownTicks) {
		this.metldownTicks = metldownTicks;
		this.storage = storage;
		this.hasMeltdownBehavior = metldownTicks > 0;
	}

	public void tick(Level world, BlockPos currentPos) {
		// Put us into meltdown mode if this ticker wants that.
		if (hasMeltdownBehavior && storage.getCurrentTemperature() >= storage.getOverheatTemperature()) {
			remainingMeltdownTicks = metldownTicks;
		}

		// Tick down if we're in a meltdown.
		// If we're not recovering from a meltdown, transfer heat.
		if (isRecoveringFromMeltdown()) {
			tickDownDuringMeltdown();
		} else {
			transferHeatWithSurroundings(world, currentPos, HeatTransferAction.EXECUTE);
		}
	}

	public void transferHeatWithSurroundings(Level world, BlockPos currentPos, HeatTransferAction action) {
		HeatUtilities.transferHeat(storage, world, currentPos, action);
	}

	public int getMeltdownRecoveryTicks() {
		return metldownTicks;
	}

	public void setMeltdownRecoveryTicks(int metldownTicks) {
		this.metldownTicks = metldownTicks;
	}

	public int getMeltdownRecoveryTicksRemaining() {
		return remainingMeltdownTicks;
	}

	public boolean isRecoveringFromMeltdown() {
		return getMeltdownRecoveryTicksRemaining() > 0;
	}

	public boolean hasMeltdownBehavior() {
		return hasMeltdownBehavior;
	}

	protected void tickDownDuringMeltdown() {
		remainingMeltdownTicks--;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		metldownTicks = nbt.getInt("metldownTicks");
		remainingMeltdownTicks = nbt.getInt("remainingMeltdownTicks");
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putInt("current_heat", metldownTicks);
		output.putInt("overheat_threshold", remainingMeltdownTicks);
		return output;
	}
}
