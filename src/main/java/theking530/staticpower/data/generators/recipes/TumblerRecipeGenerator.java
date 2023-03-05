package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.tumbler.TumblerRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.NewModMaterials;

public class TumblerRecipeGenerator extends SPRecipeProvider<TumblerRecipe> {

	public TumblerRecipeGenerator(DataGenerator dataGenerator) {
		super("tumbling", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle bundle : NewModMaterials.MATERIALS.values()) {
			if (bundle.hasOre() && bundle.has(MaterialTypes.CHUNKS)) {
				addRecipe("ores/" + bundle.getName(), StaticPowerIngredient.of(bundle.getOreItemTag()),
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
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
