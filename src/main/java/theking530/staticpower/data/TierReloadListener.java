package theking530.staticpower.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import theking530.staticpower.StaticPower;

public class TierReloadListener extends JsonReloadListener {
	public static final StaticPowerTier DEFAULT_TIER = new StaticPowerTier();
	/**
	 * Contains all the tiers loaded at runtime.
	 */
	public static final Map<String, StaticPowerTier> TIERS = new HashMap<String, StaticPowerTier>();
	private static final Gson GSON_INSTANCE = new Gson();

	public TierReloadListener() {
		super(GSON_INSTANCE, "tiers");
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
				StaticPower.LOGGER.error(String.format("Unable to find tier of type: %1$s. Supplying default. This error can be safely ignored during mod initialization.", tierType));
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
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power tiers from the server.", (firstTime ? "recieved" : "re-recieved"), TIERS.size()));
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonObject> objectMap, IResourceManager manager, IProfiler profiler) {
		// Capture if this is the first time we are caching.
		boolean firstTime = TIERS.size() == 0;
		TIERS.clear();

		// Log that caching has started.
		StaticPower.LOGGER.info(String.format("%1$s Static Power data.", (firstTime ? "Caching" : "Re-caching")));

		objectMap.forEach((key, value) -> {
			try (net.minecraft.resources.IResource res = manager.getResource(getPreparedPath(key));) {
				StaticPowerTier parsedResource = GSON_INSTANCE.fromJson(value, StaticPowerTier.class);
				ResourceLocation tierId = new ResourceLocation(value.getAsJsonObject("tierId").get("namespace").getAsString(), value.getAsJsonObject("tierId").get("path").getAsString());
				parsedResource.setId(tierId);

				TIERS.put(parsedResource.getTierId().toString(), parsedResource);
				StaticPower.LOGGER.info(String.format("Read static power tier: %1$s.", parsedResource.getTierId().toString()));
			} catch (Exception e) {
				StaticPower.LOGGER.error(String.format("An error occured when attempting to load tier: %1$s.", key), e);
			}

		});
		// Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power tiers.", (firstTime ? "cached" : "re-cached"), TIERS.size()));
	}

}
