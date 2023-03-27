package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModMaterials;

public class BlastingRecipeGenerator extends SPRecipeProvider<BlastingRecipe> {

	public BlastingRecipeGenerator(DataGenerator dataGenerator) {
		super("blasting", dataGenerator);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.has(MaterialTypes.HEATED_INGOT)) {
				addRecipe("heating/ingot_heated_" + bundle.getName(),
						SimpleCookingRecipeBuilder.blasting(Ingredient.of(bundle.get(MaterialTypes.INGOT).getItemTag()), bundle.get(MaterialTypes.HEATED_INGOT).get(), 0.5f, 100)
								.unlockedBy("has_ingot", hasItems(bundle.get(MaterialTypes.INGOT).getItemTag())));
			}
		}
	}
}
