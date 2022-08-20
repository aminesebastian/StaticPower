package theking530.staticpower.data.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * TODO: Better way to handle the "counts". Perhaps a custom JEI renderer.
 * @author amine
 *
 */
public class StaticPowerIngredient {
	public static final StaticPowerIngredient EMPTY = new StaticPowerIngredient(Ingredient.EMPTY, 0);
	private final Ingredient ingredient;
	private final int count;

	public StaticPowerIngredient(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

	public StaticPowerIngredient(ItemStack stack, int count) {
		this(Ingredient.of(stack), count);
	}

	public StaticPowerIngredient(ItemStack stack) {
		this(Ingredient.of(stack), stack.getCount());
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
		for (ItemStack stack : ingredient.getItems()) {
			stack.setCount(count);
		}
		return ingredient.test(stackToTest);
	}

	public boolean test(ItemStack stackToTest, boolean verifyCounts) {
		for (ItemStack stack : ingredient.getItems()) {
			stack.setCount(count);
		}
		if (verifyCounts) {
			return testWithCount(stackToTest);
		} else {
			return test(stackToTest);
		}
	}

	public boolean testWithCount(ItemStack stackToTest) {
		for (ItemStack stack : ingredient.getItems()) {
			stack.setCount(count);
		}
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
		Ingredient input = Ingredient.fromJson(json);

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

	public void write(FriendlyByteBuf buffer) {
		ingredient.toNetwork(buffer);
		buffer.writeInt(count);
	}

	public static StaticPowerIngredient read(FriendlyByteBuf buffer) {
		Ingredient ingredient = Ingredient.fromNetwork(buffer);
		int count = buffer.readInt();
		return new StaticPowerIngredient(ingredient, count);
	}
}
