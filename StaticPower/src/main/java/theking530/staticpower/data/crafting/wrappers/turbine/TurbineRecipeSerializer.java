package theking530.staticpower.data.crafting.wrappers.turbine;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.fluid.FluidIngredient;

public class TurbineRecipeSerializer extends StaticPowerRecipeSerializer<TurbineRecipe> {
	@Override
	public Codec<TurbineRecipe> getCodec() {
		return TurbineRecipe.CODEC;
	}

	@Override
	public TurbineRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
		FluidStack output = buffer.readFluidStack();
		int generation = buffer.readInt();
		return new TurbineRecipe(recipeId, input, output, generation);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, TurbineRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutput());
		buffer.writeInt(recipe.getGenerationAmount());
	}
}
