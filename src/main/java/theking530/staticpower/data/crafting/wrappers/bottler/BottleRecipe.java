package theking530.staticpower.data.crafting.wrappers.bottler;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.utilities.ItemUtilities;

public class BottleRecipe extends AbstractStaticPowerRecipe {
	public static final RecipeType<BottleRecipe> RECIPE_TYPE = RecipeType.register("bottler");

	private final ItemStack filledBottle;
	private final ItemStack emptyBottle;
	private final FluidStack fluid;

	public BottleRecipe(ResourceLocation name, ItemStack filledBottle, ItemStack emptyBottle, FluidStack fluid) {
		super(name);
		this.emptyBottle = emptyBottle;
		this.filledBottle = filledBottle;
		this.fluid = fluid;
	}

	public ItemStack getEmptyBottle() {
		return emptyBottle;
	}

	public ItemStack getFilledBottle() {
		return filledBottle;
	}

	public FluidStack getFluid() {
		return fluid;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return BottlerRecipeSerializer.INSTANCE;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	public boolean isValid(RecipeMatchParameters matchParams) {
		// Check if the item and fluid match.
		boolean itemMatched = ItemUtilities.areItemStacksStackable(matchParams.getItems()[0], emptyBottle) && fluid.isFluidEqual(matchParams.getFluids()[0]);

		// If the items matched, check to see if we should match counts too.
		if (itemMatched && matchParams.shouldVerifyItemCounts() && matchParams.getItems()[0].getCount() < emptyBottle.getCount()) {
			return false;
		} else {
			return itemMatched;
		}
	}

	@Override
	public boolean equals(Object otherRecipe) {
		if (otherRecipe instanceof BottleRecipe) {
			// Get the other bottle recipe.
			BottleRecipe otherBottleRecipe = (BottleRecipe) otherRecipe;
			// Check the filled bottles.
			if (!ItemStack.isSame(filledBottle, otherBottleRecipe.filledBottle)) {
				return false;
			}
			// Check the empty bottles.
			if (!ItemStack.isSame(emptyBottle, otherBottleRecipe.emptyBottle)) {
				return false;
			}
			// Check the fluids.
			if (fluid.getAmount() != otherBottleRecipe.getFluid().getAmount()
					|| !fluid.equals(otherBottleRecipe.getFluid()) && !FluidStack.areFluidStackTagsEqual(fluid, otherBottleRecipe.getFluid())) {
				return false;
			}

			// If all the above criteria are valid, return true.
			return true;
		}
		return false;
	}
}
