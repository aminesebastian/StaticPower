package theking530.staticpower.data.crafting.wrappers.solidfuel;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.data.crafting.wrappers.AbstractStaticPowerRecipe;
import theking530.staticpower.data.crafting.wrappers.former.FormerRecipe;

public class SolidFuelRecipe extends AbstractStaticPowerRecipe {
	public static final IRecipeType<FormerRecipe> RECIPE_TYPE = IRecipeType.register("solid_fuel");
	private ItemStack item;

	public SolidFuelRecipe(ResourceLocation name, ItemStack item) {
		super(name);
		this.item = item;
	}

	public ItemStack getFuel() {
		return item;
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
