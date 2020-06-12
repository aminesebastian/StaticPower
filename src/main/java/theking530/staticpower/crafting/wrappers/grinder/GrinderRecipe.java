package theking530.staticpower.crafting.wrappers.grinder;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;

public class GrinderRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<GrinderRecipe> RECIPE_TYPE = IRecipeType.register("grinder");

	private final GrinderOutput[] outputs;
	/**
	 * This is a helper datatype to use whenever you need access just to the items
	 * (for example, to see if an inventory can take the items).
	 */
	private final ItemStack[] outputItems;
	private final int processingTime;
	private final int powerCost;
	private final Ingredient inputItem;

	public GrinderRecipe(ResourceLocation name, int processingTime, int powerCost, Ingredient input, GrinderOutput... outputs) {
		super(name);
		this.processingTime = processingTime;
		this.powerCost = powerCost;
		this.inputItem = input;
		this.outputs = outputs;

		// Cache the output items.
		this.outputItems = new ItemStack[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			outputItems[i] = outputs[i].getItem();
		}
	}

	public GrinderOutput[] getOutputItems() {
		return outputs;
	}

	public ItemStack[] getRawOutputItems() {
		return outputItems;
	}

	public Ingredient getInputIngredient() {
		return inputItem;
	}

	public int getProcessingTime() {
		return processingTime;
	}

	public int getPowerCost() {
		return powerCost;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return inputItem.test(matchParams.getItems()[0]);
	}

	public static class GrinderOutput {
		private final ItemStack item;
		private final float percentChance;

		public GrinderOutput(ItemStack output) {
			this(output, 1.0f);
		}

		public GrinderOutput(ItemStack output, float percentage) {
			item = output;
			percentChance = percentage;
		}

		public boolean isValid() {
			if (item != null) {
				return true;
			}
			return false;
		}

		public ItemStack getItem() {
			return item;
		}

		public float getPercentage() {
			return percentChance;
		}

		/**
		 * Parses a {@link GrinderOutput} from the provided JSON.
		 * 
		 * @param json The JSON to parse from.
		 * @return A new Grinder Output representing the data from the provided JSON.
		 */
		public static GrinderOutput parseFromJSON(JsonObject json) {
			// Capture the output item.
			ItemStack output = ShapedRecipe.deserializeItem(json);

			// If the chance value is provided, use it, otherwise assume 100% chance and
			// return.
			if (JSONUtils.hasField(json, "chance")) {
				float percentChance = JSONUtils.getFloat(json, "chance");
				return new GrinderOutput(output, percentChance);
			} else {
				return new GrinderOutput(output);
			}
		}
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return GrinderRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}