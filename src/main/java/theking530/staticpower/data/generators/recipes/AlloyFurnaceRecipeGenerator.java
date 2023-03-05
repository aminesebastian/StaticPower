package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.NewModMaterials;

public class AlloyFurnaceRecipeGenerator extends SPRecipeProvider<AlloyFurnaceRecipe> {

	public AlloyFurnaceRecipeGenerator(DataGenerator dataGenerator) {
		super("alloying", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("brass_ingot", StaticPowerIngredient.of(NewModMaterials.COPPER.get(MaterialTypes.INGOT).get()),
				StaticPowerIngredient.of(NewModMaterials.ZINC.get(MaterialTypes.INGOT).getItemTag()),
				StaticPowerOutputItem.of(NewModMaterials.BRASS.get(MaterialTypes.INGOT).get(), 2));
		addRecipe("brass_block", StaticPowerIngredient.of(NewModMaterials.COPPER.get(MaterialTypes.STORAGE_BLOCK).get()),
				StaticPowerIngredient.of(NewModMaterials.ZINC.get(MaterialTypes.STORAGE_BLOCK).getItemTag()),
				StaticPowerOutputItem.of(NewModMaterials.BRASS.get(MaterialTypes.STORAGE_BLOCK).get(), 2));

		addRecipe("bronze_ingot", StaticPowerIngredient.of(NewModMaterials.COPPER.get(MaterialTypes.INGOT).get(), 3),
				StaticPowerIngredient.of(NewModMaterials.TIN.get(MaterialTypes.INGOT).get()), StaticPowerOutputItem.of(NewModMaterials.BRONZE.get(MaterialTypes.INGOT).get(), 4));
		addRecipe("bronze_block", StaticPowerIngredient.of(NewModMaterials.COPPER.get(MaterialTypes.STORAGE_BLOCK).get(), 3),
				StaticPowerIngredient.of(NewModMaterials.TIN.get(MaterialTypes.STORAGE_BLOCK).get()),
				StaticPowerOutputItem.of(NewModMaterials.BRONZE.get(MaterialTypes.STORAGE_BLOCK).get(), 4));

		addRecipe("redstone_alloy_ingot", StaticPowerIngredient.of(NewModMaterials.SILVER.get(MaterialTypes.INGOT).getItemTag()),
				StaticPowerIngredient.of(Tags.Items.DUSTS_REDSTONE), StaticPowerOutputItem.of(NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.INGOT).get(), 2));
		addRecipe("redstone_alloy_block", StaticPowerIngredient.of(NewModMaterials.SILVER.get(MaterialTypes.STORAGE_BLOCK).getItemTag()),
				StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE), StaticPowerOutputItem.of(NewModMaterials.REDSTONE_ALLOY.get(MaterialTypes.STORAGE_BLOCK).get(), 2));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output) {
		addRecipe(nameOverride, input1, input2, output, 0.25f);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output, float experience) {
		AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(null, input1, input2, output, experience, null);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
