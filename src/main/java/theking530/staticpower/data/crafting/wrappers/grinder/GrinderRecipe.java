package theking530.staticpower.data.crafting.wrappers.grinder;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.wrappers.AbstractMachineRecipe;
import theking530.staticpower.data.crafting.wrappers.ProbabilityItemStackOutput;
import theking530.staticpower.data.crafting.wrappers.RecipeMatchParameters;

public class GrinderRecipe extends AbstractMachineRecipe {
	public static final IRecipeType<GrinderRecipe> RECIPE_TYPE = IRecipeType.register("grinder");

	private final ProbabilityItemStackOutput[] outputs;
	/**
	 * This is a helper datatype to use whenever you need access just to the items
	 * (for example, to see if an inventory can take the items).
	 */
	private final ItemStack[] outputItems;
	private final Ingredient inputItem;

	public GrinderRecipe(ResourceLocation name, int processingTime, int powerCost, Ingredient input, ProbabilityItemStackOutput... outputs) {
		super(name, processingTime, powerCost);
		this.inputItem = input;
		this.outputs = outputs;
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

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return inputItem.test(matchParams.getItems()[0]);
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