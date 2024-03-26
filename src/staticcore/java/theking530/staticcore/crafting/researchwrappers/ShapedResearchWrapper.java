package theking530.staticcore.crafting.researchwrappers;

import java.util.Set;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class ShapedResearchWrapper extends ShapedRecipe implements IResearchBlockedRecipe {
	private final ShapedRecipe old;
	private final Set<ResourceLocation> requiredResearch;

	public ShapedResearchWrapper(Set<ResourceLocation> requiredResearch, ShapedRecipe old) {
		super(old.getId(), old.getGroup(), old.getWidth(), old.getHeight(), old.getIngredients(), old.getResultItem());
		this.requiredResearch = requiredResearch;
		this.old = old;
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		// First check if the recipe event matches.
		if (old.matches(container, level)) {
			return CraftingResearchLockingUtilities.passesResearchRequirements(this, container, level);
		}

		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer container) {
		return old.assemble(container);
	}

	@Override
	public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
		return old.canCraftInDimensions(p_43999_, p_44000_);
	}

	@Override
	public ItemStack getResultItem() {
		return old.getResultItem();
	}

	@Override
	public ResourceLocation getId() {
		return old.getId();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return old.getSerializer();
	}

	@Override
	public RecipeType<?> getType() {
		return old.getType();
	}

	public NonNullList<ItemStack> getRemainingItems(CraftingContainer p_44004_) {
		return old.getRemainingItems(p_44004_);
	}

	public NonNullList<Ingredient> getIngredients() {
		return old.getIngredients();
	}

	public boolean isSpecial() {
		return old.isSpecial();
	}

	public String getGroup() {
		return old.getGroup();
	}

	public ItemStack getToastSymbol() {
		return old.getToastSymbol();
	}

	public boolean isIncomplete() {
		return old.isIncomplete();
	}

	@Override
	public Set<ResourceLocation> getRequiredResearch() {
		return requiredResearch;
	}
}
