package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.hydroponicfarming.HydroponicFarmingRecipe;
import theking530.staticpower.data.generators.helpers.SPRecipeBuilder;
import theking530.staticpower.data.generators.helpers.SPRecipeProvider;
import theking530.staticpower.init.ModItems;

public class HydroponicFarmerRecipeGenerator extends SPRecipeProvider<HydroponicFarmingRecipe> {

	public HydroponicFarmerRecipeGenerator(DataGenerator dataGenerator) {
		super("hydroponic_farmer", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("beetroot", StaticPowerIngredient.of(Tags.Items.SEEDS_BEETROOT));
		addRecipe("wheat", StaticPowerIngredient.of(Tags.Items.SEEDS_WHEAT));
		addRecipe("carrots", StaticPowerIngredient.of(Tags.Items.CROPS_CARROT));
		addRecipe("potato", StaticPowerIngredient.of(Tags.Items.CROPS_POTATO));
		addRecipe("pumpkin", StaticPowerIngredient.of(Tags.Items.SEEDS_PUMPKIN));
		addRecipe("melon", StaticPowerIngredient.of(Tags.Items.SEEDS_MELON));

		addRecipe("static_fruit", StaticPowerIngredient.of(ModItems.StaticSeeds.get()));
		addRecipe("energized_fruit", StaticPowerIngredient.of(ModItems.EnergizedSeeds.get()));
		addRecipe("lumum_fruit", StaticPowerIngredient.of(ModItems.LumumSeeds.get()));

	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input) {
		addRecipe(nameOverride, input, null);
	}

	protected void addRecipe(String nameOverride, StaticPowerIngredient input, MachineRecipeProcessingSection processing) {
		HydroponicFarmingRecipe recipe = new HydroponicFarmingRecipe(null, input, processing);
		addRecipe(nameOverride, SPRecipeBuilder.create(recipe));
	}
}
