package theking530.staticpower.data.crafting.wrappers.fusionfurnace;

import java.util.ArrayList;
import java.util.List;

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
import theking530.staticpower.tileentities.powered.fusionfurnace.TileEntityFusionFurnace;
import theking530.staticpower.utilities.Reference;

public class FusionFurnaceRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FusionFurnaceRecipe> {
	public static final FusionFurnaceRecipeSerializer INSTANCE = new FusionFurnaceRecipeSerializer();

	private FusionFurnaceRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "fusion_furnace_recipe"));
	}

	@Override
	public FusionFurnaceRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonArray inputElement = json.getAsJsonArray("inputs");
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();

		// Deserialize the input ingredients.
		for (JsonElement element : inputElement) {
			StaticPowerIngredient input = StaticPowerIngredient.deserialize(element);
			inputs.add(input);
		}

		// Start with the default values.
		int powerCost = TileEntityFusionFurnace.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityFusionFurnace.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("time").getAsInt();
			processingTime = processingElement.get("power").getAsInt();
		}

		// Get the output.
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(json.getAsJsonObject("output"));

		// Craete the recipe.
		return new FusionFurnaceRecipe(recipeId, processingTime, powerCost, inputs, output);
	}

	@Override
	public FusionFurnaceRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, FusionFurnaceRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
