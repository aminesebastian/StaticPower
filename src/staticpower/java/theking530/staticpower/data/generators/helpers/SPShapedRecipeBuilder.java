package theking530.staticpower.data.generators.helpers;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticpower.data.generators.RecipeItem;

public class SPShapedRecipeBuilder extends ShapedRecipeBuilder {
	public SPShapedRecipeBuilder(ItemLike p_126114_, int p_126115_) {
		super(p_126114_, p_126115_);
	}

	public static SPShapedRecipeBuilder shaped(ItemLike p_126117_) {
		return shaped(p_126117_, 1);
	}

	public static SPShapedRecipeBuilder shaped(ItemLike p_126119_, int p_126120_) {
		return new SPShapedRecipeBuilder(p_126119_, p_126120_);
	}

	public SPShapedRecipeBuilder define(Character key, RecipeItem item) {
		if (item.hasItemTag()) {
			define(key, item.getItemTag());
		} else {
			define(key, item.getItem());
		}
		return this;
	}

	public SPShapedRecipeBuilder define(Character p_206417_, TagKey<Item> p_206418_) {
		super.define(p_206417_, p_206418_);
		return this;
	}

	public SPShapedRecipeBuilder define(Character p_126128_, ItemLike p_126129_) {
		super.define(p_126128_, p_126129_);
		return this;
	}

	public SPShapedRecipeBuilder define(Character p_126125_, Ingredient p_126126_) {
		super.define(p_126125_, p_126126_);
		return this;
	}
}
