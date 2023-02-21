package theking530.staticpower.data.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class AbstractStaticPowerRecipe implements Recipe<Container> {
	private final ResourceLocation id;
	private final float experience;

	public AbstractStaticPowerRecipe(ResourceLocation id) {
		this(id, 0.0f);
	}

	public AbstractStaticPowerRecipe(ResourceLocation id, float experience) {
		this.id = id;
		this.experience = experience;
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

	public boolean hasExperience() {
		return experience > 0;
	}

	public float getExperience() {
		return experience;
	}
}
