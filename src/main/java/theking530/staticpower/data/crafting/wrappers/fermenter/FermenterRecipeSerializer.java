package theking530.staticpower.data.crafting.wrappers.fermenter;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FermenterRecipeSerializer extends StaticPowerRecipeSerializer<FermenterRecipe> {
	public static final FermenterRecipeSerializer INSTANCE = new FermenterRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "fermenter_recipe");

	@Override
	public FermenterRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Get the fluid output.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");
		FluidStack fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputElement);

		// Get the residual output.
		StaticPowerOutputItem residualOutput = StaticPowerOutputItem.EMPTY;
		if (json.has("residual")) {
			JsonObject residualElement = GsonHelper.getAsJsonObject(json, "residual");
			residualOutput = StaticPowerOutputItem.parseFromJSON(residualElement);
		}

		// Create the recipe.
		return new FermenterRecipe(recipeId, input, residualOutput, fluidOutput);
	}

	@Override
	public FermenterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		FluidStack output = buffer.readFluidStack();
		StaticPowerOutputItem residualOutput = StaticPowerOutputItem.readFromBuffer(buffer);
		// Create the recipe.
		return new FermenterRecipe(recipeId, input, residualOutput, output);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FermenterRecipe recipe) {
		recipe.getInputIngredient().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluidStack());
		recipe.getResidualOutput().writeToBuffer(buffer);
	}
}
