package theking530.staticpower.data.crafting.wrappers.centrifuge;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.tileentities.powered.centrifuge.TileEntityCentrifuge;
import theking530.staticpower.utilities.Reference;

public class CentrifugeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CentrifugeRecipe> {
	public static final CentrifugeRecipeSerializer INSTANCE = new CentrifugeRecipeSerializer();

	private CentrifugeRecipeSerializer() {
		setRegistryName(new ResourceLocation(Reference.MOD_ID, "centrifuge_recipe"));
	}

	@Override
	public CentrifugeRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Get the minimum speed.
		int minimumSpeed = json.get("minimum_speed").getAsInt();

		// Start with the default values.
		int powerCost = TileEntityCentrifuge.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityCentrifuge.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("time").getAsInt();
			processingTime = processingElement.get("power").getAsInt();
		}

		// Get the outputs.
		JsonArray outputs = json.getAsJsonArray("outputs");

		// Deserialize the first output and allocate the second and third.
		ProbabilityItemStackOutput firstOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.get(0).getAsJsonObject());
		ProbabilityItemStackOutput secondOutput = ProbabilityItemStackOutput.EMPTY;
		ProbabilityItemStackOutput thirdOutput = ProbabilityItemStackOutput.EMPTY;

		// If there is a second output, deserialize it.
		if (outputs.size() > 1) {
			secondOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.get(1).getAsJsonObject());
		}

		// If there is a third output, deserialize it.
		if (outputs.size() > 2) {
			thirdOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.get(2).getAsJsonObject());
		}

		// Create the recipe.
		return new CentrifugeRecipe(recipeId, processingTime, powerCost, input, firstOutput, secondOutput, thirdOutput, minimumSpeed);
	}

	@Override
	public CentrifugeRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, CentrifugeRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
