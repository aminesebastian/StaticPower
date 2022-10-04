package theking530.staticpower.teams.production.metrics;

import net.minecraft.nbt.CompoundTag;

public class Metric {
	private final float input;
	private final float output;
	private final MetricPeriod period;

	public Metric(float input, float output, MetricPeriod period) {
		this.input = input;
		this.output = output;
		this.period = period;
	}

	public float getInput() {
		return input;
	}

	public float getOutput() {
		return output;
	}

	public MetricPeriod getPeriod() {
		return period;
	}

	public static Metric deserialize(CompoundTag tag) {
		return new Metric(tag.getFloat("i"), tag.getFloat("o"), MetricPeriod.values()[tag.getByte("p")]);
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("i", input);
		tag.putFloat("o", output);
		tag.putByte("p", (byte) period.ordinal());
		return tag;
	}
}
