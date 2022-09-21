package theking530.staticpower.data.crafting.wrappers;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public abstract class StaticPowerRecipeSerializer<T extends Recipe<?>> implements RecipeSerializer<T> {
	@Override
	public Class<RecipeSerializer<?>> getRegistryType() {
		return castClass(this.getClass());
	}

	@SuppressWarnings("unchecked") // Need this wrapper, because generics
	private static <G> Class<G> castClass(Class<?> cls) {
		return (Class<G>) cls;
	}
}
