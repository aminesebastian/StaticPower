package theking530.staticcore.crafting;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;

public class RecipeReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON_INSTANCE = new Gson();
	public final RecipeManager manager;

	public RecipeReloadListener(RecipeManager manager) {
		super(GSON_INSTANCE, "recipes");
		this.manager = manager;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectMap, ResourceManager manager, ProfilerFiller profiler) {
		StaticCoreRecipeManager.onResourcesReloaded(this.manager);
	}
}
