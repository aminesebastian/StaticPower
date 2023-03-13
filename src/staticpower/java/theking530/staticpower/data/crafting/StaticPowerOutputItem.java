package theking530.staticpower.data.crafting;

import java.util.Objects;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class StaticPowerOutputItem {
	public static final StaticPowerOutputItem EMPTY = StaticPowerOutputItem.of(ItemStack.EMPTY, 0.0f, 0, 0.0f);

	public static final Codec<StaticPowerOutputItem> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(JsonUtilities.ITEMSTACK_CODEC.fieldOf("item").forGetter(ingredient -> ingredient.getItemStack()),
					Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(ingredient -> ingredient.getOutputChance()),
					Codec.INT.optionalFieldOf("additionalBonus", 0).forGetter(ingredient -> ingredient.getAdditionalBonus()),
					Codec.FLOAT.optionalFieldOf("bonusChance", 0.0f).forGetter(ingredient -> ingredient.getBonusChance())).apply(instance, StaticPowerOutputItem::new));

	private final ItemStack item;
	private final float percentChance;

	private final int additionalBonus;
	private final float bonusChance;

	protected StaticPowerOutputItem(ItemLike output, int count, float percentage, int additionalBonus, float bonusChance) {
		this(new ItemStack(output, count), percentage, additionalBonus, bonusChance);
	}

	protected StaticPowerOutputItem(ItemStack output, float percentage, int additionalBonus, float bonusChance) {
		this.item = output;
		this.percentChance = percentage;
		this.additionalBonus = additionalBonus;
		this.bonusChance = bonusChance;
	}

	public static StaticPowerOutputItem of(ItemLike output) {
		return new StaticPowerOutputItem(output, 1, 1, 0, 0);
	}

	public static StaticPowerOutputItem of(ItemLike output, int count) {
		return new StaticPowerOutputItem(output, count, 1, 0, 0);
	}

	public static StaticPowerOutputItem of(ItemLike output, int count, float percentage) {
		return new StaticPowerOutputItem(output, count, percentage, 0, 0);
	}

	public static StaticPowerOutputItem of(ItemLike output, int count, float percentage, int additionalBonus, float bonusChance) {
		return new StaticPowerOutputItem(output, count, percentage, additionalBonus, bonusChance);
	}

	public static StaticPowerOutputItem of(ItemStack output) {
		return new StaticPowerOutputItem(output, 1, 0, 0);
	}

	public static StaticPowerOutputItem of(ItemStack output, float percentage) {
		return new StaticPowerOutputItem(output, percentage, 0, 0);
	}

	public static StaticPowerOutputItem of(ItemStack output, float percentage, int additionalBonus, float bonusChance) {
		return new StaticPowerOutputItem(output, percentage, additionalBonus, bonusChance);
	}

	public boolean isValid() {
		if (item != null) {
			return true;
		}
		return false;
	}

	public ItemStack getItemStack() {
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

	public StaticPowerOutputItem copy() {
		return StaticPowerOutputItem.of(item.copy(), percentChance, additionalBonus, bonusChance);
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
	 * Parses a {@link StaticPowerOutputItem} from the provided JSON.
	 * 
	 * @param json The JSON to parse from.
	 * @return A newProbabilityItemStackOutput representing the data from the
	 *         provided JSON.
	 */
	public static StaticPowerOutputItem parseFromJSON(JsonObject json) {
		try {
			// Capture the output item.
			ItemStack output = JsonUtilities.itemStackFromJson(json.get("item"));
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

			return StaticPowerOutputItem.of(output, percentChance, additionalBonus, additionalBonusChance);
		} catch (Exception e) {
			throw new RuntimeException(String.format("An error occured when attempting to deserialize json object: %1$s to a ProbabilityItemStack.", json), e);
		}
	}

	public JsonObject toJson() {
		JsonObject output = new JsonObject();
		output.add("item", JsonUtilities.itemStackToJson(getItemStack()));

		if (getOutputChance() < 1.0f) {
			output.addProperty("chance", this.getOutputChance());
		}

		if (getAdditionalBonus() > 0) {
			JsonObject bonus = new JsonObject();
			bonus.addProperty("chance", getBonusChance());
			bonus.addProperty("count", getAdditionalBonus());
			output.add("bonus", bonus);
		}

		return output;
	}

	public static StaticPowerOutputItem readFromBuffer(FriendlyByteBuf buffer) {
		ItemStack item = buffer.readItem();
		float percent = buffer.readFloat();
		int bonus = buffer.readInt();
		float bonusChance = buffer.readFloat();
		return StaticPowerOutputItem.of(item, percent, bonus, bonusChance);
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeItem(item);
		buffer.writeFloat(percentChance);
		buffer.writeInt(additionalBonus);
		buffer.writeFloat(bonusChance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(additionalBonus, bonusChance, item, percentChance);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StaticPowerOutputItem other = (StaticPowerOutputItem) obj;
		return additionalBonus == other.additionalBonus && Float.floatToIntBits(bonusChance) == Float.floatToIntBits(other.bonusChance)
				&& ItemUtilities.areItemStacksExactlyEqual(item, other.item) && Float.floatToIntBits(percentChance) == Float.floatToIntBits(other.percentChance);
	}

}
