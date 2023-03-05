package theking530.staticpower.data.crafting.wrappers.mixer;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class MixerRecipeSerializer extends StaticPowerRecipeSerializer<MixerRecipe> {

	@Override
	public Codec<MixerRecipe> getCodec() {
		return MixerRecipe.CODEC;
	}

	@Override
	public MixerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input1 = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerIngredient input2 = StaticPowerIngredient.readFromBuffer(buffer);
		FluidIngredient fluidInput1 = FluidIngredient.readFromBuffer(buffer);
		FluidIngredient fluidInput2 = FluidIngredient.readFromBuffer(buffer);
		FluidStack output = buffer.readFluidStack();

		// Create the recipe.
		return new MixerRecipe(recipeId, input1, input2, fluidInput1, fluidInput2, output, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, MixerRecipe recipe) {
		recipe.getPrimaryItemInput().writeToBuffer(buffer);
		recipe.getSecondaryItemInput().writeToBuffer(buffer);
		recipe.getPrimaryFluidInput().writeToBuffer(buffer);
		recipe.getSecondaryFluidInput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutput());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
