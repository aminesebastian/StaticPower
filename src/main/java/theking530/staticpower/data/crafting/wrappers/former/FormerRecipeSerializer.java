package theking530.staticpower.data.crafting.wrappers.former;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
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

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.formerProcessingTime.get(), StaticPowerConfig.SERVER.formerPowerUsage.get(), json);

		// Create the recipe.
		return new FormerRecipe(recipeId, output, input, mold, processing);
	}

	@Override
	public FormerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		Ingredient mold = Ingredient.fromNetwork(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

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
