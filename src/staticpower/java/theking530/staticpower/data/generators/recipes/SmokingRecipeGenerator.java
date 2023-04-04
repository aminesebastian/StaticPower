package theking530.staticpower.data.generators.recipes;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmokingRecipe;
import theking530.staticcore.data.generators.helpers.SCRecipeProvider;
import theking530.staticpower.init.ModItems;

public class SmokingRecipeGenerator extends SCRecipeProvider<SmokingRecipe> {

	public SmokingRecipeGenerator(DataGenerator dataGenerator) {
		super("smoking", dataGenerator);
	}

	@Override
	protected void buildRecipes() {
		addRecipe("eeef", SimpleCookingRecipeBuilder.smoking(Ingredient.of(ModItems.RawEeef.get()), ModItems.CookedEeef.get(), 0.5f, 100).unlockedBy("has_raw",
				hasItems((ModItems.RawEeef.get()))));
		addRecipe("smeep", SimpleCookingRecipeBuilder.smoking(Ingredient.of(ModItems.RawSmutton.get()), ModItems.CookedSmutton.get(), 0.5f, 100).unlockedBy("has_raw",
				hasItems((ModItems.RawSmutton.get()))));
	}
}
