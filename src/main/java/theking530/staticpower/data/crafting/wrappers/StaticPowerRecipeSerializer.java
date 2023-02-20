package theking530.staticpower.data.crafting.wrappers;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class StaticPowerRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
	@Override
	public final T fromJson(ResourceLocation recipeId, JsonObject json) {
		json.addProperty("id", recipeId.toString());
		return parse(recipeId, json);
	}

	public T parse(ResourceLocation recipeId, JsonObject json) {
		if (getCodec() != null) {
			try {
				return getCodec().decode(JsonOps.INSTANCE, json).result().get().getFirst();
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to decode a recipe with id: %1$s.", recipeId.toString()), e);
			}
		}
		return null;
	}

	public Codec<T> getCodec() {
		return null;
	}

	// TODO: Make this abstract.
	public JsonObject toJson(T recipe) {
		if (getCodec() != null) {
			return getCodec().encodeStart(JsonOps.INSTANCE, recipe).result().get().getAsJsonObject();
		}
		return null;
	}
}
