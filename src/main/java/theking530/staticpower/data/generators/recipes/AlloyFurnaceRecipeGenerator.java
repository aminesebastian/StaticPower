package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.alloyfurnace.AlloyFurnaceRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModMaterials;

public class AlloyFurnaceRecipeGenerator extends SPRecipeProvider<AlloyFurnaceRecipe> {

	public AlloyFurnaceRecipeGenerator(DataGenerator dataGenerator) {
		super("alloying", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("brass_ingot", StaticPowerIngredient.of(ModMaterials.COPPER.getIngotTag()), StaticPowerIngredient.of(ModMaterials.ZINC.getIngotTag()),
				StaticPowerOutputItem.of(ModMaterials.BRASS.getIngot().get(), 2));
		addRecipe("brass_block", StaticPowerIngredient.of(ModMaterials.COPPER.getStorageBlockItemTag()), StaticPowerIngredient.of(ModMaterials.ZINC.getStorageBlockItemTag()),
				StaticPowerOutputItem.of(ModMaterials.BRASS.getStorageBlock().get(), 2));

		addRecipe("bronze_ingot", StaticPowerIngredient.of(ModMaterials.COPPER.getIngotTag(), 3), StaticPowerIngredient.of(ModMaterials.TIN.getIngotTag()),
				StaticPowerOutputItem.of(ModMaterials.BRONZE.getIngot().get(), 4));
		addRecipe("bronze_block", StaticPowerIngredient.of(ModMaterials.COPPER.getStorageBlockItemTag(), 3), StaticPowerIngredient.of(ModMaterials.TIN.getStorageBlockItemTag()),
				StaticPowerOutputItem.of(ModMaterials.BRONZE.getStorageBlock().get(), 4));

		addRecipe("redstone_alloy_ingot", StaticPowerIngredient.of(ModMaterials.SILVER.getIngotTag()), StaticPowerIngredient.of(Tags.Items.DUSTS_REDSTONE),
				StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.getIngot().get(), 2));
		addRecipe("redstone_alloy_block", StaticPowerIngredient.of(ModMaterials.SILVER.getStorageBlockItemTag()), StaticPowerIngredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE),
				StaticPowerOutputItem.of(ModMaterials.REDSTONE_ALLOY.getStorageBlock().get(), 2));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output) {
		addRecipe(nameOverride, input1, input2, output, 0.25f);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input1, StaticPowerIngredient input2, StaticPowerOutputItem output, float experience) {
		AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(null, input1, input2, output, experience, null);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
