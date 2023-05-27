package theking530.staticcore.crafting;

import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;

public class MachineRecipeProcessingSection {
	public static final MachineRecipeProcessingSection EMPTY = new MachineRecipeProcessingSection(() -> 0, () -> 0.0,
			() -> 0.0f, () -> 0.0f);

	public static final Codec<MachineRecipeProcessingSection> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.INT.optionalFieldOf("processing_time", 0)
					.forGetter(processingSection -> processingSection.getProcessingTime()),
					Codec.DOUBLE.optionalFieldOf("power", 0.0)
							.forGetter(processingSection -> processingSection.getPower()),
					Codec.FLOAT.optionalFieldOf("minimum_temperature", 0.0f)
							.forGetter(processingSection -> processingSection.getMinimumHeat()),
					Codec.FLOAT.optionalFieldOf("heat_flux", 0.0f)
							.forGetter(processingSection -> processingSection.getHeat()))
			.apply(instance, (time, power, minHeat, heat) -> {
				return new MachineRecipeProcessingSection(() -> time, () -> power, () -> minHeat, () -> heat);
			}));

	protected final Supplier<Integer> processingTime;
	protected final Supplier<Double> power;
	protected final Supplier<Float> minimumHeat;
	protected final Supplier<Float> heat;

	protected MachineRecipeProcessingSection(Supplier<Integer> processingTime, Supplier<Double> power,
			Supplier<Float> minimumHeat, Supplier<Float> heat) {
		this.processingTime = processingTime;
		this.power = power;
		this.minimumHeat = minimumHeat;
		this.heat = heat;
	}

	/**
	 * Returns the number of ticks it takes to complete this recipe.
	 * 
	 * @return
	 */
	public int getProcessingTime() {
		return processingTime.get();
	}

	/**
	 * Returns the power cost/generation per tick.
	 * 
	 * @return
	 */
	public double getPower() {
		return power.get();
	}

	public float getMinimumHeat() {
		return minimumHeat.get();
	}

	public float getHeat() {
		return heat.get();
	}

	public static MachineRecipeProcessingSection hardcoded(Supplier<Integer> defaultTime, Supplier<Double> defaultPower,
			Supplier<Float> minimumHeat, Supplier<Float> heat) {
		return new MachineRecipeProcessingSection(defaultTime, defaultPower, minimumHeat, heat);
	}

	public static MachineRecipeProcessingSection hardcoded(int defaultTime, double defaultPower, float minimumHeat,
			float heat) {
		return new MachineRecipeProcessingSection(() -> defaultTime, () -> defaultPower, () -> minimumHeat, () -> heat);
	}

	public static MachineRecipeProcessingSection fromBuffer(FriendlyByteBuf buf) {
		int processingTime = buf.readInt();
		double powerCost = buf.readDouble();
		float minHeat = buf.readFloat();
		float heatUse = buf.readFloat();
		return new MachineRecipeProcessingSection(() -> processingTime, () -> powerCost, () -> minHeat, () -> heatUse);
	}

	public MachineRecipeProcessingSection copy() {
		return new MachineRecipeProcessingSection(this.processingTime, this.power, this.minimumHeat, this.heat);
	}

	public static MachineRecipeProcessingSection fromJson(JsonElement json) {
		DataResult<Pair<MachineRecipeProcessingSection, JsonElement>> encodedResult = CODEC.decode(JsonOps.INSTANCE,
				json);
		return encodedResult.result().get().getFirst();
	}

	public void writeToBuffer(FriendlyByteBuf buf) {
		buf.writeInt(processingTime.get());
		buf.writeDouble(power.get());
		buf.writeFloat(minimumHeat.get());
		buf.writeFloat(heat.get());
	}
}
