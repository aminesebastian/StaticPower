package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import theking530.staticcore.crafting.RecipeItem;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModMaterials;

public class StoneCutterRecipeGenerator extends SCRecipeProvider<StonecutterRecipe> {

	public StoneCutterRecipeGenerator(DataGenerator dataGenerator) {
		super("stonecutting", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.has(MaterialTypes.CUT_STORAGE_BLOCK) && bundle.has(MaterialTypes.STORAGE_BLOCK)) {
				addRecipe("metals/cut_" + bundle.getName(),
						SingleItemRecipeBuilder
								.stonecutting(Ingredient.of(bundle.get(MaterialTypes.STORAGE_BLOCK).getItemTag()),
										bundle.get(MaterialTypes.CUT_STORAGE_BLOCK).get().asItem(), 4)
								.unlockedBy("has_metal",
										hasItems(RecipeItem.of(bundle.get(MaterialTypes.STORAGE_BLOCK).getItemTag()))));
			}
		}
	}
}
