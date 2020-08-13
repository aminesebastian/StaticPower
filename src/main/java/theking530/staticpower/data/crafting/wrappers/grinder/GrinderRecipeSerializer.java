package theking530.staticpower.data.crafting.wrappers.grinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.tileentities.powered.poweredgrinder.TileEntityPoweredGrinder;
import theking530.staticpower.utilities.Reference;

public class GrinderRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<GrinderRecipe> {
	public static final GrinderRecipeSerializer INSTANCE = new GrinderRecipeSerializer();

	private GrinderRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "grinder_recipe"));
	}

	@Override
	public GrinderRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Start with the default processing values.
		int powerCost = TileEntityPoweredGrinder.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityPoweredGrinder.DEFAULT_PROCESSING_TIME;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, GrinderRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
