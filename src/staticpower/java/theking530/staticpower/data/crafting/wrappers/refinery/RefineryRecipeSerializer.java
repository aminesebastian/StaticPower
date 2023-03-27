package theking530.staticpower.data.crafting.wrappers.refinery;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.fluid.FluidIngredient;

public class RefineryRecipeSerializer extends StaticPowerRecipeSerializer<RefineryRecipe> {
	@Override
	public Codec<RefineryRecipe> getCodec() {
		return RefineryRecipe.CODEC;
	}

	@Override
	public RefineryRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient catalyst = StaticPowerIngredient.readFromBuffer(buffer);
		FluidIngredient fluidInput1 = FluidIngredient.readFromBuffer(buffer);
		FluidIngredient fluidInput2 = FluidIngredient.readFromBuffer(buffer);
		FluidStack output1 = buffer.readFluidStack();
		FluidStack output2 = buffer.readFluidStack();
		FluidStack output3 = buffer.readFluidStack();

		// Create the recipe.
		return new RefineryRecipe(recipeId, fluidInput1, fluidInput2, catalyst, output1, output2, output3, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, RefineryRecipe recipe) {
		recipe.getCatalyst().writeToBuffer(buffer);
		recipe.getPrimaryFluidInput().writeToBuffer(buffer);
		recipe.getSecondaryFluidInput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getFluidOutput1());
		buffer.writeFluidStack(recipe.getFluidOutput2());
		buffer.writeFluidStack(recipe.getFluidOutput3());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
