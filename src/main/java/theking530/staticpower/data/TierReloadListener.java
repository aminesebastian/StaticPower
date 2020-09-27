package theking530.staticpower.data;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;

public class TierReloadListener extends JsonReloadListener {
	public static final StaticPowerTier DEFAULT_TIER = new StaticPowerTier();
	public static final String FOLDER = "tiers";

	/**
	 * Contains all the tiers loaded at runtime.
	 */
	public static final Map<String, StaticPowerTier> TIERS = new HashMap<String, StaticPowerTier>();
	private static final Gson GSON_INSTANCE = new Gson();

	public TierReloadListener() {
		super(GSON_INSTANCE, FOLDER);
	}

	/**
	 * Gets the loaded tier object for the provided tier type.
	 * 
	 * @param tierType
	 * @return
	 */
	public static StaticPowerTier getTier(ResourceLocation tierType) {
		if (!TIERS.containsKey(tierType.toString())) {
			// If we HAVE tiers but not THIS tier, throw an error because that means we
			// intiailized and didn't have this tier. Otherwise, it could just be the
			// initialization.
			if (TIERS.size() > 0) {
				StaticPower.LOGGER.error(String.format(
						"Unable to find tier of type: %1$s. Supplying default. This error can be safely ignored during mod initialization.",
						tierType));
			}

			return DEFAULT_TIER;
		}
		return TIERS.get(tierType.toString());
	}

	public static void updateFromServer(Collection<StaticPowerTier> tiers) {
		// Capture if this is the first time we are caching.
		boolean firstTime = TIERS.size() == 0;
		TIERS.clear();
		for (StaticPowerTier tier : tiers) {
			TIERS.put(tier.getTierId().toString(), tier);
		} // Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power tiers from the server.",
				(firstTime ? "recieved" : "re-recieved"), TIERS.size()));
	}

	public static void updateOnServer(IResourceManager manager) {
		Map<ResourceLocation, JsonElement> output = new TreeMap<ResourceLocation, JsonElement>();
		Collection<ResourceLocation> tiers = manager.getAllResourceLocations("tiers", (res) -> res.contains(".json"));

		for (ResourceLocation res : tiers) {
			try {
				IResource resource = manager.getResource(res);
				List<String> contents = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
				String json = String.join("", contents);
				JsonElement jsonElement = JSONUtils.fromJson(GSON_INSTANCE, json, JsonElement.class);
				output.put(res, jsonElement);
			} catch (Exception e) {
				StaticPower.LOGGER
						.error(String.format("An error occured when attempting to manually load tier: %1$s.", res), e);
			}
		}

		// Now apply the resources.
		process(output, manager);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectMap, IResourceManager manager, IProfiler profiler) {
		process(objectMap, manager);
	}

	private static void process(Map<ResourceLocation, JsonElement> objectMap, IResourceManager manager) {
		// Capture if this is the first time we are caching.
		boolean firstTime = TIERS.size() == 0;
		TIERS.clear();

		// Log that caching has started.
		StaticPower.LOGGER.info(String.format("%1$s Static Power data.", (firstTime ? "Caching" : "Re-caching")));

		objectMap.forEach((key, value) -> {
			ResourceLocation tierPath = new ResourceLocation(key.getNamespace(), key.getPath());
			try (net.minecraft.resources.IResource res = manager.getResource(tierPath);) {
				cacheTierJson(value);
			} catch (Exception e) {
				StaticPower.LOGGER.error(String.format("An error occured when attempting to load tier: %1$s.", key), e);
			}
		});
		// Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power tiers.",
				(firstTime ? "cached" : "re-cached"), TIERS.size()));
	}

	private static void cacheTierJson(JsonElement json) {
		StaticPowerTier parsedResource = GSON_INSTANCE.fromJson(json, StaticPowerTier.class);
		ResourceLocation tierId = new ResourceLocation(
				json.getAsJsonObject().get("tierId").getAsJsonObject().get("namespace").getAsString(),
				json.getAsJsonObject().get("tierId").getAsJsonObject().get("path").getAsString());
		parsedResource.setId(tierId);

		TIERS.put(parsedResource.getTierId().toString(), parsedResource);
		StaticPower.LOGGER
				.info(String.format("Cached static power tier: %1$s.", parsedResource.getTierId().toString()));
	}
}
