package theking530.staticpower.data.crafting.wrappers.grinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class GrinderRecipeSerializer extends StaticPowerRecipeSerializer<GrinderRecipe> {
	public static final GrinderRecipeSerializer INSTANCE = new GrinderRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "grinder_recipe");

	@Override
	public GrinderRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.poweredGrinderProcessingTime.get(),
				StaticPowerConfig.SERVER.poweredGrinderPowerUsage.get(), json);

		// Check the outputs. If it is an array, get all the outputs and make a new
		// recipe. Otherwise, just get the single output and make a new recipe.
		JsonElement outputElement = GsonHelper.isArrayNode(json, "output") ? GsonHelper.getAsJsonArray(json, "output") : GsonHelper.getAsJsonObject(json, "output");
		if (outputElement.isJsonArray()) {
			JsonArray outputArray = outputElement.getAsJsonArray();
			ProbabilityItemStackOutput[] outputs = new ProbabilityItemStackOutput[outputArray.size()];
			for (int i = 0; i < outputArray.size(); i++) {
				outputs[i] = ProbabilityItemStackOutput.parseFromJSON(outputArray.get(i).getAsJsonObject());
			}
			return new GrinderRecipe(recipeId, input, processing, outputs);
		} else {
			ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement.getAsJsonObject());
			return new GrinderRecipe(recipeId, input, processing, output);
		}

	}

	@Override
	public GrinderRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		int outputCount = buffer.readByte();
		ProbabilityItemStackOutput[] outputs = new ProbabilityItemStackOutput[outputCount];
		for (int i = 0; i < outputCount; i++) {
			outputs[i] = ProbabilityItemStackOutput.readFromBuffer(buffer);
		}
		return new GrinderRecipe(recipeId, input, MachineRecipeProcessingSection.fromBuffer(buffer), outputs);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, GrinderRecipe recipe) {
		recipe.getInputIngredient().write(buffer);
		buffer.writeByte(recipe.getOutputItems().length);
		for (int i = 0; i < recipe.getOutputItems().length; i++) {
			recipe.getOutputItems()[i].writeToBuffer(buffer);
		}
		recipe.getProcessingSection().writeToBuffer(buffer);
	}

	@Override
	public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
		return INSTANCE;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return ID;
	}
}
