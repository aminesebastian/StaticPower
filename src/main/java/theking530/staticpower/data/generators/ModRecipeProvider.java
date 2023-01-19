package theking530.staticpower.data.generators;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
	private Map<RecipeBuilder, String> builders;

	public ModRecipeProvider(DataGenerator p_125973_) {
		super(p_125973_);
		builders = new HashMap<>();
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		// @formatter:off
		beginShapedRecipe(ModBlocks.BlockBrass.get())
			.define('i', ModItems.IngotBrass.get())
			.pattern("iii")
			.pattern("iii")
			.pattern("iii")
			.unlockedBy("has_brass", inventoryTrigger(ItemPredicate.Builder.item().of(ModItems.IngotBrass.get()).build()));
		// @formatter:on

		completeBuilding(finishedRecipeConsumer);
	}

	protected ShapedRecipeBuilder beginShapedRecipe(ItemLike result) {
		return beginShapedRecipe(result, 1, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected ShapedRecipeBuilder beginShapedRecipe(ItemLike result, int count) {
		return beginShapedRecipe(result, count, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected ShapedRecipeBuilder beginShapedRecipe(ItemLike result, String nameOverride) {
		return beginShapedRecipe(result, 1, nameOverride);

	}

	protected ShapedRecipeBuilder beginShapedRecipe(ItemLike result, int count, String nameOverride) {
		ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(result);
		builders.put(builder, "shaped/" + nameOverride);
		return builder;
	}

	protected ShapelessRecipeBuilder beginShapelessRecipe(ItemLike result) {
		return beginShapelessRecipe(result, 1, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected ShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, int count) {
		return beginShapelessRecipe(result, count, ForgeRegistries.ITEMS.getKey(result.asItem()).getPath());
	}

	protected ShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, String nameOverride) {
		return beginShapelessRecipe(result, 1, nameOverride);
	}

	protected ShapelessRecipeBuilder beginShapelessRecipe(ItemLike result, int count, String nameOverride) {
		ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(result, count);
		builders.put(builder, "shapeless/" + nameOverride);
		return builder;
	}

	private void completeBuilding(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		for (Entry<RecipeBuilder, String> pair : builders.entrySet()) {
			pair.getKey().save(finishedRecipeConsumer, new ResourceLocation(StaticPower.MOD_ID, "crafting/" + pair.getValue()).toString());
		}
	}
}