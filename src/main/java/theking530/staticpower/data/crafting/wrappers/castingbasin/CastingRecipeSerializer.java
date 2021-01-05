package theking530.staticpower.data.crafting.wrappers.castingbasin;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class CastingRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CastingRecipe> {
	public static final CastingRecipeSerializer INSTANCE = new CastingRecipeSerializer();

	private CastingRecipeSerializer() {
		this.setRegistryName(new ResourceLocation(StaticPower.MOD_ID, "casting_recipe"));
	}

	@Override
	public CastingRecipe read(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("input"));

		// Capture the input mold.
		JsonObject moldElement = JSONUtils.getJsonObject(json, "mold");
		Ingredient mold = Ingredient.deserialize(moldElement);

		// Get the output item.
		JsonObject outputElement = JSONUtils.getJsonObject(json, "output");
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement);

		// Start with the default processing values.
		long powerCost = StaticPowerConfig.SERVER.casterPowerUsage.get();
		int processingTime = StaticPowerConfig.SERVER.casterProcessingTime.get();

		// Capture the processing and power costs.
		if (JSONUtils.hasField(json, "processing")) {
			JsonObject processingElement = JSONUtils.getJsonObject(json, "processing");
			powerCost = processingElement.get("power").getAsInt();
			processingTime = processingElement.get("time").getAsInt();
		}

		// Create the recipe.
		return new CastingRecipe(recipeId, processingTime, powerCost, output, fluidInput, mold);
	}

	@Override
	public CastingRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
		long power = buffer.readLong();
		int time = buffer.readInt();
		FluidStack fluidInput = buffer.readFluidStack();
		Ingredient mold = Ingredient.read(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new CastingRecipe(recipeId, time, power, output, fluidInput, mold);
	}

	@Override
	public void write(PacketBuffer buffer, CastingRecipe recipe) {
		buffer.writeLong(recipe.getPowerCost());
		buffer.writeInt(recipe.getProcessingTime());
		buffer.writeFluidStack(recipe.getInputFluid());
		recipe.getRequiredMold().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
	}
}
