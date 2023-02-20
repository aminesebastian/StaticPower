package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class VulcanizerRecipeSerializer extends StaticPowerRecipeSerializer<VulcanizerRecipe> {
	public static final VulcanizerRecipeSerializer INSTANCE = new VulcanizerRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "vulcanizer_recipe");

	@Override
	public VulcanizerRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.vulcanizerProcessingTime,
				StaticPowerConfig.SERVER.vulcanizerPowerUsage, json);

		// Get the input fluid.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		FluidStack input = StaticPowerJsonParsingUtilities.parseFluidStack(inputElement);

		// Check the output and make a new recipe.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");

		StaticPowerOutputItem output = StaticPowerOutputItem.parseFromJSON(outputElement.getAsJsonObject());
		return new VulcanizerRecipe(recipeId, input, output, processing);
	}

	@Override
	public VulcanizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidStack input = buffer.readFluidStack();
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		return new VulcanizerRecipe(recipeId, input, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, VulcanizerRecipe recipe) {
		buffer.writeFluidStack(recipe.getInputFluid());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
