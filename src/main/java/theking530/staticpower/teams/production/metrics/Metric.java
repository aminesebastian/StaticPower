package theking530.staticpower.teams.production.metrics;

import net.minecraft.nbt.CompoundTag;

public class Metric {
	private final double consumption;
	private final double production;
	private final MetricPeriod period;

	public Metric(double consumption, double production, MetricPeriod period) {
		this.consumption = consumption;
		this.production = production;
		this.period = period;
	}

	public double getMetric(MetricType type) {
		return type == MetricType.CONSUMPTION ? consumption : production;
	}

	public double getConsumption() {
		return consumption;
	}

	public double getProduction() {
		return production;
	}

	public MetricPeriod getPeriod() {
		return period;
	}

	public static Metric deserialize(CompoundTag tag) {
		return new Metric(tag.getFloat("c"), tag.getFloat("p"), MetricPeriod.values()[tag.getByte("t")]);
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putDouble("c", consumption);
		tag.putDouble("p", production);
		tag.putByte("t", (byte) period.ordinal());
		return tag;
	}

	@Override
	public String toString() {
		return "Metric [consumption=" + consumption + ", production=" + production + ", period=" + period + "]";
	}
}
