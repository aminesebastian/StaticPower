package theking530.staticpower.data.crafting.wrappers.fermenter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.AbstractMachineRecipe;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class FermenterRecipe extends AbstractMachineRecipe {
	public static final String ID = "fermenter";
	public static final int DEFAULT_PROCESSING_TIME = 200;
	public static final double DEFAULT_POWER_COST = 5.0;

	public static final Codec<FermenterRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					StaticPowerIngredient.CODEC.fieldOf("input").forGetter(recipe -> recipe.getInputIngredient()),
					StaticPowerOutputItem.CODEC.optionalFieldOf("output_item", StaticPowerOutputItem.EMPTY).forGetter(recipe -> recipe.getResidualOutput()),
					JsonUtilities.FLUIDSTACK_CODEC.fieldOf("output_fluid").forGetter(recipe -> recipe.getOutputFluidStack()),
					MachineRecipeProcessingSection.CODEC.fieldOf("processing").forGetter(recipe -> recipe.getProcessingSection())).apply(instance, FermenterRecipe::new));

	private final FluidStack outputFluidStack;
	private final StaticPowerIngredient input;
	private final StaticPowerOutputItem output;

	public FermenterRecipe(ResourceLocation name, StaticPowerIngredient input, StaticPowerOutputItem output, FluidStack fluid, MachineRecipeProcessingSection processing) {
		super(name, processing);
		outputFluidStack = fluid;
		this.input = input;
		this.output = output;
	}

	public FluidStack getOutputFluidStack() {
		return outputFluidStack;
	}

	public StaticPowerOutputItem getResidualOutput() {
		return output;
	}

	public StaticPowerIngredient getInputIngredient() {
		return input;
	}

	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
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
	public RecipeSerializer<FermenterRecipe> getSerializer() {
		return ModRecipeSerializers.FERMENTER_SERIALIZER.get();
	}

	@Override
	public RecipeType<FermenterRecipe> getType() {
		return ModRecipeTypes.FERMENTER_RECIPE_TYPE.get();
	}

	@Override
	protected MachineRecipeProcessingSection getDefaultProcessingSection() {
		return MachineRecipeProcessingSection.hardcoded(DEFAULT_PROCESSING_TIME, DEFAULT_POWER_COST, 0, 0);
	}
}
