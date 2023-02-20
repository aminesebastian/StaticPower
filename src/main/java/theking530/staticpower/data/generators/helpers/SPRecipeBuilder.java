package theking530.staticpower.data.generators.helpers;

import java.util.function.Consumer;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;

public class SPRecipeBuilder<T extends Recipe<?>> implements RecipeBuilder {
	private final T recipe;

	private SPRecipeBuilder(T recipe) {
		this.recipe = recipe;
	}

	public static <T extends Recipe<?>> SPRecipeBuilder<T> create(T recipe) {
		return new SPRecipeBuilder<T>(recipe);
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
		SPFinalizedRecipe<T> finishedRecipe = new SPFinalizedRecipe<T>(id, recipe);
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
