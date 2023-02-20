package theking530.staticpower.data.crafting.wrappers.former;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FormerRecipeSerializer extends StaticPowerRecipeSerializer<FormerRecipe> {
	public static final FormerRecipeSerializer INSTANCE = new FormerRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "former_recipe");

	@Override
	public FormerRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the input mold.
		JsonObject moldElement = GsonHelper.getAsJsonObject(json, "mold");
		Ingredient mold = Ingredient.fromJson(moldElement);

		// Get the output item.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");
		StaticPowerOutputItem output = StaticPowerOutputItem.parseFromJSON(outputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.formerProcessingTime,
				StaticPowerConfig.SERVER.formerPowerUsage, json);

		// Create the recipe.
		return new FormerRecipe(recipeId, output, input, mold, processing);
	}

	@Override
	public FormerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		Ingredient mold = Ingredient.fromNetwork(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new FormerRecipe(recipeId, output, input, mold, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FormerRecipe recipe) {
		recipe.getInputIngredient().write(buffer);
		recipe.getRequiredMold().toNetwork(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
