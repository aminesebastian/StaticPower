package theking530.staticpower.data.crafting.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.utilities.JsonUtilities;

public class ShapedRecipePattern {
	protected static final int MAX_WIDTH = 3;
	protected static final int MAX_HEIGHT = 3;

	private final List<String> rawPattern;
	private final Map<Character, StaticPowerIngredient> keys;
	private final int recipeWidth;
	private final int recipeHeight;
	private final String[] pattern;
	private final NonNullList<StaticPowerIngredient> ingredients;

	public static final Codec<ShapedRecipePattern> CODEC = RecordCodecBuilder
			.create(instance -> instance
					.group(Codec.STRING.listOf().fieldOf("pattern").forGetter(recipe -> recipe.rawPattern),
							Codec.unboundedMap(JsonUtilities.CHAR, StaticPowerIngredient.CODEC).fieldOf("key").forGetter(recipe -> recipe.keys))
					.apply(instance, ShapedRecipePattern::new));

	public ShapedRecipePattern(List<String> pattern, Map<Character, StaticPowerIngredient> keys) {
		this.rawPattern = pattern;
		this.keys = keys;
		this.recipeWidth = pattern.get(0).length();
		this.recipeHeight = pattern.size();
		this.pattern = buildPattern();
		this.ingredients = expandIngredients();
	}

	public NonNullList<StaticPowerIngredient> getIngredients() {
		return ingredients;
	}

	public String[] getPattern() {
		return pattern;
	}

	public Map<Character, StaticPowerIngredient> getKeys() {
		return keys;
	}

	public int getRecipeWidth() {
		return recipeWidth;
	}

	public int getRecipeHeight() {
		return recipeHeight;
	}

	private String[] buildPattern() {
		String[] astring = new String[rawPattern.size()];
		if (astring.length > MAX_HEIGHT) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
		} else if (astring.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for (int i = 0; i < astring.length; ++i) {
				String s = rawPattern.get(i);
				if (s.length() > MAX_WIDTH) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
				}

				if (i > 0 && astring[0].length() != s.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				astring[i] = s;
			}

			return astring;
		}
	}

	public static ShapedRecipePattern fromNetwork(FriendlyByteBuf buffer) {
		byte recipeHeight = buffer.readByte();
		List<String> pattern = new ArrayList<String>();
		for (int i = 0; i < recipeHeight; i++) {
			pattern.add(buffer.readUtf());
		}

		Map<Character, StaticPowerIngredient> ingredientMap = new HashMap<>();
		byte ingredientCount = buffer.readByte();
		for (int i = 0; i < ingredientCount; i++) {
			ingredientMap.put(buffer.readChar(), StaticPowerIngredient.readFromBuffer(buffer));
		}

		return new ShapedRecipePattern(pattern, ingredientMap);
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		buffer.writeByte(recipeHeight);
		for (int i = 0; i < recipeHeight; i++) {
			buffer.writeUtf(rawPattern.get(i));
		}

		buffer.writeByte(keys.size());
		for (Entry<Character, StaticPowerIngredient> ing : keys.entrySet()) {
			buffer.writeChar(ing.getKey());
			ing.getValue().writeToBuffer(buffer);
		}
	}

	private NonNullList<StaticPowerIngredient> expandIngredients() {
		NonNullList<StaticPowerIngredient> nonnulllist = NonNullList.withSize(recipeWidth * recipeHeight, StaticPowerIngredient.EMPTY);
		Set<Character> set = Sets.newHashSet(keys.keySet());
		set.remove(' ');

		for (int i = 0; i < rawPattern.size(); ++i) {
			for (int j = 0; j < rawPattern.get(i).length(); ++j) {
				Character s = rawPattern.get(i).charAt(j);
				if (s == ' ') {
					set.remove(s);
					continue;
				}

				StaticPowerIngredient ingredient = keys.get(s);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}

				set.remove(s);
				nonnulllist.set(j + recipeWidth * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonnulllist;
		}
	}
}
