package theking530.staticpower.data.crafting.wrappers.condensation;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.nonpowered.condenser.TileEntityCondenser;

public class CondensationRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CondensationRecipe> {
	public static final CondensationRecipeSerializer INSTANCE = new CondensationRecipeSerializer();

	private CondensationRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "condensation_recipe"));
	}

	@Override
	public CondensationRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = JSONUtils.getJsonObject(json, "input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = JSONUtils.getJsonObject(json, "output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int processingTime = TileEntityCondenser.DEFAULT_PROCESSING_TIME;

		// Capture the heat cost.
		float heatGeneration = TileEntityCondenser.DEFAULT_HEAT_GENERATION;
		if (JSONUtils.hasField(json, "heat")) {
			heatGeneration = json.get("heat").getAsFloat();
		}

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			processingTime = processingElement.get("power").getAsInt();
		}

		// Create the recipe.
		return new CondensationRecipe(recipeId, inputFluid, outputFluid, processingTime, heatGeneration);
	}

	@Override
	public CondensationRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		int time = buffer.readInt();
		float heat = buffer.readFloat();
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new CondensationRecipe(recipeId, input, output, time, heat);
	}

	@Override
	public void write(PacketBuffer buffer, CondensationRecipe recipe) {
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFloat(recipe.getHeatGeneration());
		buffer.writeFluidStack(recipe.getInputFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
	}
}
