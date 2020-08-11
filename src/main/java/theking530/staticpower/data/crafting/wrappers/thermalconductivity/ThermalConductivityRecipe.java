package theking530.staticpower.data.crafting.wrappers.thermalconductivity;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;

public class ThermalConductivityRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<ThermalConductivityRecipe> RECIPE_TYPE = IRecipeType.register("thermal_conducitity");

	private final Ingredient blocks;
	private final float thermalConductivity;

	public ThermalConductivityRecipe(ResourceLocation name, Ingredient blocks, float thermalConductivity) {
		super(name);
		this.blocks = blocks;
		this.thermalConductivity = thermalConductivity;
	}

	public Ingredient getBlocks() {
		return blocks;
	}

	public float getThermalConductivity() {
		return thermalConductivity;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return blocks.test(matchParams.getItems()[0]);
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
