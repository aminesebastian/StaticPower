package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticpower.data.MaterialBundle;
import theking530.staticpower.data.MaterialBundle.MaterialType;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class SmeltingRecipeGenerator extends SPRecipeProvider<BlastingRecipe> {

	public SmeltingRecipeGenerator(DataGenerator dataGenerator) {
		super("smelting", dataGenerator);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.getMaterialType() == MaterialType.METAL && bundle.shouldGenerateIngot()) {
				if (bundle.shouldGenerateDust()) {
					addRecipe("dusts/" + bundle.getName(),
							create(Ingredient.of(bundle.getDustTag()), bundle.getIngot().get()).unlockedBy("has_dust", hasItems(bundle.getDustTag())));
				}
				if (bundle.shouldGenerateChunks()) {
					addRecipe("chunks/" + bundle.getName(),
							create(Ingredient.of(bundle.getChunkTag()), bundle.getIngot().get()).unlockedBy("has_chunk", hasItems(bundle.getIngotTag())));
				}
				if (bundle.hasOres()) {
					addRecipe("ores/" + bundle.getName(),
							create(Ingredient.of(bundle.getOreItemTag()), bundle.getIngot().get()).unlockedBy("has_ore", hasItems(bundle.getOreItemTag())));
				}
				if (bundle.shouldGenerateRawMaterial()) {
					addRecipe("raw_material/" + bundle.getName(),
							create(Ingredient.of(bundle.getRawMaterialTag()), bundle.getIngot().get()).unlockedBy("has_raw_material", hasItems(bundle.getRawMaterialTag())));
				}
			}
		}

		addRecipe("dusts/iron", create(Ingredient.of(ModMaterials.IRON.getDustTag()), Items.IRON_INGOT).unlockedBy("has_dust", hasItems(ModMaterials.IRON.getDustTag())));
		addRecipe("dusts/gold", create(Ingredient.of(ModMaterials.GOLD.getDustTag()), Items.GOLD_INGOT).unlockedBy("has_dust", hasItems(ModMaterials.GOLD.getDustTag())));
		addRecipe("dusts/copper", create(Ingredient.of(ModMaterials.COPPER.getDustTag()), Items.COPPER_INGOT).unlockedBy("has_dust", hasItems(ModMaterials.COPPER.getDustTag())));
		addRecipe("dusts/ruby",
				create(Ingredient.of(ModMaterials.RUBY.getDustTag()), ModMaterials.RUBY.getRawMaterial().get()).unlockedBy("has_dust", hasItems(ModMaterials.RUBY.getDustTag())));
		addRecipe("dusts/sapphire", create(Ingredient.of(ModMaterials.SAPPHIRE.getDustTag()), ModMaterials.SAPPHIRE.getRawMaterial().get()).unlockedBy("has_dust",
				hasItems(ModMaterials.SAPPHIRE.getDustTag())));

		addRecipe("chunks/iron", create(Ingredient.of(ModMaterials.IRON.getChunkTag()), Items.IRON_INGOT).unlockedBy("has_chunk", hasItems(ModMaterials.IRON.getChunkTag())));
		addRecipe("chunks/gold", create(Ingredient.of(ModMaterials.GOLD.getChunkTag()), Items.GOLD_INGOT).unlockedBy("has_chunk", hasItems(ModMaterials.GOLD.getChunkTag())));
		addRecipe("chunks/copper",
				create(Ingredient.of(ModMaterials.COPPER.getChunkTag()), Items.COPPER_INGOT).unlockedBy("has_chunk", hasItems(ModMaterials.COPPER.getChunkTag())));
		addRecipe("chunks/ruby", create(Ingredient.of(ModMaterials.RUBY.getChunkTag()), ModMaterials.RUBY.getRawMaterial().get()).unlockedBy("has_chunk",
				hasItems(ModMaterials.RUBY.getChunkTag())));
		addRecipe("chunks/sapphire", create(Ingredient.of(ModMaterials.SAPPHIRE.getChunkTag()), ModMaterials.SAPPHIRE.getRawMaterial().get()).unlockedBy("has_chunk",
				hasItems(ModMaterials.SAPPHIRE.getChunkTag())));

		addRecipe("food/eeef", create(Ingredient.of(ModItems.RawEeef.get()), ModItems.CookedEeef.get()));
		addRecipe("food/smutton", create(Ingredient.of(ModItems.RawSmutton.get()), ModItems.CookedSmutton.get()));
		addRecipe("food/potato_bread", create(Ingredient.of(ModItems.PotatoFlour.get()), ModItems.PotatoBread.get()));
		addRecipe("food/bread", create(Ingredient.of(ModItems.WheatFlour.get()), Items.BREAD));
	}

	protected SimpleCookingRecipeBuilder create(Ingredient input, ItemLike output) {
		return SimpleCookingRecipeBuilder.smelting(input, output, 0.5f, 200);
	}

	protected SimpleCookingRecipeBuilder create(Ingredient input, ItemLike output, float xp, int cookTime) {
		return SimpleCookingRecipeBuilder.smelting(input, output, xp, cookTime);
	}
}
