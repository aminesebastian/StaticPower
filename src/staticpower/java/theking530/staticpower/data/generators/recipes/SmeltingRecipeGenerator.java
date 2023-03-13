package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.ItemLike;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialBundle.MaterialBundleType;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class SmeltingRecipeGenerator extends SPRecipeProvider<SmeltingRecipe> {

	public SmeltingRecipeGenerator(DataGenerator dataGenerator) {
		super("smelting", dataGenerator);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.getMaterialType() == MaterialBundleType.METAL) {
				if (bundle.hasGeneratedMaterial(MaterialTypes.DUST) && bundle.has(MaterialTypes.INGOT)) {
					addRecipe("dusts/" + bundle.getName(), create(Ingredient.of(bundle.get(MaterialTypes.DUST).getItemTag()), bundle.get(MaterialTypes.INGOT).get())
							.unlockedBy("has_dust", hasItems(bundle.get(MaterialTypes.DUST).getItemTag())));
				}
				if (bundle.hasGeneratedOre()) {
					addRecipe("ores/" + bundle.getName(),
							create(Ingredient.of(bundle.oreItemTag()), bundle.get(MaterialTypes.INGOT).get()).unlockedBy("has_dust", hasItems(bundle.oreItemTag())));
				}
				if (bundle.hasGeneratedMaterial(MaterialTypes.RAW_MATERIAL) && bundle.has(MaterialTypes.INGOT)) {
					addRecipe("raw_material/" + bundle.getName(), create(Ingredient.of(bundle.get(MaterialTypes.RAW_MATERIAL).getItemTag()), bundle.get(MaterialTypes.INGOT).get())
							.unlockedBy("has_raw_material", hasItems(bundle.get(MaterialTypes.RAW_MATERIAL).getItemTag())));
				}
				if (bundle.hasGeneratedMaterial(MaterialTypes.CHUNKS)) {
					addRecipe("chunks/" + bundle.getName(), create(Ingredient.of(bundle.get(MaterialTypes.CHUNKS).getItemTag()), bundle.get(MaterialTypes.INGOT).get())
							.unlockedBy("has_chunks", hasItems(bundle.get(MaterialTypes.CHUNKS).getItemTag())));
				}
			}

			if (bundle.getMaterialType() == MaterialBundleType.GEM && bundle.has(MaterialTypes.RAW_MATERIAL)) {
				if (bundle.hasGeneratedMaterial(MaterialTypes.DUST)) {
					addRecipe("dusts/" + bundle.getName(), create(Ingredient.of(bundle.get(MaterialTypes.DUST).getItemTag()), bundle.get(MaterialTypes.RAW_MATERIAL).get())
							.unlockedBy("has_dust", hasItems(bundle.get(MaterialTypes.DUST).getItemTag())));
				}
				if (bundle.hasGeneratedMaterial(MaterialTypes.CHUNKS)) {
					addRecipe("chunks/" + bundle.getName(), create(Ingredient.of(bundle.get(MaterialTypes.CHUNKS).getItemTag()), bundle.get(MaterialTypes.RAW_MATERIAL).get())
							.unlockedBy("has_chunks", hasItems(bundle.get(MaterialTypes.CHUNKS).getItemTag())));
				}
			}

			if (bundle.getMaterialType() == MaterialBundleType.DUST && bundle.has(MaterialTypes.DUST)) {
				if (bundle.hasGeneratedMaterial(MaterialTypes.CHUNKS)) {
					addRecipe("chunks/" + bundle.getName(), create(Ingredient.of(bundle.get(MaterialTypes.CHUNKS).getItemTag()), bundle.get(MaterialTypes.DUST).get())
							.unlockedBy("has_chunk", hasItems(bundle.get(MaterialTypes.CHUNKS).getItemTag())));
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
