package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerIngredient;
import theking530.staticcore.crafting.StaticPowerOutputItem;
import theking530.staticcore.utilities.JsonUtilities;
import theking530.staticpower.data.crafting.wrappers.ShapedRecipePattern;
import theking530.staticpower.init.ModRecipeSerializers;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.init.tags.ModItemTags;

public class SolderingRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "soldering";
	public static final Ingredient DEFAULT_SOLDERING_IRON = Ingredient.of(ModItemTags.SOLDERING_IRON);

	protected static final int MAX_WIDTH = 3;
	protected static final int MAX_HEIGHT = 3;

	public static final Codec<SolderingRecipe> CODEC = RecordCodecBuilder
			.create(instance -> instance.group(ResourceLocation.CODEC.optionalFieldOf("id", null).forGetter(recipe -> recipe.getId()),
					ShapedRecipePattern.CODEC.fieldOf("input_items").forGetter(recipe -> recipe.getPattern()),
					JsonUtilities.INGREDIENT_CODEC.optionalFieldOf("soldering_iron").forGetter(recipe -> Optional.of(recipe.getSolderingIron())),
					StaticPowerOutputItem.CODEC.fieldOf("output").forGetter(recipe -> recipe.getOutput())).apply(instance, SolderingRecipe::new));

	private final ShapedRecipePattern pattern;
	private final Ingredient solderingIron;
	private final StaticPowerOutputItem recipeOutput;

	public SolderingRecipe(ResourceLocation id, ShapedRecipePattern pattern, Optional<Ingredient> solderingIron, StaticPowerOutputItem recipeOutputIn) {
		super(id);

		this.pattern = pattern;
		if (solderingIron.isPresent()) {
			this.solderingIron = solderingIron.get();
		} else {
			this.solderingIron = DEFAULT_SOLDERING_IRON;
		}
		this.recipeOutput = recipeOutputIn;
	}

	public ShapedRecipePattern getPattern() {
		return pattern;
	}

	public NonNullList<StaticPowerIngredient> getInputs() {
		return pattern.getIngredients();
	}

	public RecipeSerializer<SolderingRecipe> getSerializer() {
		return ModRecipeSerializers.SOLDERING_SERIALIZER.get();
	}

	public StaticPowerOutputItem getOutput() {
		return recipeOutput;
	}

	public Ingredient getSolderingIron() {
		return this.solderingIron;
	}

	@Override
	public RecipeType<SolderingRecipe> getType() {
		return ModRecipeTypes.SOLDERING_RECIPE_TYPE.get();
	}

	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
		// If there aren't enough items, just return false.
		if (matchParams.getItems().length != 9) {
			return false;
		}

		for (int i = 0; i < pattern.getRecipeWidth(); i++) {
			for (int j = 0; j < pattern.getRecipeHeight(); j++) {
				if (!pattern.getIngredients().get(i + j * pattern.getRecipeWidth()).testWithCount(matchParams.getItems()[i + j * pattern.getRecipeWidth()])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	public ItemStack getCraftingResult(CraftingContainer inv) {
		return this.getResultItem().copy();
	}

	public static NonNullList<StaticPowerIngredient> deserializeIngredients(String[] pattern, Map<Character, StaticPowerIngredient> keys, int patternWidth, int patternHeight) {
		NonNullList<StaticPowerIngredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, StaticPowerIngredient.EMPTY);
		Set<Character> set = Sets.newHashSet(keys.keySet());
		set.remove(' ');

		for (int i = 0; i < pattern.length; ++i) {
			for (int j = 0; j < pattern[i].length(); ++j) {
				Character s = pattern[i].charAt(j);
				if (s == ' ') {
					set.remove(s);
					continue;
				}

				StaticPowerIngredient ingredient = keys.get(s);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}

				set.remove(s);
				nonnulllist.set(j + patternWidth * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonnulllist;
		}
	}

	@VisibleForTesting
	static String[] shrink(String... toShrink) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for (int i1 = 0; i1 < toShrink.length; ++i1) {
			String s = toShrink[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);
			if (j1 < 0) {
				if (k == i1) {
					++k;
				}

				++l;
			} else {
				l = 0;
			}
		}

		if (toShrink.length == l) {
			return new String[0];
		} else {
			String[] astring = new String[toShrink.length - l - k];

			for (int k1 = 0; k1 < astring.length; ++k1) {
				astring[k1] = toShrink[k1 + k].substring(i, j + 1);
			}

			return astring;
		}
	}

	private static int firstNonSpace(String str) {
		int i;
		for (i = 0; i < str.length() && str.charAt(i) == ' '; ++i) {
			;
		}

		return i;
	}

	private static int lastNonSpace(String str) {
		int i;
		for (i = str.length() - 1; i >= 0 && str.charAt(i) == ' '; --i) {
			;
		}

		return i;
	}

	public static String[] patternFromJson(JsonArray jsonArr) {
		String[] astring = new String[jsonArr.size()];
		if (astring.length > MAX_HEIGHT) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
		} else if (astring.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for (int i = 0; i < astring.length; ++i) {
				String s = GsonHelper.convertToString(jsonArr.get(i), "pattern[" + i + "]");
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

	/**
	 * Returns a key json object as a Java HashMap.
	 */
	public static Map<Character, StaticPowerIngredient> deserializeKey(JsonObject json) {
		Map<Character, StaticPowerIngredient> map = Maps.newHashMap();

		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey().charAt(0), StaticPowerIngredient.fromJson(entry.getValue()));
		}

		map.put(' ', StaticPowerIngredient.EMPTY);
		return map;
	}
}
