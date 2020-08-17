package theking530.staticpower.data.crafting.wrappers.solidfuel;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.utilities.ItemUtilities;

public class SolidFuelRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<SolidFuelRecipe> RECIPE_TYPE = IRecipeType.register("solid_fuel");

	private final ItemStack item;
	private final int fuelAmount;

	public SolidFuelRecipe(ResourceLocation name, ItemStack item) {
		super(name);
		this.item = item;
		this.fuelAmount = ForgeHooks.getBurnTime(item);
	}

	public ItemStack getFuel() {
		return item;
	}

	public int getFuelAmount() {
		return fuelAmount;
	}

	@Override
	public boolean isValid(RecipeMatchParameters matchParams) {
		return ItemUtilities.areItemStacksStackable(item, matchParams.getItems()[0]);
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return null;
	}

	@Override
	public IRecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
