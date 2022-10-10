package theking530.staticpower.data.crafting.wrappers.alloyfurnace;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class AlloyFurnaceRecipeSerializer extends StaticPowerRecipeSerializer<AlloyFurnaceRecipe> {
	public static final AlloyFurnaceRecipeSerializer INSTANCE = new AlloyFurnaceRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "alloy_furnace_recipe");

	@Override
	public AlloyFurnaceRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		StaticPowerIngredient input1 = StaticPowerIngredient.deserialize(json.get("input1"));
		StaticPowerIngredient input2 = StaticPowerIngredient.deserialize(json.get("input2"));

		int experience = 0;
		if (json.has("experience")) {
			experience = json.get("experience").getAsInt();
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.alloyFurnaceProcessingTime.get(), json);

		// Get the output.
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(json.getAsJsonObject("output"));

		// Craete the recipe.
		return new AlloyFurnaceRecipe(recipeId, input1, input2, output, experience, processing);
	}

	@Override
	public AlloyFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input1 = StaticPowerIngredient.read(buffer);
		StaticPowerIngredient input2 = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		int experience = buffer.readInt();
		return new AlloyFurnaceRecipe(recipeId, input1, input2, output, experience, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, AlloyFurnaceRecipe recipe) {
		recipe.getInput1().write(buffer);
		recipe.getInput2().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
		buffer.writeInt(recipe.getExperience());
	}
}
