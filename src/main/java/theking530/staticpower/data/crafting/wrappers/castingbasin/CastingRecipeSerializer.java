package theking530.staticpower.data.crafting.wrappers.castingbasin;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistryEntry;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;

public class CastingRecipeSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<CastingRecipe> {
	public static final CastingRecipeSerializer INSTANCE = new CastingRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "casting_recipe");

	@Override
	public CastingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		// Capture the input fluid.
		FluidStack fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("input"));

		// Capture the input mold.
		JsonObject moldElement = GsonHelper.getAsJsonObject(json, "mold");
		Ingredient mold = Ingredient.fromJson(moldElement);

		// Get the output item.
		JsonObject outputElement = GsonHelper.getAsJsonObject(json, "output");
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.parseFromJSON(outputElement);

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.casterProcessingTime.get(), StaticPowerConfig.SERVER.casterPowerUsage.get(), json);

		// Create the recipe.
		return new CastingRecipe(recipeId, output, fluidInput, mold, processing);
	}

	@Override
	public CastingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidStack fluidInput = buffer.readFluidStack();
		Ingredient mold = Ingredient.fromNetwork(buffer);
		ProbabilityItemStackOutput output = ProbabilityItemStackOutput.readFromBuffer(buffer);

		// Create the recipe.
		return new CastingRecipe(recipeId, output, fluidInput, mold, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CastingRecipe recipe) {
		buffer.writeFluidStack(recipe.getInputFluid());
		recipe.getRequiredMold().toNetwork(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
