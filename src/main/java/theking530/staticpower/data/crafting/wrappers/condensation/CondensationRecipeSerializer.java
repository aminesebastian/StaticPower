package theking530.staticpower.data.crafting.wrappers.condensation;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.nonpowered.condenser.TileEntityCondenser;

public class CondensationRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CondensationRecipe> {
	public static final CondensationRecipeSerializer INSTANCE = new CondensationRecipeSerializer();

	private CondensationRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "condensation_recipe"));
	}

	@Override
	public CondensationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = GsonHelper.getAsJsonObject(json, "input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = GsonHelper.getAsJsonObject(json, "output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int processingTime = TileEntityCondenser.DEFAULT_PROCESSING_TIME;

		// Capture the heat cost.
		float heatGeneration = TileEntityCondenser.DEFAULT_HEAT_GENERATION;
		if (GsonHelper.isValidNode(json, "heat")) {
			heatGeneration = json.get("heat").getAsFloat();
		}

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = GsonHelper.getAsJsonObject(json, "processing");
			processingTime = processingElement.get("power").getAsInt();
		}

		// Create the recipe.
		return new CondensationRecipe(recipeId, inputFluid, outputFluid, processingTime, heatGeneration);
	}

	@Override
	public CondensationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int time = buffer.readInt();
		float heat = buffer.readFloat();
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new CondensationRecipe(recipeId, input, output, time, heat);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CondensationRecipe recipe) {
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFloat(recipe.getHeatGeneration());
		buffer.writeFluidStack(recipe.getInputFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
