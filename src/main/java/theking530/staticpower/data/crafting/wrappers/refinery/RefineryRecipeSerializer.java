package theking530.staticpower.data.crafting.wrappers.refinery;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.StaticPower;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerJsonParsingUtilities;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class RefineryRecipeSerializer extends StaticPowerRecipeSerializer<RefineryRecipe> {
	public static final RefineryRecipeSerializer INSTANCE = new RefineryRecipeSerializer();
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "refinery_recipe");

	@Override
	public RefineryRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		StaticPowerIngredient catalyst = StaticPowerIngredient.EMPTY;
		FluidStack fluidInput1 = FluidStack.EMPTY;
		FluidStack fluidInput2 = FluidStack.EMPTY;

		// Capture the catalyst.
		if (json.has("catalyst")) {
			catalyst = StaticPowerIngredient.deserialize(json.get("catalyst"));
		}

		// Capture the input fluids.
		if (json.has("fluid_input_1")) {
			fluidInput1 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fluid_input_1"));
		}
		if (json.has("fluid_input_2")) {
			fluidInput2 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("fluid_input_2"));
		}

		// Capture the processing and power costs.
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromJson(StaticPowerConfig.SERVER.refineryProcessingTime.get(),
				StaticPowerConfig.SERVER.refineryPowerUsage.get(), StaticPowerConfig.SERVER.refineryMinimumHeat.get(), StaticPowerConfig.SERVER.refineryHeatUse.get(), json);

		// Get the fluid result.
		FluidStack output1 = FluidStack.EMPTY;
		if (json.has("output_1")) {
			output1 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("output_1"));
		}

		FluidStack output2 = FluidStack.EMPTY;
		if (json.has("output_2")) {
			output2 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("output_2"));
		}

		FluidStack output3 = FluidStack.EMPTY;
		if (json.has("output_3")) {
			output3 = StaticPowerJsonParsingUtilities.parseFluidStack(json.getAsJsonObject("output_3"));
		}

		// Create the recipe.
		return new RefineryRecipe(recipeId, catalyst, fluidInput1, fluidInput2, output1, output2, output3, processing);
	}

	@Override
	public RefineryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient catalyst = StaticPowerIngredient.read(buffer);
		FluidStack fluidInput1 = buffer.readFluidStack();
		FluidStack fluidInput2 = buffer.readFluidStack();
		FluidStack output1 = buffer.readFluidStack();
		FluidStack output2 = buffer.readFluidStack();
		FluidStack output3 = buffer.readFluidStack();

		// Create the recipe.
		return new RefineryRecipe(recipeId, catalyst, fluidInput1, fluidInput2, output1, output2, output3, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, RefineryRecipe recipe) {
		recipe.getCatalyst().write(buffer);
		buffer.writeFluidStack(recipe.getPrimaryFluidInput());
		buffer.writeFluidStack(recipe.getSecondaryFluidInput());
		buffer.writeFluidStack(recipe.getFluidOutput1());
		buffer.writeFluidStack(recipe.getFluidOutput2());
		buffer.writeFluidStack(recipe.getFluidOutput3());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}

	@Override
	public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
		return INSTANCE;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return ID;
	}
}
