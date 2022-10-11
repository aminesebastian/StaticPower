package theking530.staticpower.data.crafting.wrappers.cauldron;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class CauldronRecipeSerializer extends StaticPowerRecipeSerializer<CauldronRecipe> {
	public static final CauldronRecipeSerializer INSTANCE = new CauldronRecipeSerializer();
	private static final Logger LOGGER = LogManager.getLogger(CauldronRecipeSerializer.class);
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "cauldron_recipe");

	@Override
	public CauldronRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a cauldron recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Get the item output.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(GsonHelper.getAsJsonObject(json, "output"));

		// Get how long the item needs to be in the cauldron.
		int cauldronTime = json.get("time").getAsInt();

		// Indicates if the cauldron should empty after.
		boolean shouldDrainCauldron = false;
		if (json.has("empty_after_craft")) {
			shouldDrainCauldron = json.get("empty_after_craft").getAsBoolean();
		}

		// Deserialize the fluid input and output.
		FluidStack fluidInput = FluidStack.EMPTY;
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (json.has("fluid")) {
			fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "fluid"));
		}
		if (json.has("output_fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "output_fluid"));
		}

		// Create the recipe.
		return new CauldronRecipe(recipeId, input, itemOutput, fluidInput, fluidOutput, shouldDrainCauldron, cauldronTime);
	}

	@Override
	public CauldronRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int time = buffer.readInt();
		boolean shouldDrainCauldron = buffer.readBoolean();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack fluidInput = buffer.readFluidStack();
		FluidStack fluidOutput = buffer.readFluidStack();

		// Create the recipe.
		return new CauldronRecipe(recipeId, input, output, fluidInput, fluidOutput, shouldDrainCauldron, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CauldronRecipe recipe) {
		buffer.writeInt(recipe.getRequiredTimeInCauldron());
		buffer.writeBoolean(recipe.shouldDrainCauldron());
		recipe.getInput().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getRequiredFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
	
	}
}
