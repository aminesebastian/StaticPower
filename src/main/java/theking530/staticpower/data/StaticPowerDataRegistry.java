package theking530.staticpower.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.TableLootEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import theking530.staticpower.StaticPower;

public class StaticPowerDataRegistry {
	public static final Logger LOGGER = LogManager.getLogger("Static Power");
	private static final Map<ResourceLocation, ResourceLocation> LOOT_TABLE_ADDITIONS = new HashMap<ResourceLocation, ResourceLocation>();

	public static void registerLootTableOverride(String vanillaBlockName, String overrideFileName) {
		LOOT_TABLE_ADDITIONS.put(new ResourceLocation("minecraft", vanillaBlockName), new ResourceLocation(StaticPower.MOD_ID, overrideFileName));
	}

	public static void onLootTableLoaded(LootTableLoadEvent event) {
		if (LOOT_TABLE_ADDITIONS.containsKey(event.getName())) {
			event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(LOOT_TABLE_ADDITIONS.get(event.getName()))).build());
		}
	}
}
