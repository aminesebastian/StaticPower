package theking530.staticpower.data.crafting.wrappers.lumbermill;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.powered.lumbermill.TileEntityLumberMill;
import theking530.staticpower.utilities.Reference;

public class LumberMillRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<LumberMillRecipe> {
	public static final LumberMillRecipeSerializer INSTANCE = new LumberMillRecipeSerializer();

	private LumberMillRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "lumber_mill_recipe"));
	}

	@Override
	public LumberMillRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input ingredient.
		JsonObject inputElement = JSONUtils.getJsonObject(json, "input");
		Ingredient input = Ingredient.deserialize(inputElement);

		// Start with the default values.
		int powerCost = TileEntityLumberMill.DEFAULT_PROCESSING_COST;
		int processingTime = TileEntityLumberMill.DEFAULT_PROCESSING_TIME;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("time").getAsInt();
			processingTime = processingElement.get("power").getAsInt();
		}

		// Get the outputs.
		JsonObject outputs = JSONUtils.getJsonObject(json, "outputs");
		ProbabilityItemStackOutput primaryOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.getAsJsonObject("primary"));
		ProbabilityItemStackOutput secondaryOutput = ProbabilityItemStackOutput.EMPTY;
		FluidStack fluidOutput = FluidStack.EMPTY;

		// Deserialize the secondary output if it exists.
		if (outputs.has("secondary")) {
			secondaryOutput = ProbabilityItemStackOutput.parseFromJSON(outputs.getAsJsonObject("secondary"));
		}

		// Deserialize the fluid output if it exsists.
		if (outputs.has("fluid")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(outputs.getAsJsonObject("fluid"));
		}

		// Craete the recipe.
		return new LumberMillRecipe(recipeId, input, primaryOutput, secondaryOutput, fluidOutput, processingTime, powerCost);
	}

	@Override
	public LumberMillRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, LumberMillRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
