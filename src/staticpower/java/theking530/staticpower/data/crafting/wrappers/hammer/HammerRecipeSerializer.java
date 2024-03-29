package theking530.staticpower.data.crafting.wrappers.hammer;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.crafting.StaticPowerRecipeSerializer;

public class HammerRecipeSerializer extends StaticPowerRecipeSerializer<HammerRecipe> {
	@Override
	public Codec<HammerRecipe> getCodec() {
		return HammerRecipe.CODEC;
	}

	@Override
	public HammerRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		boolean isBlockType = buffer.readBoolean();
		StaticPowerOutputItem outputs = StaticPowerOutputItem.readFromBuffer(buffer);
		Ingredient hammer = Ingredient.fromNetwork(buffer);
		float experience = buffer.readFloat();
		boolean requiresAnvil = buffer.readBoolean();

		if (isBlockType) {
			JsonObject parsedBlockTagKey = GsonHelper.parse(buffer.readUtf());
			TagKey<Block> blockTagKey = TagKey.codec(ForgeRegistries.BLOCKS.getRegistryKey()).decode(JsonOps.INSTANCE, parsedBlockTagKey).result().get().getFirst();
			return new HammerRecipe(recipeId, experience, hammer, blockTagKey, outputs);
		} else {
			StaticPowerIngredient inputItem = StaticPowerIngredient.readFromBuffer(buffer);
			return new HammerRecipe(recipeId, experience, hammer, inputItem, outputs, requiresAnvil);
		}
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, HammerRecipe recipe) {
		buffer.writeBoolean(recipe.isBlockType());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getHammer().toNetwork(buffer);
		buffer.writeFloat(recipe.getExperience());
		buffer.writeBoolean(recipe.requiresAnvil());

		if (recipe.isBlockType()) {
			buffer.writeUtf(recipe.getEncodedBlockTag());
		} else {
			recipe.getInputItem().writeToBuffer(buffer);
		}
	}
}
