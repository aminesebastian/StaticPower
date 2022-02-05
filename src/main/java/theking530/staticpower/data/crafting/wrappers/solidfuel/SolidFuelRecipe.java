package theking530.staticpower.data.crafting.wrappers.solidfuel;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticpower.data.crafting.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.utilities.ItemUtilities;

public class SolidFuelRecipe extends AbstractStaticPowerRecipe {
	public static final RecipeType<SolidFuelRecipe> RECIPE_TYPE = RecipeType.register("solid_fuel");

	private final ItemStack item;
	private final int fuelAmount;

	public SolidFuelRecipe(ResourceLocation name, ItemStack item) {
		super(name);
		this.item = item;
		this.fuelAmount = ForgeHooks.getBurnTime(item, RECIPE_TYPE);
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
	public RecipeSerializer<?> getSerializer() {
		return null;
	}

	@Override
	public RecipeType<?> getType() {
		return RECIPE_TYPE;
	}
}
