package theking530.staticpower.data.crafting.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class AbstractStaticPowerRecipe implements IRecipe<IInventory> {
	private ResourceLocation name;

	public AbstractStaticPowerRecipe(ResourceLocation name) {
		this.name = name;
	}

	/**
	 * This is the only method that should be overidden for recipe validation.
	 * 
	 * @param matchParams
	 * @return
	 */
	public boolean isValid(RecipeMatchParameters matchParams) {
		return false;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return false;
	}

	@Override
	public ResourceLocation getId() {
		return name;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		return null;
	}

	@Override
	public boolean canFit(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public String toString() {
		return getId().toString();
	}

}
