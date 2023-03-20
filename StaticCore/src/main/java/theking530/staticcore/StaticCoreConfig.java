package theking530.staticcore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import theking530.staticcore.data.StaticCoreTier;

@EventBusSubscriber(modid = StaticCore.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticCoreConfig {

	public static final StaticPowerCommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final StaticPowerServerConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	public static final Map<String, ConfigPair> TIERS;

	static {
		final Pair<StaticPowerServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(StaticPowerServerConfig::new);
		SERVER_SPEC = serverPair.getRight();
		SERVER = serverPair.getLeft();

		final Pair<StaticPowerCommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(StaticPowerCommonConfig::new);
		COMMON_SPEC = commonPair.getRight();
		COMMON = commonPair.getLeft();

		TIERS = new HashMap<>();
	}

	/**
	 * This config contains data that needs to be loaded before the world is
	 * created. This data is NOT synced to the client.
	 * 
	 */
	public static class StaticPowerCommonConfig {

		public StaticPowerCommonConfig(ForgeConfigSpec.Builder builder) {

		}
	}

	/**
	 * This config contains data that is loaded once the world is created and it is
	 * synced to the client.
	 * 
	 * @author amine
	 *
	 */
	public static class StaticPowerServerConfig {
		public final ConfigValue<String> initialResearch;

		public final ConfigValue<Boolean> enableElectricalDamage;
		public final ConfigValue<Double> electricalDamageThreshold;
		public final ConfigValue<Double> electricalDamageMultiplier;
		public final ConfigValue<Integer> overvoltageExplodeTime;

		public StaticPowerServerConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Research");
			{
				initialResearch = builder.comment("The default research that is selected for all players when first loading a game.").translation(StaticCore.MOD_ID + ".config." + "initialResearch")
						.define("InitialResearch", "staticcore:research_tiers/tier_1");
			}
			builder.push("Power");
			{
				enableElectricalDamage = builder.comment("If true, players can be damaged when contacting electrical sources without wearing protective equiement.")
						.translation(StaticCore.MOD_ID + ".config." + "enableElectricalDamage").define("EnableElectricalDamage", true);
				electricalDamageThreshold = builder.comment("Defines the minimum amount of current that is required to damage a player.")
						.translation(StaticCore.MOD_ID + ".config." + "currentDamageThreshold").define("CurrentDamageThreshold", 0.1);
				electricalDamageMultiplier = builder.comment("Defines the damage multiplier applied to the current when damaging a player.")
						.translation(StaticCore.MOD_ID + ".config." + "currentDamageMultiplier").define("CurrentDamageMultiplier", 1.0);

				overvoltageExplodeTime = builder.comment("Defines how many ticks it takes before a machine explodes from recieveing too high a voltage.")
						.translation(StaticCore.MOD_ID + ".config." + "overvoltageExplodeTime").define("OvervoltageExplodeTime", 40);
			}
			builder.pop();
		}
	}

	public static void registerTier(String modId, ResourceLocation tierId, BiFunction<Builder, String, StaticCoreTier> tierConstructor) {
		Builder builder = new ForgeConfigSpec.Builder();
		StaticCoreTier tier = tierConstructor.apply(builder, modId);
		ForgeConfigSpec spec = builder.build();
		TIERS.put(tierId.toString(), new ConfigPair(spec, tier));
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticCoreConfig.TIERS.get(tierId.toString()).spec, modId + "\\tiers\\" + tierId.getPath() + ".toml");
	}

	public static StaticCoreTier getTier(ResourceLocation tierId) {
		return TIERS.get(tierId.toString()).tier;
	}

	public static boolean isConfigLoaded(ResourceLocation tierId) {
		return TIERS.get(tierId.toString()).spec.isLoaded();
	}

	public static void preInitialize() {
		// Verify the config sub-folder exists.
		checkOrCreateFolder(StaticCore.MOD_ID);

		// Verify the tiers folder exists.
		checkOrCreateFolder(StaticCore.MOD_ID + "\\tiers");

		// Add the server and common configs.
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, StaticCoreConfig.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticCoreConfig.COMMON_SPEC, StaticCore.MOD_ID + "\\" + StaticCore.MOD_ID + "-common.toml");
	}

	private static void checkOrCreateFolder(String path) {
		// Verify or create the folder.
		File subFolder = new File(FMLPaths.CONFIGDIR.get().toFile(), path);
		if (!subFolder.exists()) {
			try {
				if (!subFolder.mkdir()) {
					throw new RuntimeException("Could not create config directory " + subFolder);
				}
			} catch (SecurityException e) {
				throw new RuntimeException("Could not create config directory " + subFolder, e);
			}
		}
	}

	public static class ConfigPair {
		public final StaticCoreTier tier;
		public final ForgeConfigSpec spec;

		public ConfigPair(ForgeConfigSpec spec, StaticCoreTier tier) {
			this.tier = tier;
			this.spec = spec;
		}
	}
}
