package theking530.staticpower.data.crafting.wrappers.centrifuge;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class CentrifugeRecipe extends AbstractMachineRecipe {
	public static final String SPEED_PROPERTY = "Speed";
	public static final String ID = "centrifuge";
	public static final RecipeType<CentrifugeRecipe> RECIPE_TYPE = new StaticPowerRecipeType<CentrifugeRecipe>();

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output1;
	private final StaticPowerOutputItem output2;
	private final StaticPowerOutputItem output3;
	private final int minimumSpeed;

	public CentrifugeRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output1, StaticPowerOutputItem output2,
			StaticPowerOutputItem output3, int minimumSpeed, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
		this.output1 = output1;
		this.output2 = output2;
		this.output3 = output3;
		this.minimumSpeed = minimumSpeed;
	}

	public StaticPowerIngredient getInput() {
		return input;
	}

	public StaticPowerOutputItem getOutput1() {
		return output1;
	}

	public StaticPowerOutputItem getOutput2() {
		return output2;
	}

	public StaticPowerOutputItem getOutput3() {
		return output3;
	}

	public int getMinimumSpeed() {
		return minimumSpeed;
	}

	public List<StaticPowerOutputItem> getOutputs() {
		List<StaticPowerOutputItem> output = new ArrayList<StaticPowerOutputItem>();
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
	public RecipeSerializer<CentrifugeRecipe> getSerializer() {
		return CentrifugeRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<CentrifugeRecipe> getType() {
		return RECIPE_TYPE;
	}

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

		// Check the speed.
		if (matched && matchParams.getExtraProperty(SPEED_PROPERTY).isPresent()) {
			if ((int) (matchParams.getExtraProperty(SPEED_PROPERTY).get()) < minimumSpeed) {
				return false;
			}
		}

		return matched;
	}
}
