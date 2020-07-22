package theking530.staticpower.data.crafting.wrappers.bottler;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticpower.data.crafting.wrappers.AbstractStaticPowerRecipe;

public class BottleRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<BottleRecipe> RECIPE_TYPE = IRecipeType.register("bottler");

	private final ItemStack filledBottle;
	private final ItemStack emptyBottle;
	private final FluidStack fluid;

	public BottleRecipe(ResourceLocation name, ItemStack filledBottle, ItemStack emptyBottle, FluidStack fluid) {
		super(name);
		this.emptyBottle = emptyBottle;

		// EDGD CASE HANDLE, REMOVE THIS LATER.
		if (name.getPath().equals("bottler/water_bottle")) {
			this.filledBottle = new ItemStack(Items.POTION);
			this.filledBottle.setTag(new CompoundNBT());
			this.filledBottle.getTag().putString("Potion", "minecraft:water");
		} else {
			this.filledBottle = filledBottle;
		}

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
	public IRecipeSerializer<?> getSerializer() {
		return BottlerRecipeSerializer.INSTANCE;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}

	@Override
	public boolean equals(Object otherRecipe) {
		if (otherRecipe instanceof BottleRecipe) {
			// Get the other bottle recipe.
			BottleRecipe otherBottleRecipe = (BottleRecipe) otherRecipe;
			// Check the filled bottles.
			if (!ItemStack.areItemsEqual(filledBottle, otherBottleRecipe.filledBottle)) {
				return false;
			}
			// Check the empty bottles.
			if (!ItemStack.areItemsEqual(emptyBottle, otherBottleRecipe.emptyBottle)) {
				return false;
			}
			// Check the fluids.
			if (fluid.getAmount() != otherBottleRecipe.getFluid().getAmount() || !fluid.equals(otherBottleRecipe.getFluid()) && !FluidStack.areFluidStackTagsEqual(fluid, otherBottleRecipe.getFluid())) {
				return false;
			}

			// If all the above criteria are valid, return true.
			return true;
		}
		return false;
	}
}
