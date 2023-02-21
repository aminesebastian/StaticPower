package theking530.staticpower.data.crafting.wrappers.lumbermill;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class LumberMillRecipeSerializer extends StaticPowerRecipeSerializer<LumberMillRecipe> {
	public static final LumberMillRecipeSerializer INSTANCE = new LumberMillRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "lumber_mill_recipe");

	@Override
	public LumberMillRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = GsonHelper.getAsJsonObject(json, "input");
		StaticPowerIngredient input = StaticPowerIngredient.deserialize(inputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.lumberMillProcessingTime,
				StaticPowerConfig.SERVER.lumberMillPowerUsage, json);

		// Get the outputs.
		JsonObject outputs = GsonHelper.getAsJsonObject(json, "outputs");
		StaticPowerOutputItem primaryOutput = StaticPowerOutputItem.parseFromJSON(outputs.getAsJsonObject("primary"));
		StaticPowerOutputItem secondaryOutput = StaticPowerOutputItem.EMPTY;
		FluidStack fluidOutput = FluidStack.EMPTY;

		// Deserialize the secondary output if it exists.
		if (outputs.has("secondary")) {
			secondaryOutput = StaticPowerOutputItem.parseFromJSON(outputs.getAsJsonObject("secondary"));
		}

		// Deserialize the fluid output if it exsists.
		if (outputs.has("fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputs.getAsJsonObject("fluid"));
		}

		// Craete the recipe.
		return new LumberMillRecipe(recipeId, input, primaryOutput, secondaryOutput, fluidOutput, processing);
	}

	@Override
	public LumberMillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem primary = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem secondary = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LumberMillRecipe(recipeId, input, primary, secondary, outFluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LumberMillRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
