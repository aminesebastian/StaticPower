package theking530.staticpower.data.crafting.wrappers.cauldron;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.StaticPower;

public class CauldronRecipeSerializer extends StaticPowerRecipeSerializer<CauldronRecipe> {
	public static final ResourceLocation ID = new ResourceLocation(StaticPower.MOD_ID, "cauldron_recipe");

	@Override
	public Codec<CauldronRecipe> getCodec() {
		return CauldronRecipe.CODEC;
	}

	@Override
	public CauldronRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		int time = buffer.readInt();
		boolean shouldDrainCauldron = buffer.readBoolean();
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidIngredient fluidInput = FluidIngredient.readFromBuffer(buffer);
		FluidStack fluidOutput = buffer.readFluidStack();

		// Create the recipe.
		return new CauldronRecipe(recipeId, input, output, fluidInput, fluidOutput, shouldDrainCauldron, time);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CauldronRecipe recipe) {
		buffer.writeInt(recipe.getRequiredTimeInCauldron());
		buffer.writeBoolean(recipe.shouldDrainAfterCraft());
		recipe.getInput().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getRequiredFluid().writeToBuffer(buffer);
		buffer.writeFluidStack(recipe.getOutputFluid());

	}
}
