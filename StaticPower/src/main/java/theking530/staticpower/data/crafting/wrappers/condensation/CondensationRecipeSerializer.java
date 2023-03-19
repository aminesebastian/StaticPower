package theking530.staticpower.data.crafting.wrappers.condensation;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.fluid.FluidIngredient;

public class CondensationRecipeSerializer extends StaticPowerRecipeSerializer<CondensationRecipe> {
	@Override
	public Codec<CondensationRecipe> getCodec() {
		return CondensationRecipe.CODEC;
	}

	@Override
	public CondensationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new CondensationRecipe(recipeId, input, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CondensationRecipe recipe) {
		recipe.getInputFluid().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
