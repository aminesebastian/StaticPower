package theking530.staticcore.data.generators.helpers;

import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticcore.crafting.RecipeItem;

public class SCShapelessRecipeBuilder extends ShapelessRecipeBuilder {

	public SCShapelessRecipeBuilder(ItemLike p_126180_, int p_126181_) {
		super(p_126180_, p_126181_);
	}

	public static SCShapelessRecipeBuilder shapeless(ItemLike p_126190_) {
		return new SCShapelessRecipeBuilder(p_126190_, 1);
	}

	public static SCShapelessRecipeBuilder shapeless(ItemLike p_126192_, int p_126193_) {
		return new SCShapelessRecipeBuilder(p_126192_, p_126193_);
	}

	public SCShapelessRecipeBuilder requires(RecipeItem item) {
		if (item.hasItemTag()) {
			requires(item.getItemTag());
		} else {
			requires(item.getItem());
		}
		return this;
	}

	public SCShapelessRecipeBuilder requires(RecipeItem item, int count) {
		if (item.hasItemTag()) {
			for (int i = 0; i < count; i++) {
				requires(item.getItemTag());
			}
		} else {
			requires(item.getItem(), count);
		}
		return this;
	}

	public SCShapelessRecipeBuilder requires(TagKey<Item> p_206420_) {
		super.requires(p_206420_);
		return this;
	}

	public SCShapelessRecipeBuilder requires(ItemLike p_126210_) {
		super.requires(p_126210_);
		return this;
	}

	public SCShapelessRecipeBuilder requires(ItemLike p_126212_, int p_126213_) {
		super.requires(p_126212_, p_126213_);
		return this;
	}

	public SCShapelessRecipeBuilder requires(Ingredient p_126185_) {
		super.requires(p_126185_);
		return this;
	}

	public SCShapelessRecipeBuilder requires(Ingredient p_126187_, int p_126188_) {
		super.requires(p_126187_, p_126188_);
		return this;
	}
}
