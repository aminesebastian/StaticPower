package theking530.staticpower.data.crafting.wrappers.evaporation;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.nonpowered.evaporator.TileEntityEvaporator;
import theking530.staticpower.utilities.Reference;

public class EvaporatorRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EvaporatorRecipe> {
	public static final EvaporatorRecipeSerializer INSTANCE = new EvaporatorRecipeSerializer();

	private EvaporatorRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "evaporation_recipe"));
	}

	@Override
	public EvaporatorRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = JSONUtils.getJsonObject(json, "input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = JSONUtils.getJsonObject(json, "output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int processingTime = TileEntityEvaporator.DEFAULT_PROCESSING_TIME;
		float heatCost = TileEntityEvaporator.DEFAULT_EVAPORATION_HEAT;

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			processingTime = processingElement.get("time").getAsInt();
		}

		// Capture the heat cost.
		if (JSONUtils.hasField(json, "heat")) {
			heatCost = JSONUtils.getJsonObject(json, "heat").getAsFloat();
		}
		// Create the recipe.
		return new EvaporatorRecipe(recipeId, inputFluid, outputFluid, heatCost, processingTime);
	}

	@Override
	public EvaporatorRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, EvaporatorRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
