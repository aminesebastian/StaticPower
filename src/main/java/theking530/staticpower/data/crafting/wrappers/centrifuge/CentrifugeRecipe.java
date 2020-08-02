package theking530.staticpower.data.crafting.wrappers.centrifuge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class CentrifugeRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<CentrifugeRecipe> RECIPE_TYPE = IRecipeType.register("centrifuge");

	private final StaticPowerIngredient input;
	private final ProbabilityItemStackOutput output1;
	private final ProbabilityItemStackOutput output2;
	private final ProbabilityItemStackOutput output3;
	private final int minimumSpeed;

	public CentrifugeRecipe(ResourceLocation name, int processingTime, int powerCost, StaticPowerIngredient input, ProbabilityItemStackOutput output1, ProbabilityItemStackOutput output2,
			ProbabilityItemStackOutput output3, int minimumSpeed) {
		super(name, processingTime, powerCost);
		this.input = input;
		this.output1 = output1;
		this.output2 = output2;
		this.output3 = output3;
		this.minimumSpeed = minimumSpeed;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public ProbabilityItemStackOutput getOutput1() {
		return output1;
	}

	public ProbabilityItemStackOutput getOutput2() {
		return output2;
	}

	public ProbabilityItemStackOutput getOutput3() {
		return output3;
	}

	public int getMinimumSpeed() {
		return minimumSpeed;
	}

	public List<ProbabilityItemStackOutput> getOutputs() {
		List<ProbabilityItemStackOutput> output = new ArrayList<ProbabilityItemStackOutput>();
		output.add(output1);

		if (!output2.isEmpty()) {
			output.add(output2);
		}
		if (!output3.isEmpty()) {
			output.add(output3);
		}

		return output;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return CentrifugeRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		return input.test(matchParams.getItems()[0]);
	}
}
