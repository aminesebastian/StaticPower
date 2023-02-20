package theking530.staticpower.data.crafting.wrappers.evaporation;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.blockentities.nonpowered.evaporator.BlockEntityEvaporator;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class EvaporatorRecipeSerializer extends StaticPowerRecipeSerializer<EvaporatorRecipe> {
	public static final EvaporatorRecipeSerializer INSTANCE = new EvaporatorRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "evaporation_recipe");

	@Override
	public EvaporatorRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = json.getAsJsonObject("input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = json.getAsJsonObject("output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int heatCost = BlockEntityEvaporator.DEFAULT_EVAPORATION_HEAT;

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(() -> BlockEntityEvaporator.DEFAULT_PROCESSING_TIME, () -> 0.0, json);

		// Capture the heat cost.
		if (GsonHelper.isValidNode(json, "heat")) {
			heatCost = json.get("heat").getAsInt();
		}

		// Create the recipe.
		return new EvaporatorRecipe(recipeId, inputFluid, outputFluid, heatCost, processing);
	}

	@Override
	public EvaporatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int heat = buffer.readInt();
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();
		// Create the recipe.
		return new EvaporatorRecipe(recipeId, input, output, heat, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, EvaporatorRecipe recipe) {
		buffer.writeInt(recipe.getRequiredHeat());
		buffer.writeFluidStack(recipe.getInputFluid());
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
