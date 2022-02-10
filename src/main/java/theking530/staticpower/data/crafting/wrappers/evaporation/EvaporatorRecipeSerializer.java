package theking530.staticpower.data.crafting.wrappers.evaporation;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.nonpowered.evaporator.TileEntityEvaporator;

public class EvaporatorRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<EvaporatorRecipe> {
	public static final EvaporatorRecipeSerializer INSTANCE = new EvaporatorRecipeSerializer();

	private EvaporatorRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "evaporation_recipe"));
	}

	@Override
	public EvaporatorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = json.getAsJsonObject("input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = json.getAsJsonObject("output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int processingTime = TileEntityEvaporator.DEFAULT_PROCESSING_TIME;
		float heatCost = TileEntityEvaporator.DEFAULT_EVAPORATION_HEAT;

		// Capture the processing and power costs.
		if (GsonHelper.isValidNode(json, "processing")) {
			JsonObject processingElement = json.getAsJsonObject("processing");
			processingTime = processingElement.get("time").getAsInt();
		}

		// Capture the heat cost.
		if (GsonHelper.isValidNode(json, "heat")) {
			heatCost = json.get("heat").getAsFloat();
		}

		// Create the recipe.
		return new EvaporatorRecipe(recipeId, inputFluid, outputFluid, heatCost, processingTime);
	}

	@Override
	public EvaporatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int time = buffer.readInt();
		float heat = buffer.readFloat();
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();
		// Create the recipe.
		return new EvaporatorRecipe(recipeId, input, output, heat, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, EvaporatorRecipe recipe) {
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFloat(recipe.getRequiredHeat());
		buffer.writeFluidStack(recipe.getInputFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
