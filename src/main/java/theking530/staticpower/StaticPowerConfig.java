package theking530.staticpower;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import theking530.staticpower.data.StaticPowerTier;

@EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerConfig {

	public static final StaticPowerCommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	public static final StaticPowerServerConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	public static final Map<String, ConfigPair> TIERS;

	public static int rubberWoodSpawnChance;
	public static int minRubberWoodBarkPerStrip;
	public static int maxRubberWoodBarkPerStrip;

	public static boolean generateCopperOre;
	public static boolean generateTinOre;
	public static boolean generateLeadOre;
	public static boolean generateSilverOre;
	public static boolean generatePlatinumOre;
	public static boolean generateTungstenOre;
	public static boolean generateZincOre;
	public static boolean generateMagnesiumOre;
	public static boolean generateAluminiumOre;
	public static boolean generateSapphireOre;
	public static boolean generateRubyOre;

	public static int digistoreRegulatorRate;
	public static int digistoreRegulatorSlots;
	public static int digistoreRegulatorStackSize;

	public static int digistoreIOBusRate;
	public static int digistoreIOBusSlots;
	public static int digistoreIOBusStackSize;

	public static int digistoreImporterRate;
	public static int digistoreImporterSlots;
	public static int digistoreImporterStackSize;

	public static int digistoreExporterRate;
	public static int digistoreExporterSlots;
	public static int digistoreExporterStackSize;

	public static int digistoreCraftingInterfaceSlots;

	public static double acceleratorCardMaxImprovment;

	static {
		final Pair<StaticPowerServerConfig, ForgeConfigSpec> serverPair = new ForgeConfigSpec.Builder().configure(StaticPowerServerConfig::new);
		SERVER_SPEC = serverPair.getRight();
		SERVER = serverPair.getLeft();

		final Pair<StaticPowerCommonConfig, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(StaticPowerCommonConfig::new);
		COMMON_SPEC = commonPair.getRight();
		COMMON = commonPair.getLeft();

		TIERS = new HashMap<>();

	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == StaticPowerConfig.COMMON_SPEC) {
			bakeCommonConfig();
		} else if (configEvent.getConfig().getSpec() == StaticPowerConfig.SERVER_SPEC) {
			bakeServerConfig();
		}
	}

	private static void bakeCommonConfig() {
		rubberWoodSpawnChance = COMMON.rubberWoodSpawnChance.get();
		generatePlatinumOre = COMMON.generatePlatinumOre.get();
		generateTungstenOre = COMMON.generateTungstenOre.get();
		generateZincOre = COMMON.generateZincOre.get();
		generateMagnesiumOre = COMMON.generateMagnesiumOre.get();
		generateAluminiumOre = COMMON.generateAluminiumOre.get();
		generateSapphireOre = COMMON.generateSapphireOre.get();
		generateRubyOre = COMMON.generateRubyOre.get();
	}

	private static void bakeServerConfig() {
		minRubberWoodBarkPerStrip = SERVER.minRubberWoodBarkPerStrip.get();
		maxRubberWoodBarkPerStrip = SERVER.maxRubberWoodBarkPerStrip.get();

		generateCopperOre = COMMON.generateCopperOre.get();
		generateTinOre = COMMON.generateTinOre.get();
		generateLeadOre = COMMON.generateLeadOre.get();
		generateSilverOre = COMMON.generateSilverOre.get();

		digistoreRegulatorRate = SERVER.digistoreRegulatorRate.get();
		digistoreRegulatorStackSize = SERVER.digistoreRegulatorStackSize.get();
		digistoreRegulatorSlots = SERVER.digistoreRegulatorSlots.get();

		digistoreIOBusRate = SERVER.digistoreIOBusRate.get();
		digistoreIOBusSlots = SERVER.digistoreIOBusSlots.get();
		digistoreIOBusStackSize = SERVER.digistoreIOBusStackSize.get();

		digistoreImporterRate = SERVER.digistoreImporterRate.get();
		digistoreImporterSlots = SERVER.digistoreImporterSlots.get();
		digistoreImporterStackSize = SERVER.digistoreImporterStackSize.get();

		digistoreExporterRate = SERVER.digistoreExporterRate.get();
		digistoreExporterSlots = SERVER.digistoreExporterSlots.get();
		digistoreExporterStackSize = SERVER.digistoreExporterStackSize.get();

		digistoreCraftingInterfaceSlots = SERVER.digistoreCraftingInterfaceSlots.get();

		acceleratorCardMaxImprovment = SERVER.acceleratorCardImprovment.get();
	}

	/**
	 * This config contains data that needs to be loaded before the world is
	 * created. This data is NOT synced to the client.
	 * 
	 */
	public static class StaticPowerCommonConfig {
		public ConfigValue<Integer> rubberWoodSpawnChance;

		public BooleanValue generateCopperOre;
		public BooleanValue generateTinOre;
		public BooleanValue generateLeadOre;
		public BooleanValue generateSilverOre;
		public BooleanValue generatePlatinumOre;
		public BooleanValue generateTungstenOre;
		public BooleanValue generateZincOre;
		public BooleanValue generateMagnesiumOre;
		public BooleanValue generateAluminiumOre;
		public BooleanValue generateSapphireOre;
		public BooleanValue generateRubyOre;

		public StaticPowerCommonConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Generation");
			builder.push("Ore Generation");
			generateZincOre = builder.comment("Disable or Enable Zinc Ore Generation").translation(StaticPower.MOD_ID + ".config." + "zincore").define("GenerateZincOre", true);
			generateMagnesiumOre = builder.comment("Disable or Enable Magnesium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "magnesiumore").define("GenerateMagnesiumOre",
					true);
			generateAluminiumOre = builder.comment("Disable or Enable Aluminium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "aluminiumore").define("GenerateAluminiumOre",
					true);
			generateCopperOre = builder.comment("Disable or Enable Copper Ore Generation").translation(StaticPower.MOD_ID + ".config." + "copperore").define("GenerateCopperOre", true);
			generateTinOre = builder.comment("Disable or Enable Tin Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tinore").define("GenerateTinOre", true);
			generateLeadOre = builder.comment("Disable or Enable Lead Ore Generation").translation(StaticPower.MOD_ID + ".config." + "leadore").define("GenerateLeadOre", true);
			generateSilverOre = builder.comment("Disable or Enable Silver Ore Generation").translation(StaticPower.MOD_ID + ".config." + "silverore").define("GenerateSilverOre", true);
			generatePlatinumOre = builder.comment("Disable or Enable Platinum Ore Generation").translation(StaticPower.MOD_ID + ".config." + "platinumore").define("GeneratePlatinumOre",
					true);
			generateTungstenOre = builder.comment("Disable or Enable Tunsgten Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tungstenore").define("GenerateTungstenOre",
					true);
			generateSapphireOre = builder.comment("Disable or Enable Sapphire Ore Generation").translation(StaticPower.MOD_ID + ".config." + "sapphireore").define("GenerateSapphireOre",
					true);
			generateRubyOre = builder.comment("Disable or Enable Ruby Ore Generation").translation(StaticPower.MOD_ID + ".config." + "rubyore").define("GenerateRubyOre", true);
			builder.pop();
			builder.push("Tree Generation");
			rubberWoodSpawnChance = builder.comment("Controls the chance of a rubber tree spawning.").translation(StaticPower.MOD_ID + ".config." + "rubberWoodSpawnChance")
					.define("RubberWoodSpawnChance", 30);
			builder.pop();
			builder.pop();
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
		public ConfigValue<Integer> minRubberWoodBarkPerStrip;
		public ConfigValue<Integer> maxRubberWoodBarkPerStrip;

		public ConfigValue<Integer> digistoreRegulatorRate;
		public ConfigValue<Integer> digistoreRegulatorStackSize;
		public ConfigValue<Integer> digistoreRegulatorSlots;

		public ConfigValue<Integer> digistoreIOBusRate;
		public ConfigValue<Integer> digistoreIOBusSlots;
		public ConfigValue<Integer> digistoreIOBusStackSize;

		public ConfigValue<Integer> digistoreImporterRate;
		public ConfigValue<Integer> digistoreImporterSlots;
		public ConfigValue<Integer> digistoreImporterStackSize;

		public ConfigValue<Integer> digistoreExporterRate;
		public ConfigValue<Integer> digistoreExporterSlots;
		public ConfigValue<Integer> digistoreExporterStackSize;

		public ConfigValue<Integer> digistoreCraftingInterfaceSlots;

		public ConfigValue<Double> acceleratorCardImprovment;

		public StaticPowerServerConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Tools");
			builder.push("Axe");
			minRubberWoodBarkPerStrip = builder.comment("Controls the minimum number of strips of bark are removed from a rubber wood log when stripped with an axe.")
					.translation(StaticPower.MOD_ID + ".config." + "minRubberWoodBarkPerStrip").define("MinRubberWoodBarkPerStrip", 1);
			maxRubberWoodBarkPerStrip = builder.comment("Controls the maximum number of strips of bark are removed from a rubber wood log when stripped with an axe.")
					.translation(StaticPower.MOD_ID + ".config." + "maxRubberWoodBarkPerStrip").define("MaxRubberWoodBarkPerStrip", 4);
			builder.pop();
			builder.pop();

			builder.push("Digistore");
			builder.push("Regulator");
			digistoreRegulatorRate = builder
					.comment("Controls how many ticks between each digistore regulator operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorRate").define("DigistoreRegulatorRate", 50);
			digistoreRegulatorSlots = builder.comment("Controls how many slots the regulator has.").translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorSlots")
					.define("DigistoreRegulatorSlots", 8);
			digistoreRegulatorStackSize = builder.comment("Controls how many items can be transfered for each item type during a regulation.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorStackSize").define("DigistoreRegulatorStackSize", 8);
			builder.pop();

			builder.push("I/O Bus");
			digistoreIOBusRate = builder
					.comment("Controls how many ticks between each digistore I/O bus operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusRate").define("DigistoreIOBusRate", 40);
			digistoreIOBusSlots = builder.comment("Controls how many slots each the import and output rows of the digistore I/O bus have.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusSlots").define("DigistoreIOBusSlots", 8);
			digistoreIOBusStackSize = builder.comment("Controls how many items the digistore I/O will try to import per operation. This count is separate for the import and the export.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusStackSize").define("DigistoreIOBusStackSize", 8);
			builder.pop();

			builder.push("Importer");
			digistoreImporterRate = builder
					.comment("Controls how many ticks between each digistore importer operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterRate").define("DigistoreImporterRate", 40);
			digistoreImporterSlots = builder.comment("Controls how many slots the digistore importer has.").translation(StaticPower.MOD_ID + ".config." + "digistoreImporterSlots")
					.define("DigistoreImporterSlots", 8);
			digistoreImporterStackSize = builder.comment("Controls how many items the importer will try to import per operation.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterStackSize").define("DigistoreImporterStackSize", 8);
			builder.pop();

			builder.push("Exporter");
			digistoreExporterRate = builder
					.comment("Controls how many ticks between each digistore exporter operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterRate").define("DigistoreExporterRate", 40);
			digistoreExporterSlots = builder.comment("Controls how many slots the digistore exporter has.").translation(StaticPower.MOD_ID + ".config." + "digistoreExporterSlots")
					.define("DigistoreExporterSlots", 8);
			digistoreExporterStackSize = builder.comment("Controls how many items the exporter will try to export per operation.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterStackSize").define("DigistoreExporterstackSize", 8);
			builder.pop();

			builder.push("Crafting Interface");
			digistoreCraftingInterfaceSlots = builder.comment("Controls how many slots the crafting interface attachment gets.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreCraftingInterfaceSlots").define("DigistoreCraftingInterfaceSlots", 9);
			builder.pop();
			builder.pop();

			builder.push("Upgrades");
			acceleratorCardImprovment = builder.comment("Defines the effect a max sized stack of accelerator upgrades will have.")
					.translation(StaticPower.MOD_ID + ".config." + "acceleratorCardImprovment").define("AcceleratorCardImprovment", 4.0);
			builder.pop();
		}
	}

	public static void registerTier(ResourceLocation tierId, Function<Builder, StaticPowerTier> tierConstructor) {
		Pair<StaticPowerTier, ForgeConfigSpec> basicPair = new ForgeConfigSpec.Builder().configure(tierConstructor);
		TIERS.put(tierId.toString(), new ConfigPair(basicPair.getRight(), basicPair.getLeft()));
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticPowerConfig.TIERS.get(tierId.toString()).spec, StaticPower.MOD_ID + "\\tiers\\" + tierId.getPath() + ".toml");
	}

	public static StaticPowerTier getTier(ResourceLocation tierId) {
		return TIERS.get(tierId.toString()).tier;
	}

	public static void preInitialize() {
		// Verify the config sub-folder exists.
		checkOrCreateFolder(StaticPower.MOD_ID);

		// Verify the tiers folder exists.
		checkOrCreateFolder(StaticPower.MOD_ID + "\\tiers");

		// Add the server and common configs.
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, StaticPowerConfig.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaticPowerConfig.COMMON_SPEC, StaticPower.MOD_ID + "\\" + StaticPower.MOD_ID + "-common.toml");
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
		public final StaticPowerTier tier;
		public final ForgeConfigSpec spec;

		public ConfigPair(ForgeConfigSpec spec, StaticPowerTier tier) {
			this.tier = tier;
			this.spec = spec;
		}
	}
}
