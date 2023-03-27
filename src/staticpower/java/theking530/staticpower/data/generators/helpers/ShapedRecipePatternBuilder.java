package theking530.staticpower.data.generators.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import theking530.staticcore.crafting.RecipeItem;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticpower.data.crafting.wrappers.ShapedRecipePattern;

public class ShapedRecipePatternBuilder {
	private final List<String> pattern;
	private final Map<Character, StaticPowerIngredient> key;

	protected ShapedRecipePatternBuilder() {
		pattern = new ArrayList<>();
		key = new HashMap<>();
	}

	public static ShapedRecipePatternBuilder create() {
		return new ShapedRecipePatternBuilder();
	}

	public ShapedRecipePatternBuilder define(Character itemKey, TagKey<Item> item) {
		return this.define(itemKey, StaticPowerIngredient.of(item));
	}

	public ShapedRecipePatternBuilder define(Character itemKey, ItemLike item) {
		return this.define(itemKey, StaticPowerIngredient.of(item));
	}

	public ShapedRecipePatternBuilder define(Character key, RecipeItem item) {
		if (item.hasItemTag()) {
			define(key, item.getItemTag());
		} else {
			define(key, item.getItem());
		}
		return this;
	}

	public ShapedRecipePatternBuilder define(Character itemKey, StaticPowerIngredient ingredient) {
		if (this.key.containsKey(itemKey)) {
			throw new IllegalArgumentException("Symbol '" + itemKey + "' is already defined!");
		} else if (itemKey == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(itemKey, ingredient);
			return this;
		}
	}

	public ShapedRecipePatternBuilder pattern(String pattern) {
		if (!this.pattern.isEmpty() && pattern.length() != this.pattern.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.pattern.add(pattern);
			return this;
		}
	}

	public ShapedRecipePattern build() {
		return new ShapedRecipePattern(pattern, key);
	}
}