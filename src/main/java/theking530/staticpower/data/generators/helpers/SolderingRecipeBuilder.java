package theking530.staticpower.data.generators.helpers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import theking530.staticpower.data.crafting.StaticPowerOutputItem;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.data.generators.RecipeItem;

public class SolderingRecipeBuilder {
	private final StaticPowerOutputItem output;
	private final List<String> rows;
	private final Map<Character, Ingredient> key;
	private Ingredient solderingIron;

	public SolderingRecipeBuilder(StaticPowerOutputItem output) {
		this.output = output;
		rows = Lists.newArrayList();
		key = Maps.newLinkedHashMap();
	}

	public static SolderingRecipeBuilder shaped(ItemLike item) {
		return new SolderingRecipeBuilder(StaticPowerOutputItem.of(item));
	}

	public static SolderingRecipeBuilder shaped(ItemLike item, int count) {
		return new SolderingRecipeBuilder(StaticPowerOutputItem.of(item, count));
	}

	public static SolderingRecipeBuilder shaped(StaticPowerOutputItem output) {
		return new SolderingRecipeBuilder(output);
	}

	public SolderingRecipeBuilder define(Character itemKey, TagKey<Item> item) {
		return this.define(itemKey, Ingredient.of(item));
	}

	public SolderingRecipeBuilder define(Character itemKey, ItemLike item) {
		return this.define(itemKey, Ingredient.of(item));
	}

	public SolderingRecipeBuilder define(Character key, RecipeItem item) {
		if (item.hasItemTag()) {
			define(key, item.getItemTag());
		} else {
			define(key, item.getItem());
		}
		return this;
	}

	public SolderingRecipeBuilder define(Character itemKey, Ingredient ingredient) {
		if (this.key.containsKey(itemKey)) {
			throw new IllegalArgumentException("Symbol '" + itemKey + "' is already defined!");
		} else if (itemKey == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		} else {
			this.key.put(itemKey, ingredient);
			return this;
		}
	}

	public SolderingRecipeBuilder pattern(String pattern) {
		if (!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		} else {
			this.rows.add(pattern);
			return this;
		}
	}

	public SolderingRecipe build(String name) {
		ensureValid(name);

		String[] pattern = new String[rows.size()];
		pattern = rows.toArray(pattern);
		return new SolderingRecipe(null, pattern, key, Optional.ofNullable(solderingIron), output);
	}

	private void ensureValid(String name) {
		if (this.rows.isEmpty()) {
			throw new IllegalStateException("No pattern is defined for soldering recipe " + name + "!");
		} else {
			Set<Character> set = Sets.newHashSet(this.key.keySet());
			set.remove(' ');

			for (String s : this.rows) {
				for (int i = 0; i < s.length(); ++i) {
					char c0 = s.charAt(i);
					if (!this.key.containsKey(c0) && c0 != ' ') {
						throw new IllegalStateException("Pattern in recipe " + name + " uses undefined symbol '" + c0 + "'");
					}

					set.remove(c0);
				}
			}

			if (!set.isEmpty()) {
				throw new IllegalStateException("Ingredients are defined but not used in pattern for soldering recipe " + name);
			}
		}
	}
}