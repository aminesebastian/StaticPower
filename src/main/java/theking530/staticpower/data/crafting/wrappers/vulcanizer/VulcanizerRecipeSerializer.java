package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class VulcanizerRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<VulcanizerRecipe> {
	public static final VulcanizerRecipeSerializer INSTANCE = new VulcanizerRecipeSerializer();

	private VulcanizerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "vulcanizer_recipe"));
	}

	@Override
	public VulcanizerRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.vulcanizerPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.vulcanizerProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the input fluid.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		FluidStack input = StaticPowerJsonParsingUtilities.parseFluidStack(inputElement);

		// Check the output and make a new recipe.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");

		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement.getAsJsonObject());
		return new VulcanizerRecipe(recipeId, processingTime, powerCost, input, output);
	}

	@Override
	public VulcanizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		// Start with the default processing values.
		long power = buffer.readLong();
		int processingTime = buffer.readInt();
		FluidStack input = buffer.readFluidStack();
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		return new VulcanizerRecipe(recipeId, processingTime, power, input, output);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, VulcanizerRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFluidStack(recipe.getInputFluid());
		recipe.getOutput().writeToBuffer(buffer);
	}
}
