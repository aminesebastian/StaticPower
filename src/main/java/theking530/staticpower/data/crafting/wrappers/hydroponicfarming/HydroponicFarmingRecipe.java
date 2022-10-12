package theking530.staticpower.data.crafting.wrappers.hydroponicfarming;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class HydroponicFarmingRecipe extends AbstractMachineRecipe {
	public static final String ID = "hydroponic_farming";
	public static final RecipeType<HydroponicFarmingRecipe> RECIPE_TYPE = new StaticPowerRecipeType<HydroponicFarmingRecipe>();

	private final StaticPowerIngredient input;

	public HydroponicFarmingRecipe(ResourceLocation name, StaticPowerIngredient input, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<HydroponicFarmingRecipe> getSerializer() {
		return HydroponicFarmingRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<HydroponicFarmingRecipe> getType() {
		return RECIPE_TYPE;
	}
}
