package theking530.staticpower.data.crafting.wrappers.hydroponicfarming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class HydroponicFarmingRecipeSerializer extends StaticPowerRecipeSerializer<HydroponicFarmingRecipe> {
	public static final HydroponicFarmingRecipeSerializer INSTANCE = new HydroponicFarmingRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "hydroponic_farmer_recipe");
	private static final Logger LOGGER = LogManager.getLogger(HydroponicFarmingRecipeSerializer.class);

	@Override
	public HydroponicFarmingRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Return null if the input is empty.
		if (input.isEmpty()) {
			LOGGER.error(String.format("Encounetered a hydroponic farmer recipe with no input defined...skipping. %1$s", recipeId));
			return null;
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.hydroponicFarmerProcessingTime,
				StaticPowerConfig.SERVER.hydroponicFarmerPowerUsage, json);

		// Create the recipe.
		return new HydroponicFarmingRecipe(recipeId, input, processing);
	}

	@Override
	public HydroponicFarmingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		return new HydroponicFarmingRecipe(recipeId, input, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, HydroponicFarmingRecipe recipe) {
		recipe.getInput().write(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
