package theking530.staticcore.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class AbstractStaticPowerRecipe implements Recipe<RecipeMatchParameters> {
	private final ResourceLocation id;
	private final float experience;

	public AbstractStaticPowerRecipe(ResourceLocation id) {
		this(id, 0.0f);
	}

	public AbstractStaticPowerRecipe(ResourceLocation id, float experience) {
		this.id = id;
		this.experience = experience;
	}

	@Override
	public boolean matches(RecipeMatchParameters inv, Level worldIn) {
		return false;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public ItemStack assemble(RecipeMatchParameters inv) {
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
