package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModMaterials;

public class AlloyFurnaceRecipeGenerator extends SPRecipeProvider<AlloyFurnaceRecipe> {

	public AlloyFurnaceRecipeGenerator(DataGenerator dataGenerator) {
		super("alloying", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("brass_ingot", StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.INGOT).get()),
				StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()), StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.INGOT).get(), 2));
		addRecipe("brass_block", StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.STORAGE_BLOCK).get()),
				StaticPowerIngredient.of(ModMaterials.ZINC.get(MaterialTypes.STORAGE_BLOCK).getItemTag()),
				StaticPowerOutputItem.of(ModMaterials.BRASS.get(MaterialTypes.STORAGE_BLOCK).get(), 2));

		addRecipe("bronze_ingot", StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.INGOT).get(), 3),
				StaticPowerIngredient.of(ModMaterials.TIN.get(MaterialTypes.INGOT).get()), StaticPowerOutputItem.of(ModMaterials.BRONZE.get(MaterialTypes.INGOT).get(), 4));
		addRecipe("bronze_block", StaticPowerIngredient.of(ModMaterials.COPPER.get(MaterialTypes.STORAGE_BLOCK).get(), 3),
				StaticPowerIngredient.of(ModMaterials.TIN.get(MaterialTypes.STORAGE_BLOCK).get()),
				StaticPowerOutputItem.of(ModMaterials.BRONZE.get(MaterialTypes.STORAGE_BLOCK).get(), 4));

		addRecipe("redstone_alloy_ingot", StaticPowerIngredient.of(ModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()), StaticPowerIngredient.of(Tags.Items.DUSTS_REDSTONE),
				StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).get(), 2));
		addRecipe("redstone_alloy_block", StaticPowerIngredient.of(ModMaterials.SILVER.get(MaterialTypes.STORAGE_BLOCK).getItemTag()),
				StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE), StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).get(), 2));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output) {
		addRecipe(nameOverride, input1, input2, output, 0.25f);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output, float experience) {
		addRecipe(nameOverride, input1, input2, output, experience, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output, float experience, int processingTime) {
		addRecipe(nameOverride, input1, input2, output, experience, MachineRecipeProcessingSection.hardcoded(processingTime, 0, 0, 0));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output, float experience,
			MachineRecipeProcessingSection processing) {
		AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(null, input1, input2, output, experience, processing);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
