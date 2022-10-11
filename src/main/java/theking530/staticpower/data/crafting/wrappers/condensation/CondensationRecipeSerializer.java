package theking530.staticpower.data.crafting.wrappers.condensation;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.nonpowered.condenser.BlockEntityCondenser;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class CondensationRecipeSerializer extends StaticPowerRecipeSerializer<CondensationRecipe> {
	public static final CondensationRecipeSerializer INSTANCE = new CondensationRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "condensation_recipe");

	@Override
	public CondensationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = GsonHelper.getAsJsonObject(json, "input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = GsonHelper.getAsJsonObject(json, "output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Capture the heat cost.
		int heatGeneration = BlockEntityCondenser.DEFAULT_HEAT_GENERATION;
		if (GsonHelper.isValidNode(json, "heat")) {
			heatGeneration = json.get("heat").getAsInt();
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(() -> BlockEntityCondenser.DEFAULT_PROCESSING_TIME, () -> 0.0, json);
		// Create the recipe.
		return new CondensationRecipe(recipeId, inputFluid, outputFluid, heatGeneration, processing);
	}

	@Override
	public CondensationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int heat = buffer.readInt();
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new CondensationRecipe(recipeId, input, output, heat, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CondensationRecipe recipe) {
		buffer.writeInt(recipe.getHeatGeneration());
		buffer.writeFluidStack(recipe.getInputFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
