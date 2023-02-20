package theking530.staticpower.data.crafting.wrappers.fluidgenerator;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FluidGeneratorRecipeSerializer extends StaticPowerRecipeSerializer<FluidGeneratorRecipe> {
	public static final FluidGeneratorRecipeSerializer INSTANCE = new FluidGeneratorRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "fluid_generator_recipe");

	@Override
	public FluidGeneratorRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the contained fluid.
		JsonObject fluidObject = json.get("fluid").getAsJsonObject();
		FluidStack containedFluid = StaticPowerJsonParsingUtilities.parseFluidStack(fluidObject);
		if (containedFluid.isEmpty()) {
			throw new RuntimeException(String.format("Cannot read recipe: %1$s with empty generation fluid!", recipeId.toString()));
		}

		// Capture the generated power.
		int powerAmount = json.get("power").getAsInt();
		if (powerAmount <= 0) {
			throw new RuntimeException(String.format("Cannot read recipe: %1$s with less-than-or-zero power generation!", recipeId.toString()));
		}
		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, containedFluid, powerAmount);
	}

	@Override
	public FluidGeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int power = buffer.readInt();
		FluidStack fluid = buffer.readFluidStack();
		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, fluid, power);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FluidGeneratorRecipe recipe) {
		buffer.writeInt(recipe.getPowerGeneration());
		buffer.writeFluidStack(recipe.getFluid());
	}
}
