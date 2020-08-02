package theking530.staticpower.data.crafting;

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
		for(ItemStack stack : ingredient.getMatchingStacks()) {
			stack.setCount(count);
		}
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
		return ingredient.test(stackToTest) && stackToTest.getCount() <= count;
	}

	public static StaticPowerIngredient deserialize(JsonObject json) {
		// Get the input ingredient.
		Ingredient input = Ingredient.deserialize(json);

		// Get the input count.
		int inputCount = 1;
		if (json.has("count")) {
			inputCount = json.get("count").getAsInt();
		}

		// Create the ingredient wrapper..
		return new StaticPowerIngredient(input, inputCount);
	}
}
