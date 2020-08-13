package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<ThermalConductivityRecipe> RECIPE_TYPE = IRecipeType.register("thermal_conducitity");

	private final Ingredient blocks;
	private final FluidStack fluid;
	private final float thermalConductivity;

	public ThermalConductivityRecipe(ResourceLocation name, Ingredient blocks, FluidStack fluid, float thermalConductivity) {
		super(name);
		this.blocks = blocks;
		this.fluid = fluid;
		this.thermalConductivity = thermalConductivity;
	}

	public Ingredient getBlocks() {
		return blocks;
	}

	public FluidStack getFluid() {
		return fluid;
	}

	public float getThermalConductivity() {
		return thermalConductivity;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check for block match first.
		if (blocks != Ingredient.EMPTY && matchParams.hasItems() && blocks.test(matchParams.getItems()[0])) {
			return true;
		}
		// Then check for a fluid match.
		if (!fluid.isEmpty() && matchParams.hasFluids() && fluid.isFluidEqual(matchParams.getFluids()[0])) {
			return true;
		}

		// Handle the edge case for AIR.
		if (blocks == Ingredient.EMPTY && fluid.isEmpty() && matchParams.hasItems() && matchParams.getItems()[0].isEmpty()) {
			return true;
		}

		return false;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ThermalConductivityRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
