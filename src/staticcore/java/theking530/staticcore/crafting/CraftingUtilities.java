package theking530.staticcore.crafting;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CraftingUtilities {

	public static <K extends Container, T extends Recipe<K>> Optional<T> getRecipe(RecipeType<T> type, K params, Level level) {
		RecipeManager manager = level.getRecipeManager();
		return manager.getRecipeFor(type, params, level);
	}

	public static <T extends RecipeMatchParameters> Optional<? extends Recipe<?>> getRecipeByKey(ResourceLocation id, Level level) {
		RecipeManager manager = level.getRecipeManager();
		return manager.byKey(id);
	}
}
