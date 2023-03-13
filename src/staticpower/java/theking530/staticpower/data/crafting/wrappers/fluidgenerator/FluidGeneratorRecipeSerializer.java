package theking530.staticpower.data.crafting.wrappers.fluidgenerator;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FluidGeneratorRecipeSerializer extends StaticPowerRecipeSerializer<FluidGeneratorRecipe> {
	@Override
	public Codec<FluidGeneratorRecipe> getCodec() {
		return FluidGeneratorRecipe.CODEC;
	}

	@Override
	public FluidGeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient fluid = FluidIngredient.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		// Create the recipe.
		return new FluidGeneratorRecipe(recipeId, fluid, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FluidGeneratorRecipe recipe) {
		recipe.getFluid().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
