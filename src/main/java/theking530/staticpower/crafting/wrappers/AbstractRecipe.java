package theking530.staticpower.crafting.wrappers;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class AbstractRecipe implements IRecipe<IInventory> {
	private ResourceLocation name;

	public AbstractRecipe(ResourceLocation name) {
		this.name = name;
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
