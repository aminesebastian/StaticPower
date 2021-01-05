package theking530.staticpower.data.crafting.wrappers.former;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FormerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FormerRecipe> {
	public static final FormerRecipeSerializer INSTANCE = new FormerRecipeSerializer();

	private FormerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "former_recipe"));
	}

	@Override
	public FormerRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the input mold.
		JsonObject moldElement = JSONUtils.getJsonObject(json, "mold");
		Ingredient mold = Ingredient.deserialize(moldElement);

		// Get the output item.
		JsonObject outputElement = JSONUtils.getJsonObject(json, "output");
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement);

		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.formerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.formerProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new FormerRecipe(recipeId, processingTime, powerCost, output, input, mold);
	}

	@Override
	public FormerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		Ingredient mold = Ingredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new FormerRecipe(recipeId, time, power, output, input, mold);
	}

	@Override
	public void write(PacketBuffer buffer, FormerRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInputIngredient().write(buffer);
		recipe.getRequiredMold().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}
