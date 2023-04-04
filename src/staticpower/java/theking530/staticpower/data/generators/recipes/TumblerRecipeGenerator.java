package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModMaterials;

public class TumblerRecipeGenerator extends SCRecipeProvider<TumblerRecipe> {

	public TumblerRecipeGenerator(DataGenerator dataGenerator) {
		super("tumbling", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.hasOre() && bundle.has(MaterialTypes.CHUNKS)) {
				addRecipe("ores/" + bundle.getName(), StaticPowerIngredient.of(bundle.oreItemTag()),
						StaticPowerOutputItem.of(bundle.get(MaterialTypes.CHUNKS).get(), 6, 1, 2, 0.25f));
			}
			if (bundle.has(MaterialTypes.RAW_MATERIAL) && bundle.has(MaterialTypes.CHUNKS)) {
				addRecipe("raw_materials/" + bundle.getName(), StaticPowerIngredient.of(bundle.get(MaterialTypes.RAW_MATERIAL).getItemTag()),
						StaticPowerOutputItem.of(bundle.get(MaterialTypes.CHUNKS).get(), 6, 1, 2, 0.25f));
			}
		}
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output) {
		addRecipe(nameOverride, input, output, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output, MachineRecipeProcessingSection processing) {
		TumblerRecipe recipe = new TumblerRecipe(null, input, output, processing);
		addRecipe(nameOverride, SCRecipeBuilder.create(recipe));
	}
}
