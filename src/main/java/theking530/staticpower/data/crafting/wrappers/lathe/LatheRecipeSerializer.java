package theking530.staticpower.data.crafting.wrappers.lathe;

import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class LatheRecipeSerializer extends StaticPowerRecipeSerializer<LatheRecipe> {
	public static final LatheRecipeSerializer INSTANCE = new LatheRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "lathe_recipe");

	@Override
	public LatheRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredients.
		Map<String, StaticPowerIngredient> map = LatheRecipe.deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
		String[] astring = LatheRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));
		int width = astring[0].length();
		int height = astring.length;
		NonNullList<StaticPowerIngredient> inputs = LatheRecipe.deserializeIngredients(astring, map, width, height);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.latheProcessingTime, StaticPowerConfig.SERVER.lathePowerUsage,
				json);

		// Get the outputs.
		JsonObject outputs = GsonHelper.getAsJsonObject(json, "outputs");
		ProbabilityItemStackOutput primaryOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.getAsJsonObject("primary"));
		ProbabilityItemStackOutput secondaryOutput = ProbabilityItemStackOutput.EMPTY;
		FluidStack fluidOutput = FluidStack.EMPTY;

		// Deserialize the secondary output if it exists.
		if (outputs.has("secondary")) {
			secondaryOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.getAsJsonObject("secondary"));
		}

		// Deserialize the fluid output if it exists.
		if (outputs.has("fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputs.getAsJsonObject("fluid"));
		}

		// Create the recipe.
		return new LatheRecipe(recipeId, width, height, inputs, primaryOutput, secondaryOutput, fluidOutput, processing);
	}

	@Override
	public LatheRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int width = buffer.readVarInt();
		int height = buffer.readVarInt();

		NonNullList<StaticPowerIngredient> nonnulllist = NonNullList.withSize(width * height, StaticPowerIngredient.EMPTY);
		for (int k = 0; k < nonnulllist.size(); ++k) {
			nonnulllist.set(k, StaticPowerIngredient.read(buffer));
		}

		ProbabilityItemStackOutput primary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput secondary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LatheRecipe(recipeId, width, height, nonnulllist, primary, secondary, outFluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LatheRecipe recipe) {
		buffer.writeVarInt(recipe.recipeWidth);
		buffer.writeVarInt(recipe.recipeHeight);

		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.write(buffer);
		}

		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
