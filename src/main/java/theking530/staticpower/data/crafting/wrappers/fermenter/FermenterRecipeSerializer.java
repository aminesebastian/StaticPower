package theking530.staticpower.data.crafting.wrappers.fermenter;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class FermenterRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FermenterRecipe> {
	public static final FermenterRecipeSerializer INSTANCE = new FermenterRecipeSerializer();

	private FermenterRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "fermenter_recipe"));
	}

	@Override
	public FermenterRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Get the fluid output.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");
		FluidStack fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputElement);

		// Get the residual output.
		ProbabilityItemStackOutput residualOutput = ProbabilityItemStackOutput.EMPTY;
		if(json.has("residual")) {
			JsonObject residualElement = GsonHelper.getAsJsonObject(json, "residual");
			residualOutput = ProbabilityItemStackOutput.parseFromJSON(residualElement);
		}

		// Create the recipe.
		return new FermenterRecipe(recipeId, input, residualOutput, fluidOutput);
	}

	@Override
	public FermenterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		FluidStack output = buffer.readFluidStack();
		ProbabilityItemStackOutput residualOutput = ProbabilityItemStackOutput.readFromBuffer(buffer);
		// Create the recipe.
		return new FermenterRecipe(recipeId, input, residualOutput, output);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FermenterRecipe recipe) {
		recipe.getInputIngredient().write(buffer);
		buffer.writeFluidStack(recipe.getOutputFluidStack());
		recipe.getResidualOutput().writeToBuffer(buffer);
	}
}
