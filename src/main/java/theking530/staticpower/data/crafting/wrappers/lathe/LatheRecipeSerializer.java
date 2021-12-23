package theking530.staticpower.data.crafting.wrappers.lathe;

import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class LatheRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<LatheRecipe> {
	public static final LatheRecipeSerializer INSTANCE = new LatheRecipeSerializer();

	private LatheRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "lathe_recipe"));
	}

	@Override
	public LatheRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredients.
		Map<String, StaticPowerIngredient> map = LatheRecipe.deserializeKey(GsonHelper.getAsJsonObject(json, "key"));
		String[] astring = LatheRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern"));
		int width = astring[0].length();
		int height = astring.length;
		NonNullList<StaticPowerIngredient> inputs = LatheRecipe.deserializeIngredients(astring, map, width, height);

		// Start with the default values.
		long powerCost = StaticPowerConfig.SERVER.lathePowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.latheProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

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
		return new LatheRecipe(recipeId, width, height, inputs, primaryOutput, secondaryOutput, fluidOutput, processingTime, powerCost);
	}

	@Override
	public LatheRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		int width = buffer.readVarInt();
		int height = buffer.readVarInt();

		NonNullList<StaticPowerIngredient> nonnulllist = NonNullList.withSize(width * height, StaticPowerIngredient.EMPTY);
		for (int k = 0; k < nonnulllist.size(); ++k) {
			nonnulllist.set(k, StaticPowerIngredient.read(buffer));
		}

		ProbabilityItemStackOutput primary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		ProbabilityItemStackOutput secondary = ProbabilityItemStackOutput.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LatheRecipe(recipeId, width, height, nonnulllist, primary, secondary, outFluid, time, power);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LatheRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeVarInt(recipe.recipeWidth);
		buffer.writeVarInt(recipe.recipeHeight);

		for (StaticPowerIngredient ing : recipe.getInputs()) {
			ing.write(buffer);
		}

		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
