package theking530.staticpower.data.crafting.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import theking530.staticpower.StaticPower;

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
		StaticPower.LOGGER.error("This method should not be called on any Static Power Recipes.");
		return false;
	}

	@Override
	public ResourceLocation getId() {
		return name;
	}

	@Override
	public ItemStack getCraftingResult(IInventory inv) {
		StaticPower.LOGGER.error("This method is not applicable for this recipe.");
		return null;
	}

	@Override
	public boolean canFit(int width, int height) {
		StaticPower.LOGGER.error("This method is not applicable for this recipe.");
		return false;
	}

	@Override
	public ItemStack getRecipeOutput() {
		StaticPower.LOGGER.error("This method is not applicable for this recipe.");
		return ItemStack.EMPTY;
	}

	@Override
	public String toString() {
		return getId().toString();
	}

}
