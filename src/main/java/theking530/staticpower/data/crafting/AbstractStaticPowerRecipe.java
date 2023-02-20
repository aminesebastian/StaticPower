package theking530.staticpower.data.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class AbstractStaticPowerRecipe implements Recipe<Container> {
	private final ResourceLocation id;

	public AbstractStaticPowerRecipe(ResourceLocation name) {
		this.id = name;
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
	public boolean matches(Container inv, Level worldIn) {
		return false;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack assemble(Container inv) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	public ItemStack getResultItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public String toString() {
		return getId().toString();
	}
}
