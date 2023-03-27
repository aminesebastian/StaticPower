package theking530.staticcore.crafting;

import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticcore.utilities.JsonUtilities;

/**
 * TODO: Better way to handle the "counts". Perhaps a custom JEI renderer.
 * 
 * @author amine
 *
 */
public class StaticPowerIngredient {
	public static final StaticPowerIngredient EMPTY = new StaticPowerIngredient(Ingredient.EMPTY, 0);

	public static final Codec<StaticPowerIngredient> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(JsonUtilities.INGREDIENT_CODEC.fieldOf("ingredient").forGetter(ingredient -> ingredient.getIngredient()),
					Codec.INT.optionalFieldOf("count", 1).forGetter(ingredient -> ingredient.getCount())).apply(instance, StaticPowerIngredient::new));

	private final Ingredient ingredient;
	private final int count;

	protected StaticPowerIngredient(RecipeItem item, int count) {	
		if (item.hasItemTag()) {
			ingredient = Ingredient.of(item.getItemTag());
		} else {
			ingredient = Ingredient.of(item.getItem());
		}
		this.count = count;
	}

	protected StaticPowerIngredient(int count, ItemLike... itemLikes) {
		ingredient = Ingredient.of(itemLikes);
		this.count = count;
	}

	@SafeVarargs
	protected StaticPowerIngredient(int count, TagKey<Item>... tags) {
		ingredient = Ingredient.fromValues(Stream.of(tags).map(tag -> new Ingredient.TagValue(tag)));
		this.count = count;
	}

	protected StaticPowerIngredient(Ingredient ingredient, int count) {
		this.ingredient = ingredient;
		this.count = count;
	}

	protected StaticPowerIngredient(ItemStack stack, int count) {
		this(Ingredient.of(stack), count);
	}

	protected StaticPowerIngredient(ItemLike item, int count) {
		this(Ingredient.of(item), count);
	}

	public static StaticPowerIngredient of(TagKey<Item> item) {
		return new StaticPowerIngredient(RecipeItem.of(item), 1);
	}

	@SafeVarargs
	public static StaticPowerIngredient of(TagKey<Item>... items) {
		return new StaticPowerIngredient(1, items);
	}

	@SafeVarargs
	public static StaticPowerIngredient of(int count, TagKey<Item>... items) {
		return new StaticPowerIngredient(count, items);
	}

	public static StaticPowerIngredient of(TagKey<Item> item, int count) {
		return new StaticPowerIngredient(RecipeItem.of(item), count);
	}

	public static StaticPowerIngredient of(RecipeItem item) {
		return new StaticPowerIngredient(item, 1);
	}

	public static StaticPowerIngredient of(RecipeItem item, int count) {
		return new StaticPowerIngredient(item, count);
	}

	public static StaticPowerIngredient of(Ingredient ingredient) {
		return new StaticPowerIngredient(ingredient, 1);
	}

	public static StaticPowerIngredient of(Ingredient ingredient, int count) {
		return new StaticPowerIngredient(ingredient, count);
	}

	public static StaticPowerIngredient of(ItemStack stack) {
		return new StaticPowerIngredient(stack, stack.getCount());
	}

	public static StaticPowerIngredient of(ItemStack stack, int count) {
		return new StaticPowerIngredient(stack, count);
	}

	public static StaticPowerIngredient of(ItemLike item) {
		return new StaticPowerIngredient(item, 1);
	}

	public static StaticPowerIngredient of(ItemLike item, int count) {
		return new StaticPowerIngredient(item, count);
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
		for (ItemStack stack : ingredient.getItems()) {
			stack.setCount(count);
		}
		return ingredient.test(stackToTest) && stackToTest.getCount() >= count;
	}

	public JsonElement toJson() {
		JsonElement output = ingredient.toJson();
		if (output instanceof JsonObject) {
			((JsonObject) output).addProperty("count", count);
		}
		return output;
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

	public static StaticPowerIngredient fromJson(JsonElement json) {
		DataResult<Pair<StaticPowerIngredient, JsonElement>> encodedResult = StaticPowerIngredient.CODEC.decode(JsonOps.INSTANCE, json);
		return encodedResult.result().get().getFirst();
	}

	public void writeToBuffer(FriendlyByteBuf buffer) {
		ingredient.toNetwork(buffer);
		buffer.writeInt(count);
	}

	public StaticPowerIngredient copy() {
		return new StaticPowerIngredient(ingredient, count);
	}

	public StaticPowerIngredient copy(int count) {
		return new StaticPowerIngredient(ingredient, count);
	}

	public static StaticPowerIngredient readFromBuffer(FriendlyByteBuf buffer) {
		Ingredient ingredient = Ingredient.fromNetwork(buffer);
		int count = buffer.readInt();
		return new StaticPowerIngredient(ingredient, count);
	}
}
