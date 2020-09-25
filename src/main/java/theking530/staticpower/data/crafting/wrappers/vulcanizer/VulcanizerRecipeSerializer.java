package theking530.staticpower.data.crafting.wrappers.vulcanizer;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.powered.poweredgrinder.TileEntityPoweredGrinder;

public class VulcanizerRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<VulcanizerRecipe> {
	public static final VulcanizerRecipeSerializer INSTANCE = new VulcanizerRecipeSerializer();

	private VulcanizerRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "vulcanizer_recipe"));
	}

	@Override
	public VulcanizerRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Start with the default processing values.
		int powerCost = TileEntityPoweredGrinder.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityPoweredGrinder.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Get the input fluid.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		FluidStack input = StaticPowerJsonParsingUtilities.parseFluidStack(inputElement);

		// Check the output and make a new recipe.
		JsonObject outputElement = JSONUtils.getJsonObject(json, "output");

		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement.getAsJsonObject());
		return new VulcanizerRecipe(recipeId, processingTime, powerCost, input, output);
	}

	@Override
	public VulcanizerRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// Start with the default processing values.
		int powerCost = buffer.readInt();
		int processingTime = buffer.readInt();
		FluidStack input = buffer.readFluidStack();
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);
		return new VulcanizerRecipe(recipeId, processingTime, powerCost, input, output);
	}

	@Override
	public void write(PacketBuffer buffer, VulcanizerRecipe recipe) {
		buffer.writeInt(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFluidStack(recipe.getInputFluid());
		recipe.getOutputItem().writeToBuffer(buffer);
	}
}
