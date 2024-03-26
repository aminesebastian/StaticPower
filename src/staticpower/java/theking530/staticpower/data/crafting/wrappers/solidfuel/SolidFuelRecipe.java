package theking530.staticpower.data.crafting.wrappers.solidfuel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import theking530.staticcore.crafting.AbstractStaticPowerRecipe;
import theking530.staticcore.crafting.RecipeMatchParameters;
import theking530.staticcore.crafting.StaticPowerRecipeType;
import theking530.staticcore.utilities.item.ItemUtilities;

public class SolidFuelRecipe extends AbstractStaticPowerRecipe {
	public static final String ID = "solid_fuel";
	public static final RecipeType<SolidFuelRecipe> RECIPE_TYPE = new StaticPowerRecipeType<SolidFuelRecipe>();

	private final ItemStack item;
	private final int fuelAmount;

	public SolidFuelRecipe(ResourceLocation name, ItemStack item) {
		super(name);
		this.item = item;
		this.fuelAmount = ForgeHooks.getBurnTime(item, RECIPE_TYPE);
	}

	public ItemStack getInput() {
		return item;
	}

	public int getFuelAmount() {
		return fuelAmount;
	}

	@Override
	protected boolean matchesInternal(RecipeMatchParameters matchParams, Level worldIn) {
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
