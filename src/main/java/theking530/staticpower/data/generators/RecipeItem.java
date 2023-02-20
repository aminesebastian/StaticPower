package theking530.staticpower.data.generators;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class RecipeItem {
	private final ItemLike item;
	private final TagKey<Item> itemTag;

	protected RecipeItem(ItemLike item) {
		this.item = item;
		this.itemTag = null;
	}

	protected RecipeItem(TagKey<Item> itemTag) {
		this.item = null;
		this.itemTag = itemTag;
	}

	public static RecipeItem of(ItemLike item) {
		return new RecipeItem(item);
	}

	public static RecipeItem of(TagKey<Item> itemTag) {
		return new RecipeItem(itemTag);
	}

	public boolean hasItem() {
		return item != null;
	}

	public ItemLike getItem() {
		return item;
	}

	public boolean hasItemTag() {
		return itemTag != null;
	}

	public TagKey<Item> getItemTag() {
		return itemTag;
	}
}
