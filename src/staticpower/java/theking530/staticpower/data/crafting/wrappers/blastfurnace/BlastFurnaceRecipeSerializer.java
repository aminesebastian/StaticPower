package theking530.staticpower.data.crafting.wrappers.blastfurnace;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.crafting.MachineRecipeProcessingSection;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class BlastFurnaceRecipeSerializer extends StaticPowerRecipeSerializer<BlastFurnaceRecipe> {
	@Override
	public Codec<BlastFurnaceRecipe> getCodec() {
		return BlastFurnaceRecipe.CODEC;
	}

	@Override
	public BlastFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input = StaticPowerIngredient.readFromBuffer(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		StaticPowerOutputItem slagOutput = StaticPowerOutputItem.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		float experience = buffer.readFloat();
		return new BlastFurnaceRecipe(recipeId, input, output, slagOutput, experience, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, BlastFurnaceRecipe recipe) {
		recipe.getInput().writeToBuffer(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getSlagOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
		buffer.writeFloat(recipe.getExperience());
	}
}
