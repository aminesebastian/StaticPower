package theking530.api.heat;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.heat.IHeatStorage.HeatTransferAction;
import theking530.staticcore.StaticCore;
import theking530.staticcore.utilities.NumericalAggregator;

public class HeatTicker implements INBTSerializable<CompoundTag> {
	protected final IHeatStorage owningStorage;

	private long lastTickTime;

	protected NumericalAggregator heatedAgg;
	protected NumericalAggregator cooledAgg;

	public HeatTicker(IHeatStorage owningStorage) {
		this.owningStorage = owningStorage;
		heatedAgg = new NumericalAggregator(0);
		cooledAgg = new NumericalAggregator(0);
	}

	public void tick(Level level, BlockPos pos, boolean transferHeatWithEnvironment) {
		if (lastTickTime == level.getGameTime()) {
			StaticCore.LOGGER.error("StaticPowerStorageTicker#tick should only be called once per tick!");
			return;
		}
		lastTickTime = level.getGameTime();

		if (transferHeatWithEnvironment) {
			HeatUtilities.transferHeat(level, owningStorage, pos, HeatTransferAction.EXECUTE);
		}

		heatedAgg.tick();
		cooledAgg.tick();
	}

	public void heated(float tempChange, float powerAdded) {
		heatedAgg.addSample(powerAdded);
	}

	public void cooled(float tempChange, float powerRemoved) {
		cooledAgg.addSample(powerRemoved);
	}

	public double getAverageCooledPerTick() {
		// We add 0.0f here to avoid a -0 value getting returned and rendered in the UI.
		return -cooledAgg.getAverageValue() + 0.0f;
	}

	public double getAverageHeatedPerTick() {
		return heatedAgg.getAverageValue();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.put("heat", heatedAgg.serialize());
		output.put("cool", cooledAgg.serialize());

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		heatedAgg.deserialize(nbt.getCompound("heat"));
		cooledAgg.deserialize(nbt.getCompound("cool"));
	}
}