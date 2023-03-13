package theking530.staticpower.data.crafting.wrappers.fertilization;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class FertalizerRecipeSerializer extends StaticPowerRecipeSerializer<FertalizerRecipe> {

	@Override
	public Codec<FertalizerRecipe> getCodec() {
		return FertalizerRecipe.CODEC;
	}

	@Override
	public FertalizerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient fluid = FluidIngredient.readFromBuffer(buffer);
		float fertilizer = buffer.readFloat();
		return new FertalizerRecipe(recipeId, fluid, fertilizer);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FertalizerRecipe recipe) {
		recipe.getRequiredFluid().writeToBuffer(buffer);
		buffer.writeFloat(recipe.getFertalizationAmount());
	}
}
