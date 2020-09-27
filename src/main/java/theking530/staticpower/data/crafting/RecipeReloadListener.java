package theking530.staticpower.data.crafting;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class RecipeReloadListener extends JsonReloadListener {
	private static final Gson GSON_INSTANCE = new Gson();
	public final RecipeManager manager;

	public RecipeReloadListener(RecipeManager manager) {
		super(GSON_INSTANCE, "recipes");
		this.manager = manager;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectMap, IResourceManager manager, IProfiler profiler) {
		StaticPowerRecipeRegistry.onResourcesReloaded(this.manager);
	}
}
