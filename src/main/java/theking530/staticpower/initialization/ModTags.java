package theking530.staticpower.initialization;

import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class ModTags {
	public static final Tag<Item> LOG = ItemTags.getCollection().getOrCreate(new ResourceLocation("minecraft", "logs"));
	public static final Tag<Item> LEAVES = ItemTags.getCollection().getOrCreate(new ResourceLocation("minecraft", "leaves"));
	public static final Tag<Item> SAPLING = ItemTags.getCollection().getOrCreate(new ResourceLocation("minecraft", "saplings"));
}
