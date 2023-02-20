package theking530.staticpower.data.crafting.wrappers.mixer;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class MixerRecipeSerializer extends StaticPowerRecipeSerializer<MixerRecipe> {
	public static final MixerRecipeSerializer INSTANCE = new MixerRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "mixer_recipe");

	@Override
	public MixerRecipe parse(ResourceLocation recipeId, JsonObject json) {
		StaticPowerIngredient input1 = StaticPowerIngredient.EMPTY;
		StaticPowerIngredient input2 = StaticPowerIngredient.EMPTY;
		FluidStack fluidInput1 = FluidStack.EMPTY;
		FluidStack fluidInput2 = FluidStack.EMPTY;

		// Capture the input ingredients.
		if (json.has("item_input_1")) {
			input1 = StaticPowerIngredient.deserialize(json.get("item_input_1"));
		}
		if (json.has("item_input_2")) {
			input2 = StaticPowerIngredient.deserialize(json.get("item_input_2"));
		}

		// Capture the input fluids.
		if (json.has("fluid_input_1")) {
			fluidInput1 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fluid_input_1"));
		}
		if (json.has("fluid_input_2")) {
			fluidInput2 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fluid_input_2"));
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.mixerProcessingTime, StaticPowerConfig.SERVER.mixerPowerUsage,
				json);

		// Get the fluid result.
		FluidStack output = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("result"));

		// Create the recipe.
		return new MixerRecipe(recipeId, input1, input2, fluidInput1, fluidInput2, output, processing);
	}

	@Override
	public MixerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input1 = StaticPowerIngredient.read(buffer);
		StaticPowerIngredient input2 = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput1 = buffer.readFluidStack();
		FluidStack fluidInput2 = buffer.readFluidStack();
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new MixerRecipe(recipeId, input1, input2, fluidInput1, fluidInput2, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, MixerRecipe recipe) {
		recipe.getPrimaryItemInput().write(buffer);
		recipe.getSecondaryItemInput().write(buffer);
		buffer.writeFluidStack(recipe.getPrimaryFluidInput());
		buffer.writeFluidStack(recipe.getSecondaryFluidInput());
		buffer.writeFluidStack(recipe.getOutput());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
