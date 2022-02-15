package theking530.staticpower.data.crafting;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class MachineRecipeProcessingSection {
	protected final int processingTime;
	protected final long powerCost;

	protected MachineRecipeProcessingSection(int processingTime, long powerCost) {
		this.processingTime = processingTime;
		this.powerCost = powerCost;
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

	public static MachineRecipeProcessingSection hardcoded(int defaultTime, long defaultPowerCost) {
		return new MachineRecipeProcessingSection(defaultTime, defaultPowerCost);
	}

	public static MachineRecipeProcessingSection fromJson(int defaultTime, long defaultPowerCost, JsonObject json) {
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
			return new MachineRecipeProcessingSection(time, power);
		}

		return new MachineRecipeProcessingSection(defaultTime, defaultPowerCost);
	}

	public static MachineRecipeProcessingSection fromBuffer(FriendlyByteBuf buf) {
		return new MachineRecipeProcessingSection(buf.readInt(), buf.readLong());
	}

	public void writeToBuffer(FriendlyByteBuf buf) {
		buf.writeInt(processingTime);
		buf.writeLong(powerCost);
	}
}
