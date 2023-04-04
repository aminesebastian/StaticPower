package theking530.staticcore.data.generators.helpers;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticcore.crafting.RecipeItem;

public class SCShapedRecipeBuilder extends ShapedRecipeBuilder {
	public SCShapedRecipeBuilder(ItemLike p_126114_, int p_126115_) {
		super(p_126114_, p_126115_);
	}

	public static SCShapedRecipeBuilder shaped(ItemLike p_126117_) {
		return shaped(p_126117_, 1);
	}

	public static SCShapedRecipeBuilder shaped(ItemLike p_126119_, int p_126120_) {
		return new SCShapedRecipeBuilder(p_126119_, p_126120_);
	}

	public SCShapedRecipeBuilder define(Character key, RecipeItem item) {
		if (item.hasItemTag()) {
			define(key, item.getItemTag());
		} else {
			define(key, item.getItem());
		}
		return this;
	}

	public SCShapedRecipeBuilder define(Character p_206417_, TagKey<Item> p_206418_) {
		super.define(p_206417_, p_206418_);
		return this;
	}

	public SCShapedRecipeBuilder define(Character p_126128_, ItemLike p_126129_) {
		super.define(p_126128_, p_126129_);
		return this;
	}

	public SCShapedRecipeBuilder define(Character p_126125_, Ingredient p_126126_) {
		super.define(p_126125_, p_126126_);
		return this;
	}
}
