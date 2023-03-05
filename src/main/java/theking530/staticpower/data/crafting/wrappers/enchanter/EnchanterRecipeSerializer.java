package theking530.staticpower.data.crafting.wrappers.enchanter;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import theking530.staticcore.fluid.FluidIngredient;
import theking530.staticpower.data.crafting.EnchantmentRecipeWrapper;
import theking530.staticpower.data.crafting.MachineRecipeProcessingSection;
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

public class EnchanterRecipeSerializer extends StaticPowerRecipeSerializer<EnchanterRecipe> {
	@Override
	public Codec<EnchanterRecipe> getCodec() {
		return EnchanterRecipe.CODEC;
	}

	@Override
	public EnchanterRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		FluidIngredient inputFluid = FluidIngredient.readFromBuffer(buffer);

		// Read all the inputs.
		List<StaticPowerIngredient> inputs = new ArrayList<StaticPowerIngredient>();
		for (int i = 0; i < buffer.readByte(); i++) {
			inputs.add(StaticPowerIngredient.readFromBuffer(buffer));
		}

		// Read all the enchantments.
		List<EnchantmentRecipeWrapper> enchantments = new ArrayList<EnchantmentRecipeWrapper>();
		for (int i = 0; i < buffer.readByte(); i++) {
			enchantments.add(EnchantmentRecipeWrapper.fromBuffer(buffer));
		}

		return new EnchanterRecipe(recipeId, inputs, inputFluid, enchantments, MachineRecipeProcessingSection.fromBuffer(buffer));
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, EnchanterRecipe recipe) {
		recipe.getInputFluidStack().writeToBuffer(buffer);

		// Write the items.
		buffer.writeByte(recipe.getInputIngredients().size());
		for (StaticPowerIngredient ing : recipe.getInputIngredients()) {
			ing.writeToBuffer(buffer);
		}

		// Write the enchantments.
		buffer.writeByte(recipe.getEnchantments().size());
		for (EnchantmentRecipeWrapper wrapper : recipe.getEnchantments()) {
			wrapper.writeToBuffer(buffer);
		}
		recipe.getProcessingSection().writeToBuffer(buffer);
	}
}
