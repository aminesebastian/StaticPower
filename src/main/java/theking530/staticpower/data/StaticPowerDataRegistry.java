package theking530.staticpower.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.utilities.CustomJsonLoader;
import theking530.staticpower.StaticPower;

public class StaticPowerDataRegistry {
	public static final Logger LOGGER = LogManager.getLogger(StaticPowerDataRegistry.class);
	private static final Map<ResourceLocation, ResourceLocation> LOOT_TABLE_ADDITIONS = new HashMap<ResourceLocation, ResourceLocation>();

	/**
	 * Contains all the tiers loaded at runtime.
	 */
	private static final Map<ResourceLocation, StaticPowerTier> TIERS = new HashMap<ResourceLocation, StaticPowerTier>();

	public static void registerLootTableOverride(String vanillaBlockName, String overrideFileName) {
		LOOT_TABLE_ADDITIONS.put(new ResourceLocation("minecraft", vanillaBlockName), new ResourceLocation(StaticPower.MOD_ID, overrideFileName));
	}

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
		LOGGER.info(String.format("%1$s Static Power data.", (firstTime ? "Caching" : "Re-caching")));

		// Load all tiers.
		List<StaticPowerTier> tiers = CustomJsonLoader.loadAllInPath(StaticPower.class, FMLEnvironment.dist == Dist.CLIENT ? "/data/staticpower/tiers" : "/tiers", StaticPowerTier.class);
		customDataCount += tiers.size();
		tiers.forEach(tier -> {
			TIERS.put(tier.getTierId(), tier);
			LOGGER.info(String.format("Loaded Tier: %1$s.", tier.getTierId()));
		});

		// Log the completion.
		LOGGER.info(String.format("Succesfully %1$s %2$d Static Power data.", (firstTime ? "cached" : "re-cached"), customDataCount));
	}

	public static void onLootTableLoaded(LootTableLoadEvent event) {
		if (LOOT_TABLE_ADDITIONS.containsKey(event.getName())) {
			event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(LOOT_TABLE_ADDITIONS.get(event.getName()))).build());
		}
	}
}
