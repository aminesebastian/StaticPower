package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import theking530.staticpower.data.MaterialBundle;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class HammerRecipeGenerator extends SPRecipeProvider<HammerRecipe> {

	public HammerRecipeGenerator(DataGenerator dataGenerator) {
		super("hammering", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addBlockRecipe("sand_to_silicon", BlockTags.SAND, StaticPowerOutputItem.of(ModItems.Silicon.get(), 1, 0.15f));
		addItemRecipe("rubber_sheet", StaticPowerIngredient.of(ModItemTags.RUBBER), StaticPowerOutputItem.of(ModItems.RubberSheet.get()));

		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.shouldGenerateHeatedIngot() && material.shouldGeneratePlate()) {
				addItemRecipe("plates/" + material.getName(), StaticPowerIngredient.of(material.getHeatedIngot().get()), StaticPowerOutputItem.of(material.getPlate().get(), 2));
			}
		}
	}

	protected void addBlockRecipe(String nameOverride, TagKey<Block> block, StaticPowerOutputItem output) {
		addRecipe(nameOverride, block, null, output);
	}

	protected void addItemRecipe(String nameOverride, StaticPowerIngredient inputItem, StaticPowerOutputItem output) {
		addRecipe(nameOverride, null, inputItem, output);
	}

	protected void addRecipe(String nameOverride, TagKey<Block> block, StaticPowerIngredient inputItem, StaticPowerOutputItem output) {
		HammerRecipe recipe = new HammerRecipe(null, 1, inputItem, block, output);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
