package theking530.staticpower.crafting.wrappers.grinder;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.crafting.wrappers.ProbabilityItemStackOutput;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;

public class GrinderRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<GrinderRecipe> RECIPE_TYPE = IRecipeType.register("grinder");

	private final ProbabilityItemStackOutput[] outputs;
	/**
	 * This is a helper datatype to use whenever you need access just to the items
	 * (for example, to see if an inventory can take the items).
	 */
	private final ItemStack[] outputItems;
	private final int processingTime;
	private final int powerCost;
	private final int powerCostPerTick;
	private final Ingredient inputItem;

	public GrinderRecipe(ResourceLocation name, int processingTime, int powerCost, Ingredient input, ProbabilityItemStackOutput... outputs) {
		super(name);
		this.processingTime = processingTime;
		this.powerCost = powerCost;
		this.inputItem = input;
		this.outputs = outputs;
		this.powerCostPerTick = powerCost / processingTime;
		// Cache the output items.
		this.outputItems = new ItemStack[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			outputItems[i] = outputs[i].getItem();
		}
	}

	public ProbabilityItemStackOutput[] getOutputItems() {
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

	public int getPowerCostPerTick() {
		return powerCostPerTick;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return inputItem.test(matchParams.getItems()[0]) && matchParams.getStoredEnergy() >= powerCost;
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