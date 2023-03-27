package theking530.staticpower.data.crafting.wrappers.carpenter;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticpower.data.crafting.wrappers.ShapedRecipePattern;

public class CarpenterRecipeSerializer extends StaticPowerRecipeSerializer<CarpenterRecipe> {
	@Override
	public Codec<CarpenterRecipe> getCodec() {
		return CarpenterRecipe.CODEC;
	}

	@Override
	public CarpenterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		ShapedRecipePattern pattern = ShapedRecipePattern.fromNetwork(buffer);
		StaticPowerOutputItem primaryOutput = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem secondaryOutput = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidStack fluidOutput = buffer.readFluidStack();
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);

		return new CarpenterRecipe(recipeId, pattern, primaryOutput, secondaryOutput, fluidOutput, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CarpenterRecipe recipe) {
		recipe.getPattern().toNetwork(buffer);
		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
