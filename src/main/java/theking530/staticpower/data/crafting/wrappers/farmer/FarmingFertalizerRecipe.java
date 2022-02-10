package theking530.staticpower.data.crafting.wrappers.farmer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class FarmingFertalizerRecipe extends AbstractStaticPowerRecipe {
	public static final RecipeType<FarmingFertalizerRecipe> RECIPE_TYPE = RecipeType.register("farming_fertalizer");
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
	public RecipeSerializer<?> getSerializer() {
		return FarmingFertalizerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
