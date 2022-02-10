package theking530.staticpower.data.crafting;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import theking530.staticcore.utilities.SDMath;

public class ProbabilityItemStackOutput {
	public static final ProbabilityItemStackOutput EMPTY = new ProbabilityItemStackOutput(ItemStack.EMPTY, 0.0f, 0,
			0.0f);

	private final ItemStack item;
	private final float percentChance;

	private final int additionalBonus;
	private final float bonusChance;

	public ProbabilityItemStackOutput(ItemStack output) {
		this(output, 1.0f, 0, 0.0f);
	}

	public ProbabilityItemStackOutput(ItemStack output, float percentage, int additionalBonus, float bonusChance) {
		this.item = output;
		this.percentChance = percentage;
		this.additionalBonus = additionalBonus;
		this.bonusChance = bonusChance;
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

	public float getBonusChance() {
		return bonusChance;
	}

	public int getAdditionalBonus() {
		return additionalBonus;
	}

	public boolean isEmpty() {
		return item.isEmpty() || percentChance == 0.0f;
	}

	public ProbabilityItemStackOutput copy() {
		return new ProbabilityItemStackOutput(item.copy(), percentChance, additionalBonus, bonusChance);
	}

	public void setCount(int count) {
		item.setCount(count);
	}

	public ItemStack calculateOutput(double bonusBoost) {
		// Perform the initial dice roll.
		if (percentChance >= 1.0f || SDMath.diceRoll(percentChance + bonusBoost)) {
			// Create an output item.
			ItemStack output = this.item.copy();

			// Check if there's a bonus.
			if (additionalBonus > 0) {
				// If there is, perform the bonus check and add the bonus if it succeeds.
				if (SDMath.diceRoll(bonusChance + bonusBoost)) {
					int additionalCount = SDMath.getRandomIntInRange(1, additionalBonus);
					output.grow(additionalCount);
				}
			}

			// Return the output.
			return output;
		}
		return ItemStack.EMPTY;
	}

	public ItemStack calculateOutput() {
		return calculateOutput(0.0f);
	}

	/**
	 * Parses a {@link ProbabilityItemStackOutput} from the provided JSON.
	 * 
	 * @param json The JSON to parse from.
	 * @return A newProbabilityItemStackOutput representing the data from the
	 *         provided JSON.
	 */
	public static ProbabilityItemStackOutput parseFromJSON(JsonObject json) {
		try {
			// Capture the output item.
			ItemStack output = ShapedRecipe.itemStackFromJson(json);
			float percentChance = 1.0f;
			float additionalBonusChance = 0.0f;
			int additionalBonus = 0;

			// If the chance value is provided, use it.
			if (GsonHelper.isValidNode(json, "chance")) {
				percentChance = GsonHelper.getAsFloat(json, "chance");
			}

			// If the bonus value is provided, use it.
			if (GsonHelper.isValidNode(json, "bonus")) {
				additionalBonus = GsonHelper.getAsJsonObject(json, "bonus").get("count").getAsInt();
				additionalBonusChance = GsonHelper.getAsJsonObject(json, "bonus").get("chance").getAsFloat();
			}

			return new ProbabilityItemStackOutput(output, percentChance, additionalBonus, additionalBonusChance);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"An error occured when attempting to deserialize json object: %1$s to a ProbabilityItemStack.",
					json), e);
		}
	}

	public static ProbabilityItemStackOutput readFromBuffer(FriendlyByteBuf buffer) {
		ItemStack item = buffer.readItem();
		float percent = buffer.readFloat();
		int bonus = buffer.readInt();
		float bonusChance = buffer.readFloat();
		return new ProbabilityItemStackOutput(item, percent, bonus, bonusChance);
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeItem(item);
		buffer.writeFloat(percentChance);
		buffer.writeInt(additionalBonus);
		buffer.writeFloat(bonusChance);
	}
}
