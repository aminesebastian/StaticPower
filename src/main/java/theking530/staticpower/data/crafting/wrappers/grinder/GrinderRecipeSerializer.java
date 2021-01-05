package theking530.staticpower.data.crafting.wrappers.grinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class GrinderRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrinderRecipe> {
	public static final GrinderRecipeSerializer INSTANCE = new GrinderRecipeSerializer();

	private GrinderRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "grinder_recipe"));
	}

	@Override
	public GrinderRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.poweredGrinderPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.poweredGrinderProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Check the outputs. If it is an array, get all the outputs and make a new
		// recipe. Otherwise, just get the single output and make a new recipe.
		JsonElement outputElement = JSONUtils.isJsonArray(json, "output") ? JSONUtils.getJsonArray(json, "output") : JSONUtils.getJsonObject(json, "output");
		if (outputElement.isJsonArray()) {
			JsonArray outputArray = outputElement.getAsJsonArray();
			ProbabilityItemStackOutput[] outputs = new ProbabilityItemStackOutput[outputArray.size()];
			for (int i = 0; i < outputArray.size(); i++) {
				outputs[i] = ProbabilityItemStackOutput.parseFromJSON(outputArray.get(i).getAsJsonObject());
			}
			return new GrinderRecipe(recipeId, processingTime, powerCost, input, outputs);
		} else {
			ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement.getAsJsonObject());
			return new GrinderRecipe(recipeId, processingTime, powerCost, input, output);
		}

	}

	@Override
	public GrinderRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		int outputCount = buffer.readByte();
		ProbabilityItemStackOutput[] outputs = new ProbabilityItemStackOutput[outputCount];
		for (int i = 0; i < outputCount; i++) {
			outputs[i] = ProbabilityItemStackOutput.readFromBuffer(buffer);
		}
		return new GrinderRecipe(recipeId, time, power, input, outputs);
	}

	@Override
	public void write(PacketBuffer buffer, GrinderRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInputIngredient().write(buffer);
		buffer.writeByte(recipe.getOutputItems().length);
		for (int i = 0; i < recipe.getOutputItems().length; i++) {
			recipe.getOutputItems()[i].writeToBuffer(buffer);
		}
	}
}
