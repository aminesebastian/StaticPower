package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialBundle.MaterialType;
import theking530.staticpower.init.ModBlocks;
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
			if (bundle.getMaterialType() == MaterialType.METAL && bundle.hasIngot()) {
				if (bundle.hasDust()) {
					addRecipe("dusts/" + bundle.getName(),
							create(Ingredient.of(bundle.getDustTag()), bundle.getIngot().get()).unlockedBy("has_dust", hasItems(bundle.getDustTag())));
				}
				if (bundle.hasOres()) {
					addRecipe("ores/" + bundle.getName(),
							create(Ingredient.of(bundle.getOreItemTag()), bundle.getIngot().get()).unlockedBy("has_ore", hasItems(bundle.getOreItemTag())));
				}
				if (bundle.hasRawMaterial()) {
					addRecipe("raw_material/" + bundle.getName(),
							create(Ingredient.of(bundle.getRawMaterialTag()), bundle.getIngot().get()).unlockedBy("has_raw_material", hasItems(bundle.getRawMaterialTag())));
				}
			}
			if (bundle.getMaterialType() == MaterialType.GEM && bundle.hasRawMaterial()) {
				if (bundle.hasDust()) {
					addRecipe("dusts/" + bundle.getName(),
							create(Ingredient.of(bundle.getDustTag()), bundle.getRawMaterial().get()).unlockedBy("has_dust", hasItems(bundle.getDustTag())));
				}
				if (bundle.hasChunks()) {
					addRecipe("chunks/" + bundle.getName(),
							create(Ingredient.of(bundle.getChunkTag()), bundle.getRawMaterial().get()).unlockedBy("has_chunk", hasItems(bundle.getChunkTag())));
				}
			}

			if (bundle.getMaterialType() == MaterialType.DUST && bundle.hasDust()) {
				if (bundle.hasChunks()) {
					addRecipe("chunks/" + bundle.getName(),
							create(Ingredient.of(bundle.getChunkTag()), bundle.getDust().get()).unlockedBy("has_chunk", hasItems(bundle.getChunkTag())));
				}
			}
		}

		addRecipe("ores/rusty_iron",
				create(Ingredient.of(ModBlocks.OreRustyIron.get()), ModItems.RustyIronScrap.get(), 0.1f, 100).unlockedBy("has_item", hasItems(ModBlocks.OreRustyIron.get())));
		addRecipe("raw_material/rusty_iron",
				create(Ingredient.of(ModItems.RawRustyIron.get()), ModItems.RustyIronScrap.get(), 0.1f, 100).unlockedBy("has_item", hasItems(ModItems.RawRustyIron.get())));

		addRecipe("rusty_iron_scrap",
				create(Ingredient.of(ModItems.RustyIronScrap.get()), Items.IRON_NUGGET, 0.1f, 100).unlockedBy("has_item", hasItems(ModItems.RustyIronScrap.get())));
		addRecipe("raw_silicon", create(Ingredient.of(ModItems.RawSilicon.get()), ModItems.Silicon.get()).unlockedBy("has_item", hasItems(ModItems.RawSilicon.get())));

		addRecipe("food/eeef", create(Ingredient.of(ModItems.RawEeef.get()), ModItems.CookedEeef.get()).unlockedBy("has_item", hasItems(ModItems.RawEeef.get())));
		addRecipe("food/smutton", create(Ingredient.of(ModItems.RawSmutton.get()), ModItems.CookedSmutton.get()).unlockedBy("has_item", hasItems(ModItems.RawSmutton.get())));
		addRecipe("food/potato_bread", create(Ingredient.of(ModItems.PotatoFlour.get()), ModItems.PotatoBread.get()).unlockedBy("has_item", hasItems(ModItems.PotatoFlour.get())));
		addRecipe("food/bread", create(Ingredient.of(ModItems.WheatFlour.get()), Items.BREAD).unlockedBy("has_item", hasItems(ModItems.WheatFlour.get())));
	}

	protected SimpleCookingRecipeBuilder create(Ingredient input, ItemLike output) {
		return SimpleCookingRecipeBuilder.smelting(input, output, 0.5f, 200);
	}

	protected SimpleCookingRecipeBuilder create(Ingredient input, ItemLike output, float xp, int cookTime) {
		return SimpleCookingRecipeBuilder.smelting(input, output, xp, cookTime);
	}
}
