package theking530.staticcore.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import theking530.staticcore.StaticCore;

public class ItemTags {

	public static final TagKey<Item> RESEARCH = create("research");

	public static TagKey<Item> create(String name) {
		return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(StaticCore.MOD_ID, name));
	}
}
