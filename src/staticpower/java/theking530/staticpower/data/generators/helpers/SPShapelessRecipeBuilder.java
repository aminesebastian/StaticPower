package theking530.staticpower.data.generators.helpers;

import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticpower.data.generators.RecipeItem;

public class SPShapelessRecipeBuilder extends ShapelessRecipeBuilder {

	public SPShapelessRecipeBuilder(ItemLike p_126180_, int p_126181_) {
		super(p_126180_, p_126181_);
	}

	public static SPShapelessRecipeBuilder shapeless(ItemLike p_126190_) {
		return new SPShapelessRecipeBuilder(p_126190_, 1);
	}

	public static SPShapelessRecipeBuilder shapeless(ItemLike p_126192_, int p_126193_) {
		return new SPShapelessRecipeBuilder(p_126192_, p_126193_);
	}

	public SPShapelessRecipeBuilder requires(RecipeItem item) {
		if (item.hasItemTag()) {
			requires(item.getItemTag());
		} else {
			requires(item.getItem());
		}
		return this;
	}

	public SPShapelessRecipeBuilder requires(RecipeItem item, int count) {
		if (item.hasItemTag()) {
			for (int i = 0; i < count; i++) {
				requires(item.getItemTag());
			}
		} else {
			requires(item.getItem(), count);
		}
		return this;
	}

	public SPShapelessRecipeBuilder requires(TagKey<Item> p_206420_) {
		super.requires(p_206420_);
		return this;
	}

	public SPShapelessRecipeBuilder requires(ItemLike p_126210_) {
		super.requires(p_126210_);
		return this;
	}

	public SPShapelessRecipeBuilder requires(ItemLike p_126212_, int p_126213_) {
		super.requires(p_126212_, p_126213_);
		return this;
	}

	public SPShapelessRecipeBuilder requires(Ingredient p_126185_) {
		super.requires(p_126185_);
		return this;
	}

	public SPShapelessRecipeBuilder requires(Ingredient p_126187_, int p_126188_) {
		super.requires(p_126187_, p_126188_);
		return this;
	}
}
