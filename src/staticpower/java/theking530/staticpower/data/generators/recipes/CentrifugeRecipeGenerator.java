package theking530.staticpower.data.generators.recipes;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.data.generators.helpers.SCRecipeBuilder;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.data.crafting.wrappers.centrifuge.CentrifugeRecipe;
import theking530.staticpower.data.materials.MaterialTypes;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModMaterials;

public class CentrifugeRecipeGenerator extends SCRecipeProvider<CentrifugeRecipe> {

	public CentrifugeRecipeGenerator(DataGenerator dataGenerator) {
		super("centrifuge", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("misc/gunpowder", StaticPowerIngredient.of(Tags.Items.GUNPOWDER), 500,
				StaticPowerOutputItem.of(ModItems.DustSaltpeter.get()),
				StaticPowerOutputItem.of(ModItems.DustSulfur.get()),
				StaticPowerOutputItem.of(ModMaterials.CHARCOAL.get(MaterialTypes.DUST).get()));
		addRecipe("misc/ender_eye", StaticPowerIngredient.of(Items.ENDER_EYE), 500,
				StaticPowerOutputItem.of(Items.ENDER_PEARL), StaticPowerOutputItem.of(Items.BLAZE_POWDER));
		addRecipe("misc/fire_charger", StaticPowerIngredient.of(Items.FIRE_CHARGE), 500,
				StaticPowerOutputItem.of(Items.GUNPOWDER), StaticPowerOutputItem.of(Items.BLAZE_POWDER),
				StaticPowerOutputItem.of(Items.COAL));
		addRecipe("misc/magma_cream", StaticPowerIngredient.of(Items.MAGMA_CREAM), 500,
				StaticPowerOutputItem.of(Items.SLIME_BALL), StaticPowerOutputItem.of(Items.BLAZE_POWDER));

		addRecipe("metal/inert_infusion_dust",
				StaticPowerIngredient.of(ModMaterials.INERT_INFUSION.get(MaterialTypes.DUST).getItemTag(), 3), 200,
				StaticPowerOutputItem.of(ModMaterials.SILVER.get(MaterialTypes.DUST).get()),
				StaticPowerOutputItem.of(ModMaterials.GOLD.get(MaterialTypes.DUST).get()),
				StaticPowerOutputItem.of(ModMaterials.PLATINUM.get(MaterialTypes.DUST).get()));
		addRecipe("metal/brass_dust",
				StaticPowerIngredient.of(ModMaterials.BRASS.get(MaterialTypes.DUST).getItemTag(), 2), 200,
				StaticPowerOutputItem.of(ModMaterials.COPPER.get(MaterialTypes.DUST).get()),
				StaticPowerOutputItem.of(ModMaterials.ZINC.get(MaterialTypes.DUST).get()));

		addRecipe("metal/bronze_dust",
				StaticPowerIngredient.of(ModMaterials.BRONZE.get(MaterialTypes.DUST).getItemTag(), 4), 200,
				StaticPowerOutputItem.of(ModMaterials.COPPER.get(MaterialTypes.DUST).get(), 3),
				StaticPowerOutputItem.of(ModMaterials.TIN.get(MaterialTypes.DUST).get()));

		for (MinecraftColor color : MinecraftColor.values()) {
			if (color == MinecraftColor.WHITE) {
				continue;
			}
			addRecipe("wool/" + color.getName(), StaticPowerIngredient.of(color.getItemWoolTag()), 500,
					StaticPowerOutputItem.of(color.getColoredWool().get().asItem()),
					StaticPowerOutputItem.of(color.getDyeItem().get()));
			addRecipe("concrete/" + color.getName(), StaticPowerIngredient.of(color.getItemConcreteTag(), 8), 500,
					StaticPowerOutputItem.of(Items.SAND, 4), StaticPowerOutputItem.of(Items.GRAVEL, 4),
					StaticPowerOutputItem.of(color.getDyeItem().get()));
		}

		addRecipe("dyes/cyan", StaticPowerIngredient.of(MinecraftColor.CYAN.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.GREEN_DYE), StaticPowerOutputItem.of(Items.BLUE_DYE));
		addRecipe("dyes/gray", StaticPowerIngredient.of(MinecraftColor.GRAY.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.WHITE_DYE), StaticPowerOutputItem.of(Items.BLACK_DYE));
		addRecipe("dyes/green", StaticPowerIngredient.of(MinecraftColor.GREEN.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.YELLOW_DYE), StaticPowerOutputItem.of(Items.BLUE_DYE));
		addRecipe("dyes/light_blue", StaticPowerIngredient.of(MinecraftColor.LIGHT_BLUE.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.WHITE_DYE), StaticPowerOutputItem.of(Items.BLUE_DYE));
		addRecipe("dyes/lime", StaticPowerIngredient.of(MinecraftColor.LIME.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.WHITE_DYE), StaticPowerOutputItem.of(Items.GREEN_DYE));
		addRecipe("dyes/magenta", StaticPowerIngredient.of(MinecraftColor.MAGENTA.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.PURPLE_DYE), StaticPowerOutputItem.of(Items.PINK_DYE));
		addRecipe("dyes/orange", StaticPowerIngredient.of(MinecraftColor.ORANGE.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.YELLOW_DYE), StaticPowerOutputItem.of(Items.RED_DYE));
		addRecipe("dyes/pink", StaticPowerIngredient.of(MinecraftColor.PINK.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.WHITE_DYE), StaticPowerOutputItem.of(Items.RED_DYE));
		addRecipe("dyes/purple", StaticPowerIngredient.of(MinecraftColor.PURPLE.getDyeTag(), 2), 800,
				StaticPowerOutputItem.of(Items.BLUE_DYE), StaticPowerOutputItem.of(Items.RED_DYE));
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, int minimumSpeed,
			StaticPowerOutputItem... outputs) {
		List<StaticPowerOutputItem> outputsList = new LinkedList<StaticPowerOutputItem>();
		for (StaticPowerOutputItem item : outputs) {
			outputsList.add(item);
		}
		CentrifugeRecipe recipe = new CentrifugeRecipe(null, input, outputs[0],
				outputs.length > 1 ? outputs[1] : StaticPowerOutputItem.EMPTY,
				outputs.length > 2 ? outputs[2] : StaticPowerOutputItem.EMPTY, minimumSpeed, null);
		addRecipe(nameOverride, SCRecipeBuilder.create(recipe), false);
	}
}
