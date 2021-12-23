package theking530.staticpower.data.crafting.wrappers.former;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class FormerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<FormerRecipe> {
	public static final FormerRecipeSerializer INSTANCE = new FormerRecipeSerializer();

	private FormerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "former_recipe"));
	}

	@Override
	public FormerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the input mold.
		JsonObject moldElement = GsonHelper.getAsJsonObject(json, "mold");
		Ingredient mold = Ingredient.fromJson(moldElement);

		// Get the output item.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement);

		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.formerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.formerProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new FormerRecipe(recipeId, processingTime, powerCost, output, input, mold);
	}

	@Override
	public FormerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		Ingredient mold = Ingredient.fromNetwork(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new FormerRecipe(recipeId, time, power, output, input, mold);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FormerRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		recipe.getInputIngredient().write(buffer);
		recipe.getRequiredMold().toNetwork(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}
