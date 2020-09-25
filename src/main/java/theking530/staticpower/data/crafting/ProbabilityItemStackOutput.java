package theking530.staticpower.data.crafting;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

public class ProbabilityItemStackOutput {
	public static final ProbabilityItemStackOutput EMPTY = new ProbabilityItemStackOutput(ItemStack.EMPTY, 0.0f);

	private final ItemStack item;
	private final float percentChance;

	public ProbabilityItemStackOutput(ItemStack output) {
		this(output, 1.0f);
	}

	public ProbabilityItemStackOutput(ItemStack output, float percentage) {
		item = output;
		percentChance = percentage;
	}

	public boolean isValid() {
		if (item != null) {
			return true;
		}
		return false;
	}

	public ItemStack getItem() {
		return item;
	}

	/**
	 * Gets the output chance between (0.0, 1.0).
	 * 
	 * @return
	 */
	public float getOutputChance() {
		return percentChance;
	}

	public boolean isEmpty() {
		return item.isEmpty() || percentChance == 0.0f;
	}

	/**
	 * Parses a {@link ProbabilityItemStackOutput} from the provided JSON.
	 * 
	 * @param json The JSON to parse from.
	 * @return A newProbabilityItemStackOutput representing the data from the
	 *         provided JSON.
	 */
	public static ProbabilityItemStackOutput parseFromJSON(JsonObject json) {
		// Capture the output item.
		ItemStack output = ShapedRecipe.deserializeItem(json);

		// If the chance value is provided, use it, otherwise assume 100% chance and
		// return.
		if (JSONUtils.hasField(json, "chance")) {
			float percentChance = JSONUtils.getFloat(json, "chance");
			return new ProbabilityItemStackOutput(output, percentChance);
		} else {
			return new ProbabilityItemStackOutput(output);
		}
	}

	public static ProbabilityItemStackOutput readFromBuffer(PacketBuffer buffer) {
		ItemStack localItem = buffer.readItemStack();
		float localPercent = buffer.readFloat();
		return new ProbabilityItemStackOutput(localItem, localPercent);
	}

	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeItemStack(item);
		buffer.writeFloat(percentChance);
	}
}
