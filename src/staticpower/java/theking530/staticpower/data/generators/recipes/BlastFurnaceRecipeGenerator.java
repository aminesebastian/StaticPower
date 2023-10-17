package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.crafting.wrappers.blastfurnace.BlastFurnaceRecipe;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class BlastFurnaceRecipeGenerator extends SCRecipeProvider<AlloyFurnaceRecipe> {

	public BlastFurnaceRecipeGenerator(DataGenerator dataGenerator) {
		super("blast_furnacing", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("raw_steel", StaticPowerIngredient.of(ModMaterials.IRON.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerOutputItem.of(ModMaterials.STEEL.get(MaterialTypes.RAW_MATERIAL).get()),
				StaticPowerOutputItem.of(ModItems.Slag.get()), 1, 60 * 20);

		addRecipe("raw_steel_block",
				StaticPowerIngredient.of(ModMaterials.IRON.get(MaterialTypes.RAW_STOARGE_BLOCK).get()),
				StaticPowerOutputItem.of(ModMaterials.STEEL.get(MaterialTypes.RAW_STOARGE_BLOCK).get()),
				StaticPowerOutputItem.of(ModItems.Slag.get()), 9, 60 * 20 * 9);

		for (MaterialBundle bundle : ModMaterials.MATERIALS.values()) {
			if (bundle.has(MaterialTypes.HEATED_INGOT)) {
				addRecipe("heating/ingot_heated_" + bundle.getName(),
						StaticPowerIngredient.of(bundle.get(MaterialTypes.INGOT).getItemTag()),
						StaticPowerOutputItem.of(bundle.get(MaterialTypes.HEATED_INGOT).get()),
						StaticPowerOutputItem.EMPTY, 0.5f, 20 * 5);
			}
		}
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			StaticPowerOutputItem slagOutput, float experience, int processingTime) {
		addRecipe(nameOverride, input, output, slagOutput, experience,
				MachineRecipeProcessingSection.hardcoded(processingTime, 0, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, StaticPowerOutputItem output,
			StaticPowerOutputItem slagOutput, float experience, MachineRecipeProcessingSection processing) {
		BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(null, input, output, slagOutput, experience, processing);
		addRecipe(nameOverride, SCRecipeBuilder.create(recipe));
	}
}
