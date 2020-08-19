package theking530.staticpower.data.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class StaticPowerIngredient {
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

	public boolean testWithCount(ItemStack stackToTest) {
		return ingredient.test(stackToTest) && stackToTest.getCount() >= count;
	}

	public static StaticPowerIngredient deserialize(JsonElement json) {
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
}
