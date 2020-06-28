package theking530.staticpower.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;
import theking530.common.utilities.CustomJsonLoader;
import theking530.staticpower.StaticPower;

public class StaticPowerDataRegistry {
	/**
	 * Contains all the tiers loaded at runtime.
	 */
	private static final Map<ResourceLocation, StaticPowerTier> TIERS = new HashMap<ResourceLocation, StaticPowerTier>();

	/**
	 * Gets the loaded tier object for the provided tier type.
	 * 
	 * @param tierType
	 * @return
	 */
	public static StaticPowerTier getTier(ResourceLocation tierType) {
		if (!TIERS.containsKey(tierType)) {
			throw new RuntimeException(String.format("Unable to find tier of type: %1$s.", tierType));
		}
		return TIERS.get(tierType);
	}

	/**
	 * Loads all the custom data StaticPower requires.
	 * 
	 * @param event
	 */
	public static void onResourcesReloaded() {
		// Capture if this is the first time we are caching.
		boolean firstTime = TIERS.size() == 0;
		TIERS.clear();
		int customDataCount = 0;

		// Log that caching has started.
		StaticPower.LOGGER.info(String.format("%1$s Static Power data.", (firstTime ? "Caching" : "Re-caching")));

		// Load all tiers.
		List<StaticPowerTier> tiers = CustomJsonLoader.loadAllInPath(StaticPower.class, "/data/staticpower/tiers", StaticPowerTier.class);
		customDataCount += tiers.size();
		tiers.forEach(tier -> TIERS.put(tier.getTierId(), tier));

		// Log the completion.
		StaticPower.LOGGER.info(String.format("Succesfully %1$s %2$d Static Power data.", (firstTime ? "cached" : "re-cached"), customDataCount));
	}
}
