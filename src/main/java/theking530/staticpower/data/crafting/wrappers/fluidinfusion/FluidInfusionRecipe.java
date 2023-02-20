package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class FluidInfusionRecipe extends AbstractMachineRecipe {
	public static final String ID = "fluid_infusion";
	public static final RecipeType<FluidInfusionRecipe> RECIPE_TYPE = new StaticPowerRecipeType<FluidInfusionRecipe>();

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidStack inputFluid;

	public FluidInfusionRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack inputFluid, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
		this.output = output;
		this.inputFluid = inputFluid;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getOutput() {
		return output;
	}

	public boolean hasItemOutput() {
		return !output.isEmpty();
	}

	public FluidStack getRequiredFluid() {
		return inputFluid;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			matched &= matchParams.hasFluids() && matchParams.getFluids()[0].equals(inputFluid);
			if (matched && matchParams.shouldVerifyFluidAmounts()) {
				matched &= matchParams.hasFluids() && matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
			}
		}

		// Check items.
		if (matched && matchParams.shouldVerifyItems()) {
			if (matchParams.shouldVerifyItemCounts()) {
				matched &= matchParams.hasItems() && input.testWithCount(matchParams.getItems()[0]);
			} else {
				matched &= matchParams.hasItems() && input.test(matchParams.getItems()[0]);
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<FluidInfusionRecipe> getSerializer() {
		return FluidInfusionRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<FluidInfusionRecipe> getType() {
		return RECIPE_TYPE;
	}
}
