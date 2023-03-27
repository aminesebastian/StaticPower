package theking530.staticpower.data.crafting.wrappers.fermenter;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class FermenterRecipeSerializer extends StaticPowerRecipeSerializer<FermenterRecipe> {

	@Override
	public Codec<FermenterRecipe> getCodec() {
		return FermenterRecipe.CODEC;
	}

	@Override
	public FermenterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		FluidStack output = buffer.readFluidStack();
		StaticPowerOutputItem residualOutput = StaticPowerOutputItem.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		// Create the recipe.
		return new FermenterRecipe(recipeId, input, residualOutput, output, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, FermenterRecipe recipe) {
		recipe.getInputIngredient().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluidStack());
		recipe.getResidualOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
