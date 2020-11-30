package theking530.staticpower.data.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;

public class StaticPowerIngredient {
	public static final StaticPowerIngredient EMPTY = new StaticPowerIngredient(Ingredient.EMPTY, 0);
	private final Ingredient ingredient;
	private final int count;

	protected StaticPowerIngredient(Ingredient ingredient, int count) {
		super();
		this.ingredient = ingredient;
		this.count = count;
		for (ItemStack stack : ingredient.getMatchingStacks()) {
			stack.setCount(count);
		}
	}

	public boolean isEmpty() {
		return ingredient == Ingredient.EMPTY || count == 0;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public int getCount() {
		return count;
	}

	public boolean test(ItemStack stackToTest) {
		return ingredient.test(stackToTest);
	}

	public boolean test(ItemStack stackToTest, boolean verifyCounts) {
		if (verifyCounts) {
			return testWithCount(stackToTest);
		} else {
			return test(stackToTest);
		}
	}

	public boolean testWithCount(ItemStack stackToTest) {
		return ingredient.test(stackToTest) && stackToTest.getCount() >= count;
	}

	public static StaticPowerIngredient deserialize(JsonElement json) {
		// Handle empty ingredients.
		if (json.isJsonObject()) {
			JsonObject object = json.getAsJsonObject();
			if (object.size() == 0) {
				return StaticPowerIngredient.EMPTY;
			}
		}

		// Get the input ingredient.
		Ingredient input = Ingredient.deserialize(json);

		// Get the input count.
		int inputCount = 1;
		if (json instanceof JsonObject) {
			JsonObject jsonObject = (JsonObject) json;
			if (jsonObject.has("count")) {
				inputCount = jsonObject.get("count").getAsInt();
			}
		}

		// Create the ingredient wrapper..
		return new StaticPowerIngredient(input, inputCount);
	}

	public void write(PacketBuffer buffer) {
		ingredient.write(buffer);
		buffer.writeInt(count);
	}

	public static StaticPowerIngredient read(PacketBuffer buffer) {
		Ingredient ingredient = Ingredient.read(buffer);
		int count = buffer.readInt();
		return new StaticPowerIngredient(ingredient, count);
	}
}
