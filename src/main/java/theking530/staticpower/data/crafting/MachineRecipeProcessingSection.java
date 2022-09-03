package theking530.staticpower.data.crafting;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class MachineRecipeProcessingSection {
	protected final int processingTime;
	protected final long powerCost;
	protected final int minimumHeat;
	protected final int heatUse;

	protected MachineRecipeProcessingSection(int processingTime, long powerCost, int minimumHeat, int heatUse) {
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
		return processingTime;
	}

	/**
	 * Returns the power cost per tick.
	 * 
	 * @return
	 */
	public long getPowerCost() {
		return powerCost;
	}

	public int getMinimumHeat() {
		return minimumHeat;
	}

	public int getHeatUse() {
		return heatUse;
	}

	public static MachineRecipeProcessingSection hardcoded(int defaultTime, long defaultPowerCost, int minimumHeat, int heatUse) {
		return new MachineRecipeProcessingSection(defaultTime, defaultPowerCost, minimumHeat, heatUse);
	}

	public static MachineRecipeProcessingSection fromJson(int defaultTime, long defaultPowerCost, JsonObject json) {
		return fromJson(defaultTime, defaultPowerCost, 0, 0, json);
	}

	public static MachineRecipeProcessingSection fromJson(int defaultTime, long defaultPowerCost, int defaultMinimumHeat, int defaultHeatUse, JsonObject json) {
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			int time = defaultTime;
			if (processingElement.has("power")) {
				time = processingElement.get("time").getAsInt();
			}

			long power = defaultPowerCost;
			if (processingElement.has("power")) {
				power = processingElement.get("power").getAsLong();
			}

			int minimumHeat = defaultMinimumHeat;
			if (processingElement.has("minimum_heat")) {
				time = processingElement.get("minimum_heat").getAsInt();
			}

			int heatUse = defaultHeatUse;
			if (processingElement.has("heat_use")) {
				time = processingElement.get("heat_use").getAsInt();
			}
			return new MachineRecipeProcessingSection(time, power, minimumHeat, heatUse);
		}

		return new MachineRecipeProcessingSection(defaultTime, defaultPowerCost, defaultMinimumHeat, defaultHeatUse);
	}

	public static MachineRecipeProcessingSection fromBuffer(FriendlyByteBuf buf) {
		return new MachineRecipeProcessingSection(buf.readInt(), buf.readLong(), buf.readInt(), buf.readInt());
	}

	public void writeToBuffer(FriendlyByteBuf buf) {
		buf.writeInt(processingTime);
		buf.writeLong(powerCost);
		buf.writeInt(minimumHeat);
		buf.writeInt(heatUse);
	}
}
