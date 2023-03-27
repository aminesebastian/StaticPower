package theking530.staticcore.utilities.tags;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

public class TagUtilities {
	public static boolean matches(TagKey<Fluid> tag, Fluid biome) {
		return ForgeRegistries.FLUIDS.tags().getTag(tag).contains(biome);
	}

	public static boolean matches(TagKey<Block> tag, Block block) {
		return ForgeRegistries.BLOCKS.tags().getTag(tag).contains(block);
	}

	public static boolean matches(TagKey<Biome> tag, Biome biome) {
		return ForgeRegistries.BIOMES.tags().getTag(tag).contains(biome);
	}

	public static boolean matches(TagKey<Item> tag, Item item) {
		return ForgeRegistries.ITEMS.tags().getTag(tag).contains(item);
	}

	public static List<ResourceLocation> getTags(Item item) {
		Holder<Item> holder = ForgeRegistries.ITEMS.getHolder(item).orElse(null);
		if (holder != null) {
			holder.tags().toList();
		}
		return Collections.emptyList();
	}

	public static List<TagKey<Item>> getTags(ItemStack itemStack) {
		Holder<Item> holder = ForgeRegistries.ITEMS.getHolder(itemStack.getItem()).orElse(null);
		if (holder != null) {
			return holder.tags().toList();
		}
		return Collections.emptyList();
	}
}
