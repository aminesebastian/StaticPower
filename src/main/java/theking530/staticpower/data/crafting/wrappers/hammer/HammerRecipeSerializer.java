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
import theking530.staticpower.data.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeSerializer;

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

		if (isBlockType) {
			JsonObject parsedBlockTagKey = GsonHelper.parse(buffer.readUtf());
			TagKey<Block> blockTagKey = TagKey.codec(ForgeRegistries.BLOCKS.getRegistryKey()).decode(JsonOps.INSTANCE, parsedBlockTagKey).result().get().getFirst();
			return new HammerRecipe(recipeId, hammer, blockTagKey, outputs);
		} else {
			StaticPowerIngredient inputItem = StaticPowerIngredient.read(buffer);
			return new HammerRecipe(recipeId, hammer, inputItem, outputs);
		}
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, HammerRecipe recipe) {
		buffer.writeBoolean(recipe.isBlockType());
		recipe.getOutput().writeToBuffer(buffer);
		recipe.getHammer().toNetwork(buffer);
		if (recipe.isBlockType()) {
			buffer.writeUtf(recipe.getEncodedBlockTag());
		} else {
			recipe.getInputItem().write(buffer);
		}
	}
}
