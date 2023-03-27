package theking530.staticpower.data.crafting.wrappers.castingbasin;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.fluid.FluidIngredient;

public class CastingRecipeSerializer extends StaticPowerRecipeSerializer<CastingRecipe> {

	@Override
	public Codec<CastingRecipe> getCodec() {
		return CastingRecipe.CODEC;
	}

	@Override
	public CastingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient fluidInput = FluidIngredient.readFromBuffer(buffer);
		StaticPowerIngredient mold = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);

		// Create the recipe.
		return new CastingRecipe(recipeId, mold, output, fluidInput, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CastingRecipe recipe) {
		recipe.getInputFluid().writeToBuffer(buffer);
		recipe.getRequiredMold().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
