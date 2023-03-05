package theking530.staticpower.data.crafting.wrappers.squeezer;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class SqueezerRecipeSerializer extends StaticPowerRecipeSerializer<SqueezerRecipe> {
	@Override
	public Codec<SqueezerRecipe> getCodec() {
		return SqueezerRecipe.CODEC;
	}

	@Override
	public SqueezerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidStack fluid = buffer.readFluidStack();

		return new SqueezerRecipe(recipeId, input, output, fluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, SqueezerRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
