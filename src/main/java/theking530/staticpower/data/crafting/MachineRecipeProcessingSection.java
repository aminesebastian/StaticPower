package theking530.staticpower.data.crafting;

import java.util.function.Supplier;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class MachineRecipeProcessingSection {
	public static final MachineRecipeProcessingSection EMPTY = new MachineRecipeProcessingSection(() -> 0, () -> 0.0, () -> 0, () -> 0);

	public static final Codec<MachineRecipeProcessingSection> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(Codec.INT.optionalFieldOf("processing_time", 0).forGetter(processingSection -> processingSection.getProcessingTime()),
					Codec.DOUBLE.optionalFieldOf("power_cost", 0.0).forGetter(processingSection -> processingSection.getPowerCost()),
					Codec.INT.optionalFieldOf("minimum_heat", 0).forGetter(processingSection -> processingSection.getMinimumHeat()),
					Codec.INT.optionalFieldOf("heat_use", 0).forGetter(processingSection -> processingSection.getHeatUse()))
			.apply(instance, (time, powerCode, minHeat, heatUse) -> {
				return new MachineRecipeProcessingSection(() -> time, () -> powerCode, () -> minHeat, () -> heatUse);
			}));

	protected final Supplier<Integer> processingTime;
	protected final Supplier<Double> powerCost;
	protected final Supplier<Integer> minimumHeat;
	protected final Supplier<Integer> heatUse;

	protected MachineRecipeProcessingSection(Supplier<Integer> processingTime, Supplier<Double> powerCost, Supplier<Integer> minimumHeat, Supplier<Integer> heatUse) {
		this.processingTime = processingTime;
		this.powerCost = powerCost;
		this.minimumHeat = minimumHeat;
		this.heatUse = heatUse;
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
	 * Returns the power cost per tick.
	 * 
	 * @return
	 */
	public double getPowerCost() {
		return powerCost.get();
	}

	public int getMinimumHeat() {
		return minimumHeat.get();
	}

	public int getHeatUse() {
		return heatUse.get();
	}

	public static MachineRecipeProcessingSection hardcoded(Supplier<Integer> defaultTime, Supplier<Double> defaultPowerCost, Supplier<Integer> minimumHeat,
			Supplier<Integer> heatUse) {

		return new MachineRecipeProcessingSection(defaultTime, defaultPowerCost, minimumHeat, heatUse);
	}

	public static MachineRecipeProcessingSection fromJson(Supplier<Integer> defaultTime, JsonObject json) {
		return fromJson(defaultTime, () -> 0.0, () -> 0, () -> 0, json);

	}

	public static MachineRecipeProcessingSection fromJson(Supplier<Integer> defaultTime, Supplier<Double> defaultPowerCost, JsonObject json) {
		return fromJson(defaultTime, defaultPowerCost, () -> 0, () -> 0, json);
	}

	public JsonObject toJson() {
		JsonObject output = new JsonObject();

		if (getPowerCost() >= 0) {
			output.addProperty("time", getProcessingTime());
		}
		if (getPowerCost() >= 0) {
			output.addProperty("power", getPowerCost());
		}
		if (getPowerCost() >= 0) {
			output.addProperty("minimum_heat", getMinimumHeat());
		}
		if (getPowerCost() >= 0) {
			output.addProperty("heat_use", getHeatUse());
		}

		return output;
	}

	public static MachineRecipeProcessingSection fromJson(Supplier<Integer> defaultTime, Supplier<Double> defaultPowerCost, Supplier<Integer> defaultMinimumHeat,
			Supplier<Integer> defaultHeatUse, JsonObject json) {
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			Supplier<Integer> time = defaultTime;
			if (processingElement.has("time")) {
				time = () -> processingElement.get("time").getAsInt();
			}

			Supplier<Double> power = defaultPowerCost;
			if (processingElement.has("power")) {
				power = () -> processingElement.get("power").getAsDouble();
			}

			Supplier<Integer> minimumHeat = defaultMinimumHeat;
			if (processingElement.has("minimum_heat")) {
				time = () -> processingElement.get("minimum_heat").getAsInt();
			}

			Supplier<Integer> heatUse = defaultHeatUse;
			if (processingElement.has("heat_use")) {
				time = () -> processingElement.get("heat_use").getAsInt();
			}
			return new MachineRecipeProcessingSection(time, power, minimumHeat, heatUse);
		}

		return new MachineRecipeProcessingSection(defaultTime, defaultPowerCost, defaultMinimumHeat, defaultHeatUse);
	}

	public static MachineRecipeProcessingSection fromBuffer(FriendlyByteBuf buf) {
		int processingTime = buf.readInt();
		double powerCost = buf.readDouble();
		int minHeat = buf.readInt();
		int heatUse = buf.readInt();
		return new MachineRecipeProcessingSection(() -> processingTime, () -> powerCost, () -> minHeat, () -> heatUse);
	}

	public void writeToBuffer(FriendlyByteBuf buf) {
		buf.writeInt(processingTime.get());
		buf.writeDouble(powerCost.get());
		buf.writeInt(minimumHeat.get());
		buf.writeInt(heatUse.get());
	}
}
