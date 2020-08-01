package theking530.staticpower.data.crafting.wrappers.farmer;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;

public class FarmingFertalizerRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<FarmingFertalizerRecipe> RECIPE_TYPE = IRecipeType.register("farming_fertalizer");
	private final FluidStack inputFluid;
	private final float fertalizationAmount;

	public FarmingFertalizerRecipe(ResourceLocation name, FluidStack inputFluid, float fertalizationAmount) {
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
	public IRecipeSerializer<?> getSerializer() {
		return FarmingFertalizerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
