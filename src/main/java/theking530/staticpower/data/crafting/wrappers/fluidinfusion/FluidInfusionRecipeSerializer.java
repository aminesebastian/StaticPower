package theking530.staticpower.data.crafting.wrappers.fluidinfusion;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FluidInfusionRecipeSerializer extends StaticPowerRecipeSerializer<FluidInfusionRecipe> {
	@Override
	public Codec<FluidInfusionRecipe> getCodec() {
		return FluidInfusionRecipe.CODEC;
	}

	@Override
	public FluidInfusionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		FluidIngredient fluidInput = FluidIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new FluidInfusionRecipe(recipeId, input, fluidInput, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FluidInfusionRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getRequiredFluid().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
