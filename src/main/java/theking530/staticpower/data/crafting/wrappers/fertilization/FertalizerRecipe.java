package theking530.staticpower.data.crafting.wrappers.fertilization;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class FertalizerRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "farming_fertalizer";
	public static final RecipeType<FertalizerRecipe> RECIPE_TYPE = new StaticPowerRecipeType<FertalizerRecipe>();
	
	private final FluidStack inputFluid;
	private final float fertalizationAmount;

	public FertalizerRecipe(ResourceLocation name, FluidStack inputFluid, float fertalizationAmount) {
		super(name);
		this.inputFluid = inputFluid;
		this.fertalizationAmount = fertalizationAmount;
	}

	public FluidStack getRequiredFluid() {
		return inputFluid;
	}

	public float getFertalizationAmount() {
		return fertalizationAmount;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return inputFluid.isFluidEqual(matchParams.getFluids()[0]) && matchParams.getFluids()[0].getAmount() >= inputFluid.getAmount();
	}

	@Override
	public RecipeSerializer<FertalizerRecipe> getSerializer() {
		return FertalizerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<FertalizerRecipe> getType() {
		return RECIPE_TYPE;
	}
}
