package theking530.staticpower.data.crafting.wrappers.mixer;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class MixerRecipe extends AbstractMachineRecipe {
	public static final RecipeType<MixerRecipe> RECIPE_TYPE = RecipeType.register("mixer");

	private final StaticPowerIngredient input1;
	private final StaticPowerIngredient input2;
	private final FluidStack inputFluid1;
	private final FluidStack inputFluid2;
	private final FluidStack output;

	public MixerRecipe(ResourceLocation name, StaticPowerIngredient input1, StaticPowerIngredient input2, FluidStack inputFluid1, FluidStack inputFluid2, FluidStack output,
			int processingTime, long powerCost) {
		super(name, processingTime, powerCost);
		this.input1 = input1;
		this.input2 = input2;
		this.inputFluid1 = inputFluid1;
		this.inputFluid2 = inputFluid2;
		this.output = output;
	}

	public StaticPowerIngredient getPrimaryItemInput() {
		return input1;
	}

	public boolean hasPrimaryItemInput() {
		return !input1.isEmpty();
	}

	public StaticPowerIngredient getSecondaryItemInput() {
		return input2;
	}

	public boolean hasSecondaryItemInput() {
		return !input2.isEmpty();
	}

	public int getRequiredItemIngredientCount() {
		int initial = input1.isEmpty() ? 0 : 1;
		return initial + (input2.isEmpty() ? 0 : 1);
	}

	public FluidStack getPrimaryFluidInput() {
		return inputFluid1;
	}

	public boolean hasPrimaryFluidInput() {
		return !inputFluid1.isEmpty();
	}

	public FluidStack getSecondaryFluidInput() {
		return inputFluid2;
	}

	public boolean hasSecondaryFluidInput() {
		return !inputFluid2.isEmpty();
	}

	public int getRequiredFluidCount() {
		int initial = inputFluid1.isEmpty() ? 0 : 1;
		return initial + (inputFluid2.isEmpty() ? 0 : 1);
	}

	public FluidStack getOutput() {
		return output;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		boolean matched = true;

		// Check fluids.
		if (matchParams.shouldVerifyFluids()) {
			// If we dont have enough fluids, return false.
			if (matchParams.getFluids().length < getRequiredFluidCount()) {
				return false;
			}

			// Check fluids either way.
			boolean straightMatch = true;
			straightMatch &= matchParams.getFluids()[0].equals(inputFluid1);
			straightMatch &= matchParams.getFluids()[1].equals(inputFluid2);
			if (!straightMatch) {
				matched &= matchParams.getFluids()[1].equals(inputFluid1);
				matched &= matchParams.getFluids()[0].equals(inputFluid2);
			}

			// Verify the amounts.
			if (matched && matchParams.shouldVerifyFluidAmounts()) {
				if (straightMatch) {
					matched &= matchParams.getFluids()[0].getAmount() >= inputFluid1.getAmount();
					matched &= matchParams.getFluids()[1].getAmount() >= inputFluid2.getAmount();
				} else {
					matched &= matchParams.getFluids()[1].getAmount() >= inputFluid1.getAmount();
					matched &= matchParams.getFluids()[0].getAmount() >= inputFluid2.getAmount();
				}
			}
		}

		// Check items.
		if (matchParams.shouldVerifyItems()) {
			// If we dont have enough items, return false.
			if (matchParams.getItems().length < getRequiredItemIngredientCount()) {
				return false;
			}

			// Check items either way.
			boolean straightMatch = true;
			straightMatch &= matchParams.hasItems() && input1.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
			straightMatch &= matchParams.hasItems() && input2.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts());

			// Check the alternate config.
			if (!straightMatch) {
				matched &= matchParams.hasItems() && input1.test(matchParams.getItems()[1], matchParams.shouldVerifyItemCounts());
				matched &= matchParams.hasItems() && input2.test(matchParams.getItems()[0], matchParams.shouldVerifyItemCounts());
			}
		}

		return matched;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return MixerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
