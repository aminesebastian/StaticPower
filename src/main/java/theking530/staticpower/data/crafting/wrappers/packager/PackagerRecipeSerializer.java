package theking530.staticpower.data.crafting.wrappers.packager;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;

public class PackagerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<PackagerRecipe> {
	public static final PackagerRecipeSerializer INSTANCE = new PackagerRecipeSerializer();

	private PackagerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "packager_recipe"));
	}

	@Override
	public PackagerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.packagerProcessingTime.get(), StaticPowerConfig.SERVER.packagerPowerUsage.get(),
				json);

		// Get the recipe size.
		int size = json.get("size").getAsInt();

		// Get the item output if one is defined.
		ProbabilityItemStackOutput itemOutput = ProbabilityItemStackOutput.parseFromJSON(json.get("output").getAsJsonObject());

		// Create the recipe.
		return new PackagerRecipe(recipeId, size, input, itemOutput, processing);
	}

	@Override
	public PackagerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int size = buffer.readInt();
		StaticPowerIngredient input = StaticPowerIngredient.read(buffer);
		ProbabilityItemStackOutput outputs = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new PackagerRecipe(recipeId, size, input, outputs, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, PackagerRecipe recipe) {
		buffer.writeInt(recipe.getSize());
		recipe.getInputIngredient().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
