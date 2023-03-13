package theking530.staticpower.data.generators.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.generators.RecipeItem;

public abstract class SPRecipeProvider<T extends Recipe<?>> extends RecipeProvider implements IConditionBuilder {
	private Map<String, RecipeBuilder> builders;
	private final String folder;

	public SPRecipeProvider(String folder, DataGenerator dataGenerator) {
		super(dataGenerator);
		this.folder = folder;
		builders = new HashMap<>();
	}

	@Override
	protected final void buildCraftingRecipes(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		buildRecipes();
		completeBuilding(finishedRecipeConsumer);
	}

	protected abstract void buildRecipes();

	protected void addRecipe(String name, RecipeBuilder builder) {
		addRecipe(name, builder, false);
	}

	protected void addRecipe(String name, RecipeBuilder builder, boolean replace) {
		if (!replace && builders.containsKey(name)) {
			throw new RuntimeException("Encountered duplicate recipe name: " + name);
		}
		builders.put(name, builder);
	}

	protected void completeBuilding(Consumer<FinishedRecipe> finishedRecipeConsumer) {
		for (Entry<String, RecipeBuilder> pair : builders.entrySet()) {
			try {
				pair.getValue().save(finishedRecipeConsumer, new ResourceLocation(StaticPower.MOD_ID, folder + "/" + pair.getKey()));
			} catch (Exception e) {
				throw new RuntimeException(String.format("An error occured when attempting to save recipe: %1$s.", pair.getKey()), e);
			}
		}
	}

	protected TriggerInstance hasItems(ItemLike... items) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(items).build());
	}

	protected TriggerInstance hasItems(RecipeItem... items) {
		ItemPredicate.Builder builder = ItemPredicate.Builder.item();
		for (RecipeItem item : items) {
			if (item.hasItemTag()) {
				builder.of(item.getItemTag());
			} else {
				builder.of(item.getItem());
			}
		}
		return inventoryTrigger(builder.build());
	}

	@SuppressWarnings("unchecked")
	protected TriggerInstance hasItems(TagKey<Item>... items) {
		ItemPredicate.Builder builder = ItemPredicate.Builder.item();
		for (TagKey<Item> tag : items) {
			builder.of(tag);
		}
		return inventoryTrigger(builder.build());
	}
}
