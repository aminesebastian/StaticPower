package theking530.staticpower.data.crafting.wrappers.soldering;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.wrappers.StaticPowerRecipeType;

public class SolderingRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "soldering";
	public static final RecipeType<SolderingRecipe> RECIPE_TYPE = new StaticPowerRecipeType<SolderingRecipe>();

	protected static final int MAX_WIDTH = 3;
	protected static final int MAX_HEIGHT = 3;

	public final int recipeWidth;
	public final int recipeHeight;
	public final NonNullList<Ingredient> recipeItems;
	public final Ingredient solderingIron;
	public final ItemStack recipeOutput;
	public final String group;

	public SolderingRecipe(ResourceLocation name, String groupIn, int recipeWidthIn, int recipeHeightIn, Ingredient solderingIron, NonNullList<Ingredient> recipeItemsIn,
			ItemStack recipeOutputIn) {
		super(name);
		this.group = groupIn;
		this.solderingIron = solderingIron;
		this.recipeWidth = recipeWidthIn;
		this.recipeHeight = recipeHeightIn;
		this.recipeItems = recipeItemsIn;
		this.recipeOutput = recipeOutputIn;
	}

	public RecipeSerializer<?> getSerializer() {
		return SolderingRecipeSerializer.INSTANCE;
	}

	/**
	 * Get the result of this recipe, usually for display purposes (e.g. recipe
	 * book). If your recipe has more than one possible result (e.g. it's dynamic
	 * and depends on its inputs), then return an empty stack.
	 */
	public ItemStack getResultItem() {
		return recipeOutput;
	}

	public Ingredient getSolderingIron() {
		return this.solderingIron;
	}

	public NonNullList<Ingredient> getIngredients() {
		return this.recipeItems;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		// If there aren't enough items, just return false.
		if (matchParams.getItems().length != 9) {
			return false;
		}

		for (int i = 0; i < this.recipeWidth; i++) {
			for (int j = 0; j < this.recipeHeight; j++) {
				if (!recipeItems.get(i + j * this.recipeWidth).test(matchParams.getItems()[i + j * this.recipeWidth])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	public boolean matches(IItemHandler inv, Level worldIn) {
		for (int i = 0; i <= this.recipeWidth; ++i) {
			for (int j = 0; j <= this.recipeHeight; ++j) {
				if (this.checkMatch(inv, i, j, true)) {
					return true;
				}

				if (this.checkMatch(inv, i, j, false)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	private boolean checkMatch(IItemHandler craftingInventory, int p_77573_2_, int p_77573_3_, boolean p_77573_4_) {
		for (int i = 0; i < recipeWidth; ++i) {
			for (int j = 0; j < recipeHeight; ++j) {
				int k = i - p_77573_2_;
				int l = j - p_77573_3_;
				Ingredient ingredient = Ingredient.EMPTY;
				if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {
					if (p_77573_4_) {
						ingredient = this.recipeItems.get(this.recipeWidth - k - 1 + l * this.recipeWidth);
					} else {
						ingredient = this.recipeItems.get(k + l * this.recipeWidth);
					}
				}

				if (!ingredient.test(craftingInventory.getStackInSlot(i + j * recipeWidth))) {
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

	public static NonNullList<Ingredient> deserializeIngredients(String[] pattern, Map<String, Ingredient> keys, int patternWidth, int patternHeight) {
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(patternWidth * patternHeight, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(keys.keySet());
		set.remove(" ");

		for (int i = 0; i < pattern.length; ++i) {
			for (int j = 0; j < pattern[i].length(); ++j) {
				String s = pattern[i].substring(j, j + 1);
				Ingredient ingredient = keys.get(s);
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
	public static Map<String, Ingredient> deserializeKey(JsonObject json) {
		Map<String, Ingredient> map = Maps.newHashMap();

		for (Entry<String, JsonElement> entry : json.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String) entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
		}

		map.put(" ", Ingredient.EMPTY);
		return map;
	}
}
