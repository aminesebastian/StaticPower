package theking530.staticpower.data.crafting.wrappers.castingbasin;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class CastingRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CastingRecipe> {
	public static final CastingRecipeSerializer INSTANCE = new CastingRecipeSerializer();

	private CastingRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "casting_recipe"));
	}

	@Override
	public CastingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("input"));

		// Capture the input mold.
		JsonObject moldElement = GsonHelper.getAsJsonObject(json, "mold");
		Ingredient mold = Ingredient.fromJson(moldElement);

		// Get the output item.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement);

		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.casterPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.casterProcessingTime.get();

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new CastingRecipe(recipeId, processingTime, powerCost, output, fluidInput, mold);
	}

	@Override
	public CastingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		FluidStack fluidInput = buffer.readFluidStack();
		Ingredient mold = Ingredient.fromNetwork(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new CastingRecipe(recipeId, time, power, output, fluidInput, mold);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CastingRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFluidStack(recipe.getInputFluid());
		recipe.getRequiredMold().toNetwork(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}
