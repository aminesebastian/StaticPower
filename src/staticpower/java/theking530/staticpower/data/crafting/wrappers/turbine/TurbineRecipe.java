package theking530.staticpower.data.crafting.wrappers.turbine;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class TurbineRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "turbine";

	public static final Codec<TurbineRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					FluidIngredient.CODEC.fieldOf("input_fluid").forGetter(recipe -> recipe.getInput()),
					JsonUtilities.FLUIDSTACK_CODEC.optionalFieldOf("output_fluid", FluidStack.EMPTY).forGetter(recipe -> recipe.getOutput()),
					Codec.INT.fieldOf("generation_amount").forGetter(recipe -> recipe.getGenerationAmount())).apply(instance, TurbineRecipe::new));

	private final FluidIngredient input;
	private final FluidStack output;
	private final int generationAmount;

	public TurbineRecipe(ResourceLocation name, FluidIngredient input, FluidStack output, int generationAmount) {
		super(name);
		this.input = input;
		this.output = output;
		this.generationAmount = generationAmount;
	}

	public FluidIngredient getInput() {
		return input;
	}

	public FluidStack getOutput() {
		return output;
	}

	public boolean hasOutput() {
		return !output.isEmpty();
	}

	public int getGenerationAmount() {
		return generationAmount;
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		if (matchParams.shouldVerifyFluids() && matchParams.hasFluids()) {
			return input.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
		}
		return true;
	}

	@Override
	public RecipeSerializer<TurbineRecipe> getSerializer() {
		return ModRecipeSerializers.TURBINE_SERIALIZER.get();
	}

	@Override
	public RecipeType<TurbineRecipe> getType() {
		return ModRecipeTypes.TURBINE_RECIPE_TYPE.get();
	}
}
