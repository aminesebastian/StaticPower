package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class SolderingRecipeSerializer extends StaticPowerRecipeSerializer<SolderingRecipe> {

	@Override
	public Codec<SolderingRecipe> getCodec() {
		return null;
	}

	@Override
	public SolderingRecipe parse(ResourceLocation recipeId, JsonObject json) {
		Map<Character, Ingredient> map = SolderingRecipe.deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
		String[] pattern = SolderingRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));
		StaticPowerOutputItem output = StaticPowerOutputItem.parseFromJSON(GsonHelper.getAsJsonObject(json, "result"));

		Optional<Ingredient> solderingIron = Optional.empty();
		if (json.has("soldering_iron")) {
			solderingIron = Optional.of(Ingredient.fromJson(json.get("soldering_iron")));
		}
		return new SolderingRecipe(recipeId, pattern, map, solderingIron, output);
	}

	@Override
	public SolderingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		byte recipeHeight = buffer.readByte();
		String[] pattern = new String[recipeHeight];
		for (int i = 0; i < recipeHeight; i++) {
			pattern[i] = buffer.readUtf();
		}

		Map<Character, Ingredient> ingredientMap = new HashMap<>();
		byte ingredientCount = buffer.readByte();
		for (int i = 0; i < ingredientCount; i++) {
			ingredientMap.put(buffer.readChar(), Ingredient.fromNetwork(buffer));
		}

		Ingredient solderingIron = Ingredient.fromNetwork(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		return new SolderingRecipe(recipeId, pattern, ingredientMap, Optional.of(solderingIron), output);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SolderingRecipe recipe) {
		buffer.writeByte(recipe.getRecipeHeight());
		for (int i = 0; i < recipe.getRecipeHeight(); i++) {
			buffer.writeUtf(recipe.getPattern()[i]);
		}

		buffer.writeByte(recipe.getIngredientMap().size());
		for (Entry<Character, Ingredient> ing : recipe.getIngredientMap().entrySet()) {
			buffer.writeChar(ing.getKey());
			ing.getValue().toNetwork(buffer);
		}

		recipe.getSolderingIron().toNetwork(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}