package theking530.staticpower.data.crafting.wrappers.cokeoven;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class CokeOvenRecipeSerializer extends StaticPowerRecipeSerializer<CokeOvenRecipe> {
	@Override
	public Codec<CokeOvenRecipe> getCodec() {
		return CokeOvenRecipe.CODEC;
	}

	@Override
	public CokeOvenRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		FluidStack fluidOutput = FluidStack.readFromPacket(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		float experience = buffer.readFloat();
		return new CokeOvenRecipe(recipeId, input, output, fluidOutput, experience, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, CokeOvenRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getOutputFluid().writeToPacket(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
		buffer.writeFloat(recipe.getExperience());
	}
}
