package theking530.staticpower.data.crafting.wrappers.evaporation;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class EvaporatorRecipeSerializer extends StaticPowerRecipeSerializer<EvaporatorRecipe> {

	@Override
	public Codec<EvaporatorRecipe> getCodec() {
		return EvaporatorRecipe.CODEC;
	}

	@Override
	public EvaporatorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient input = FluidIngredient.readFromBuffer(buffer);
		FluidStack output = buffer.readFluidStack();
		// Create the recipe.
		return new EvaporatorRecipe(recipeId, input, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, EvaporatorRecipe recipe) {
		recipe.getInputFluid().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
