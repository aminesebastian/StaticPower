package theking530.staticpower.data.generators.helpers;

import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class SPFinalizedRecipe<T extends Recipe<?>> implements FinishedRecipe {
	private final T recipe;
	private final ResourceLocation id;

	public SPFinalizedRecipe(ResourceLocation id, T recipe) {
		this.id = id;
		this.recipe = recipe;
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		StaticPowerRecipeSerializer<T> serializer = (StaticPowerRecipeSerializer<T>) getType();
		if (serializer != null) {
			JsonObject serializedRecipe = serializer.toJson(recipe);
			for (Entry<String, JsonElement> entry : serializedRecipe.entrySet()) {
				json.add(entry.getKey(), entry.getValue());
			}
		} else {
			throw new RuntimeException(String.format(
					"Recipe serializer for recipe: %1$s of type: %2$s does not inheirt from StaticPowerRecipeSerializer. You must override #serializerRecipedata and serialize the recipe manually.",
					recipe.getId(), recipe.getType()));
		}
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecipeSerializer<T> getType() {
		return (RecipeSerializer<T>) recipe.getSerializer();
	}

	@Override
	public JsonObject serializeAdvancement() {
		return null;
	}

	@Override
	public ResourceLocation getAdvancementId() {
		return null;
	}

}
