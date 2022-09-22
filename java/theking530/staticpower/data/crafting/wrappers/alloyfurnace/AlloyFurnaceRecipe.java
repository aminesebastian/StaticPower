package theking530.staticpower.data.crafting.wrappers.alloyfurnace;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class AlloyFurnaceRecipe extends AbstractMachineRecipe {
	public static final String ID = "alloy_furnace";
	public static final RecipeType<AlloyFurnaceRecipe> RECIPE_TYPE = new StaticPowerRecipeType<AlloyFurnaceRecipe>();

	private final StaticPowerIngredient input1;
	private final StaticPowerIngredient input2;
	private final ProbabilityItemStackOutput output;
	private final int experience;

	public AlloyFurnaceRecipe(ResourceLocation name, StaticPowerIngredient input1, StaticPowerIngredient input2, ProbabilityItemStackOutput output, int experience,
			MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.experience = experience;
		this.input1 = input1;
		this.input2 = input2;
		this.output = output;
	}

	public StaticPowerIngredient getInput1() {
		return input1;
	}

	public StaticPowerIngredient getInput2() {
		return input2;
	}

	public ProbabilityItemStackOutput getOutput() {
		return output;
	}

	public int getExperience() {
		return experience;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check if the input counts catch.
		if (matchParams.shouldVerifyItems() && matchParams.getItems().length != 2) {
			return false;
		}

		if (matchParams.shouldVerifyItemCounts()) {
			if (input1.testWithCount(matchParams.getItems()[0]) && input2.testWithCount(matchParams.getItems()[1])) {
				return true;
			}
			if (input1.testWithCount(matchParams.getItems()[1]) && input2.testWithCount(matchParams.getItems()[0])) {
				return true;
			}
		} else {
			if (input1.test(matchParams.getItems()[0]) && input2.test(matchParams.getItems()[1])) {
				return true;
			}
			if (input1.test(matchParams.getItems()[1]) && input2.test(matchParams.getItems()[0])) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AlloyFurnaceRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
