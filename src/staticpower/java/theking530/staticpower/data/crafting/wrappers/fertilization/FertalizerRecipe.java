package theking530.staticpower.data.crafting.wrappers.fertilization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;

public class FertalizerRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "farming_fertalizer";
	public static final Codec<FertalizerRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					FluidIngredient.CODEC.fieldOf("fluid").forGetter(recipe -> recipe.getRequiredFluid()),
					Codec.FLOAT.fieldOf("fertilization_chance").forGetter(recipe -> recipe.getFertalizationAmount())).apply(instance, FertalizerRecipe::new));

	private final FluidIngredient inputFluid;
	private final float fertilizationChance;

	public FertalizerRecipe(ResourceLocation id, FluidIngredient inputFluid, float fertalizationAmount) {
		super(id);
		this.inputFluid = inputFluid;
		this.fertilizationChance = fertalizationAmount;
	}

	public FluidIngredient getRequiredFluid() {
		return inputFluid;
	}

	public float getFertalizationAmount() {
		return fertilizationChance;
	}

	@Override
	public boolean matches(RecipeMatchParameters matchParams, Level worldIn) {
		if (!matchParams.shouldVerifyFluids()) {
			return true;
		}
		return inputFluid.test(matchParams.getFluids()[0], matchParams.shouldVerifyFluidAmounts());
	}

	@Override
	public RecipeSerializer<FertalizerRecipe> getSerializer() {
		return ModRecipeSerializers.FERTILIZER_SERIALIZER.get();
	}

	@Override
	public RecipeType<FertalizerRecipe> getType() {
		return ModRecipeTypes.FERTALIZER_RECIPE_TYPE.get();
	}
}
