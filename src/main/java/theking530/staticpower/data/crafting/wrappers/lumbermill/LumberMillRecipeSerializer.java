package theking530.staticpower.data.crafting.wrappers.lumbermill;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class LumberMillRecipeSerializer extends StaticPowerRecipeSerializer<LumberMillRecipe> {
	@Override
	public Codec<LumberMillRecipe> getCodec() {
		return LumberMillRecipe.CODEC;
	}

	@Override
	public LumberMillRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem primary = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem secondary = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidStack outFluid = buffer.readFluidStack();

		return new LumberMillRecipe(recipeId, input, primary, secondary, outFluid, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, LumberMillRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getPrimaryOutput().writeToBuffer(buffer);
		recipe.getSecondaryOutput().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
