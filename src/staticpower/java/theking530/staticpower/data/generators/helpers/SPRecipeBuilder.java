package theking530.staticpower.data.generators.helpers;

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

public class SPRecipeBuilder<T extends Recipe<?>> implements RecipeBuilder {
	private final T recipe;
	private final Function<T, JsonObject> optionalConverter;

	private SPRecipeBuilder(T recipe, Function<T, JsonObject> optionalConverter) {
		this.recipe = recipe;
		this.optionalConverter = optionalConverter;
	}

	public static <T extends Recipe<?>> SPRecipeBuilder<T> create(T recipe, Function<T, JsonObject> optionalConverter) {
		return new SPRecipeBuilder<T>(recipe, optionalConverter);

	}

	public static <T extends Recipe<?>> SPRecipeBuilder<T> create(T recipe) {
		return new SPRecipeBuilder<T>(recipe, null);
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
		SPFinalizedRecipe<T> finishedRecipe = null;
		if (optionalConverter != null) {
			finishedRecipe = new SPFinalizedRecipe<T>(id, recipe) {
				protected JsonObject convert(T recipe) {
					return optionalConverter.apply(recipe);
				}
			};
		} else {
			finishedRecipe = new SPFinalizedRecipe<T>(id, recipe);
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
