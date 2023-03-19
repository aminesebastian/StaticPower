package theking530.staticpower.data.crafting.wrappers.centrifuge;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class CentrifugeRecipe extends AbstractMachineRecipe {
	public static final String ID = "centrifuge";
	public static final String SPEED_PROPERTY = "Speed";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<CentrifugeRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput()),
					StaticPowerOutputItem.CODEC.fieldOf("primary_output").forGetter(recipe -> recipe.getOutput1()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("secondary_output", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getOutput2()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("tertiary_output", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getOutput3()),
					Codec.INT.fieldOf("minimum_speed").forGetter(recipe -> recipe.getMinimumSpeed()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, CentrifugeRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output1;
	private final StaticPowerOutputItem output2;
	private final StaticPowerOutputItem output3;
	private final int minimumSpeed;

	public CentrifugeRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output1, StaticPowerOutputItem output2, StaticPowerOutputItem output3,
			int minimumSpeed, MachineRecipeProcessingSection processing) {
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
		return ModRecipeSerializers.CENTRIFUGE_SERIALIZER.get();
	}

	@Override
	public RecipeType<CentrifugeRecipe> getType() {
		return ModRecipeTypes.CENTRIFUGE_RECIPE_TYPE.get();
	}

	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
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

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
