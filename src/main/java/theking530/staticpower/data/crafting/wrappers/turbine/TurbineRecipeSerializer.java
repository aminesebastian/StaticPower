package theking530.staticpower.data.crafting.wrappers.turbine;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class TurbineRecipeSerializer extends StaticPowerRecipeSerializer<TurbineRecipe> {
	public static final TurbineRecipeSerializer INSTANCE = new TurbineRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "turbine_recipe");

	@Override
	public TurbineRecipe parse(ResourceLocation recipeId, JsonObject json) {
		// Deserialize the fluid input..
		FluidStack fluidInput = FluidStack.EMPTY;
		if (GsonHelper.isValidNode(json, "input")) {
			fluidInput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "input"));
		}

		// Deserialize the fluid output if it exists.
		FluidStack fluidOutput = FluidStack.EMPTY;
		if (GsonHelper.isValidNode(json, "output")) {
			fluidOutput = StaticPowerJsonParsingUtilities.parseFluidStack(GsonHelper.getAsJsonObject(json, "output"));
		}

		// Get the generation amount.
		int generationAmount = json.get("generation").getAsInt();

		return new TurbineRecipe(recipeId, fluidInput, fluidOutput, generationAmount);
	}

	@Override
	public TurbineRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidStack input = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();
		int generation = buffer.readInt();
		return new TurbineRecipe(recipeId, input, output, generation);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, TurbineRecipe recipe) {
		buffer.writeFluidStack(recipe.getInput());
		buffer.writeFluidStack(recipe.getOutput());
		buffer.writeInt(recipe.getGenerationAmount());
	}
}
