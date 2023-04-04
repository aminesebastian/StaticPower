package theking530.staticcore.data.generators.helpers;

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

public class SCRecipeBuilder<T extends Recipe<?>> implements RecipeBuilder {
	private final T recipe;
	private final Function<T, JsonObject> optionalConverter;

	private SCRecipeBuilder(T recipe, Function<T, JsonObject> optionalConverter) {
		this.recipe = recipe;
		this.optionalConverter = optionalConverter;
	}

	public static <T extends Recipe<?>> SCRecipeBuilder<T> create(T recipe, Function<T, JsonObject> optionalConverter) {
		return new SCRecipeBuilder<T>(recipe, optionalConverter);

	}

	public static <T extends Recipe<?>> SCRecipeBuilder<T> create(T recipe) {
		return new SCRecipeBuilder<T>(recipe, null);
	}

	@Override
	public RecipeBuilder unlockedBy(String p_176496_, CriterionTriggerInstance p_176497_) {
		return null;
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, String name) {
		save(consumer, new ResourceLocation(name));
	}

	@Override
	public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
		SCFinalizedRecipe<T> finishedRecipe = null;
		if (optionalConverter != null) {
			finishedRecipe = new SCFinalizedRecipe<T>(id, recipe) {
				protected JsonObject convert(T recipe) {
					return optionalConverter.apply(recipe);
				}
			};
		} else {
			finishedRecipe = new SCFinalizedRecipe<T>(id, recipe);
		}
		consumer.accept(finishedRecipe);
	}

	@Override
	public RecipeBuilder group(String p_176495_) {
		return null;
	}

	@Override
	public Item getResult() {
		return null;
	}
}
