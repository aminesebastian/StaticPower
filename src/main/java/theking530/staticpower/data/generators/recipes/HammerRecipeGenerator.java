package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.hammer.HammerRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.data.materials.MaterialBundle;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;
import theking530.staticpower.init.tags.ModItemTags;

public class HammerRecipeGenerator extends SPRecipeProvider<HammerRecipe> {

	public HammerRecipeGenerator(DataGenerator dataGenerator) {
		super("hammering", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		for (MaterialBundle material : ModMaterials.MATERIALS.values()) {
			if (material.has(MaterialTypes.HEATED_INGOT) && material.has(MaterialTypes.PLATE)) {
				addItemRecipe("plates/" + material.getName(), StaticPowerIngredient.of(material.get(MaterialTypes.HEATED_INGOT).get()),
						StaticPowerOutputItem.of(material.get(MaterialTypes.PLATE).get(), 2), true);
			}
		}

		addBlockRecipe("sand_to_silicon", BlockTags.SAND, StaticPowerOutputItem.of(ModItems.Silicon.get(), 1, 0.15f));
		addItemRecipe("rubber_sheet", StaticPowerIngredient.of(ModItemTags.RUBBER), StaticPowerOutputItem.of(ModItems.RubberSheet.get()), false);
		addItemRecipe("coal_dust", StaticPowerIngredient.of(ModMaterials.COAL.get(MaterialTypes.RAW_MATERIAL).getItemTag()),
				StaticPowerOutputItem.of(ModMaterials.COAL.get(MaterialTypes.DUST).get()), false);
		addItemRecipe("charcoal_dust", StaticPowerIngredient.of(ModMaterials.CHARCOAL.get(MaterialTypes.RAW_MATERIAL).getItemTag()),
				StaticPowerOutputItem.of(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).get()), false);
	}

	protected void addBlockRecipe(String nameOverride, TagKey<Block> block, StaticPowerOutputItem output) {
		addRecipe(nameOverride, block, null, output, false);
	}

	protected void addItemRecipe(String nameOverride, StaticPowerIngredient inputItem, StaticPowerOutputItem output, boolean requiresAnvil) {
		addRecipe(nameOverride, null, inputItem, output, requiresAnvil);
	}

	protected void addRecipe(String nameOverride, TagKey<Block> block, StaticPowerIngredient inputItem, StaticPowerOutputItem output, boolean requiresAnvil) {
		HammerRecipe recipe = new HammerRecipe(null, 1, HammerRecipe.DEFAULT_HAMMERS, inputItem, block, output, requiresAnvil);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
