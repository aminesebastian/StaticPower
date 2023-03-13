package theking530.staticpower.data.crafting.wrappers.squeezer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.JsonUtilities;
import theking530.staticpower.data.crafting.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class SqueezerRecipe extends AbstractMachineRecipe {
	public static final String ID = "squeezer";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<SqueezerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInput()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("output_item", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getOutput()),
					JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output_fluid").forGetter(recipe -> recipe.getOutputFluid()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, SqueezerRecipe::new));

	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;
	private final FluidStack outputFluid;

	public SqueezerRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack outputFluid, MachineRecipeProcessingSection processing) {
		super(name, processing);
		this.input = input;
		this.output = output;
		this.outputFluid = outputFluid;
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

	public FluidStack getOutputFluid() {
		return outputFluid;
	}

	public boolean hasOutputFluid() {
		return !outputFluid.isEmpty();
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
	public RecipeSerializer<SqueezerRecipe> getSerializer() {
		return ModRecipeSerializers.SQUEEZER_SERIALIZER.get();
	}

	@Override
	public RecipeType<SqueezerRecipe> getType() {
		return ModRecipeTypes.SQUEEZER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
