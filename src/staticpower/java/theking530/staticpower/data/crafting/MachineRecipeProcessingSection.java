package theking530.staticpower.data.crafting;

import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;

public class MachineRecipeProcessingSection {
	public static final MachineRecipeProcessingSection EMPTY = new MachineRecipeProcessingSection(() -> 0, () -> 0.0, () -> 0, () -> 0);

	public static final Codec<MachineRecipeProcessingSection> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(Codec.INT.optionalFieldOf("processing_time", 0).forGetter(processingSection -> processingSection.getProcessingTime()),
					Codec.DOUBLE.optionalFieldOf("power", 0.0).forGetter(processingSection -> processingSection.getPower()),
					Codec.INT.optionalFieldOf("minimum_heat", 0).forGetter(processingSection -> processingSection.getMinimumHeat()),
					Codec.INT.optionalFieldOf("heat", 0).forGetter(processingSection -> processingSection.getHeat())).apply(instance, (time, power, minHeat, heat) -> {
						return new MachineRecipeProcessingSection(() -> time, () -> power, () -> minHeat, () -> heat);
					}));

	protected final Supplier<Integer> processingTime;
	protected final Supplier<Double> power;
	protected final Supplier<Integer> minimumHeat;
	protected final Supplier<Integer> heat;

	protected MachineRecipeProcessingSection(Supplier<Integer> processingTime, Supplier<Double> power, Supplier<Integer> minimumHeat, Supplier<Integer> heat) {
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

	public int getMinimumHeat() {
		return minimumHeat.get();
	}

	public int getHeat() {
		return heat.get();
	}

	public static MachineRecipeProcessingSection hardcoded(Supplier<Integer> defaultTime, Supplier<Double> defaultPower, Supplier<Integer> minimumHeat, Supplier<Integer> heat) {
		return new MachineRecipeProcessingSection(defaultTime, defaultPower, minimumHeat, heat);
	}

	public static MachineRecipeProcessingSection hardcoded(int defaultTime, double defaultPower, int minimumHeat, int heat) {
		return new MachineRecipeProcessingSection(() -> defaultTime, () -> defaultPower, () -> minimumHeat, () -> heat);
	}

	public static MachineRecipeProcessingSection fromBuffer(FriendlyByteBuf buf) {
		int processingTime = buf.readInt();
		double powerCost = buf.readDouble();
		int minHeat = buf.readInt();
		int heatUse = buf.readInt();
		return new MachineRecipeProcessingSection(() -> processingTime, () -> powerCost, () -> minHeat, () -> heatUse);
	}

	public MachineRecipeProcessingSection copy() {
		return new MachineRecipeProcessingSection(this.processingTime, this.power, this.minimumHeat, this.heat);
	}

	public static MachineRecipeProcessingSection fromJson(JsonElement json) {
		DataResult<Pair<MachineRecipeProcessingSection, JsonElement>> encodedResult = CODEC.decode(JsonOps.INSTANCE, json);
		return encodedResult.result().get().getFirst();
	}

	public void writeToBuffer(FriendlyByteBuf buf) {
		buf.writeInt(processingTime.get());
		buf.writeDouble(power.get());
		buf.writeInt(minimumHeat.get());
		buf.writeInt(heat.get());
	}
}
