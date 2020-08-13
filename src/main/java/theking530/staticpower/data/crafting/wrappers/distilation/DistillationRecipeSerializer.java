package theking530.staticpower.data.crafting.wrappers.distilation;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.tileentities.nonpowered.distillery.TileEntityDistillery;
import theking530.staticpower.utilities.Reference;

public class DistillationRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DistillationRecipe> {
	public static final DistillationRecipeSerializer INSTANCE = new DistillationRecipeSerializer();

	private DistillationRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(Reference.MOD_ID, "distillation_recipe"));
	}

	@Override
	public DistillationRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		JsonObject inputFluidObject = JSONUtils.getJsonObject(json, "input_fluid");
		FluidStack inputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(inputFluidObject);

		// Capture the output fluid.
		JsonObject outputFluidObject = JSONUtils.getJsonObject(json, "output_fluid");
		FluidStack outputFluid = StaticPowerJsonParsingUtilities.parseFluidStack(outputFluidObject);

		// Start with the default processing values.
		int processingTime = TileEntityDistillery.DEFAULT_PROCESSING_TIME;
		float heatCost = TileEntityDistillery.DEFAULT_EVAPORATION_HEAT;

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
		return new DistillationRecipe(recipeId, inputFluid, outputFluid, heatCost, processingTime);
	}

	@Override
	public DistillationRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write(PacketBuffer buffer, DistillationRecipe recipe) {
		// TODO Auto-generated method stub

	}
}
