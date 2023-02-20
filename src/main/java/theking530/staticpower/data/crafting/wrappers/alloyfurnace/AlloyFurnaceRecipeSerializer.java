package theking530.staticpower.data.crafting.wrappers.alloyfurnace;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class AlloyFurnaceRecipeSerializer extends StaticPowerRecipeSerializer<AlloyFurnaceRecipe> {
	@Override
	public Codec<AlloyFurnaceRecipe> getCodec() {
		return AlloyFurnaceRecipe.CODEC;
	}

	@Override
	public AlloyFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		StaticPowerIngredient input1 = StaticPowerIngredient.read(buffer);
		StaticPowerIngredient input2 = StaticPowerIngredient.read(buffer);
		StaticPowerOutputItem output = StaticPowerOutputItem.readFromBuffer(buffer);
		MachineRecipeProcessingSection processing = MachineRecipeProcessingSection.fromBuffer(buffer);
		int experience = buffer.readInt();
		return new AlloyFurnaceRecipe(recipeId, input1, input2, output, experience, processing);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, AlloyFurnaceRecipe recipe) {
		recipe.getInput1().write(buffer);
		recipe.getInput2().write(buffer);
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getProcessingSection().writeToBuffer(buffer);
		buffer.writeInt(recipe.getExperience());
	}
}
