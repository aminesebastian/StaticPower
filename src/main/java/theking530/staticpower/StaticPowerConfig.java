package theking530.staticpower;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

@EventBusSubscriber(modid = StaticPower.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StaticPowerConfig {

	public static final StaticPowerClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;

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

	static {
		final Pair<StaticPowerClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(StaticPowerClientConfig::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
		if (configEvent.getConfig().getSpec() == StaticPowerConfig.CLIENT_SPEC) {
			bakeConfig();
		}
	}

	public static void bakeConfig() {
		generateCopperOre = CLIENT.generateCopperOre.get();
		generateTinOre = CLIENT.generateTinOre.get();
		generateLeadOre = CLIENT.generateLeadOre.get();
		generateSilverOre = CLIENT.generateSilverOre.get();
		generatePlatinumOre = CLIENT.generatePlatinumOre.get();
		generateTungstenOre = CLIENT.generateTungstenOre.get();
		generateZincOre = CLIENT.generateZincOre.get();
		generateMagnesiumOre = CLIENT.generateMagnesiumOre.get();
		generateAluminiumOre = CLIENT.generateAluminiumOre.get();
		generateSapphireOre = CLIENT.generateSapphireOre.get();
		generateRubyOre = CLIENT.generateRubyOre.get();

		digistoreRegulatorRate = CLIENT.digistoreRegulatorRate.get();
		digistoreRegulatorStackSize = CLIENT.digistoreRegulatorStackSize.get();
		digistoreRegulatorSlots = CLIENT.digistoreRegulatorSlots.get();

		digistoreIOBusRate = CLIENT.digistoreIOBusRate.get();
		digistoreIOBusSlots = CLIENT.digistoreIOBusSlots.get();
		digistoreIOBusStackSize = CLIENT.digistoreIOBusStackSize.get();

		digistoreImporterRate = CLIENT.digistoreImporterRate.get();
		digistoreImporterSlots = CLIENT.digistoreImporterSlots.get();
		digistoreImporterStackSize = CLIENT.digistoreImporterStackSize.get();

		digistoreExporterRate = CLIENT.digistoreExporterRate.get();
		digistoreExporterSlots = CLIENT.digistoreExporterSlots.get();
		digistoreExporterStackSize = CLIENT.digistoreExporterStackSize.get();
	}

	public static class StaticPowerClientConfig {
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

		public StaticPowerClientConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Digistore");
			builder.push("Regulator");
			digistoreRegulatorRate = builder.comment("Controls how many ticks between each digistore regulator operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorRate").define("DigistoreRegulatorRate", 25);
			digistoreRegulatorSlots = builder.comment("Controls how many slots the regulator has.").translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorSlots")
					.define("DigistoreRegulatorSlots", 8);
			digistoreRegulatorStackSize = builder.comment("Controls how many items can be transfered for each item type during a regulation.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreRegulatorStackSize").define("DigistoreRegulatorStackSize", 8);
			builder.pop();
			
			builder.push("I/O Bus");
			digistoreIOBusRate = builder.comment("Controls how many ticks between each digistore I/O bus operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusRate").define("DigistoreIOBusRate", 20);
			digistoreIOBusSlots = builder.comment("Controls how many slots each the import and output rows of the digistore I/O bus have.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusSlots").define("DigistoreIOBusSlots", 8);
			digistoreIOBusStackSize = builder.comment("Controls how many items the digistore I/O will try to import per operation. This count is separate for the import and the export.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreIOBusStackSize").define("DigistoreIOBusStackSize", 8);
			builder.pop();
			
			builder.push("Importer");
			digistoreImporterRate = builder.comment("Controls how many ticks between each digistore importer operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterRate").define("DigistoreImporterRate", 20);
			digistoreImporterSlots = builder.comment("Controls how many slots the digistore importer has.").translation(StaticPower.MOD_ID + ".config." + "digistoreImporterSlots")
					.define("DigistoreImporterSlots", 8);
			digistoreImporterStackSize = builder.comment("Controls how many items the importer will try to import per operation.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreImporterStackSize").define("DigistoreImporterStackSize", 8);
			builder.pop();
			
			builder.push("Exporter");
			digistoreExporterRate = builder.comment("Controls how many ticks between each digistore exporter operation. The higher, the faster the operations, but the stronger hit to performance.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterRate").define("DigistoreExporterRate", 20);
			digistoreExporterSlots = builder.comment("Controls how many slots the digistore exporter has.").translation(StaticPower.MOD_ID + ".config." + "digistoreExporterSlots")
					.define("DigistoreExporterSlots", 8);
			digistoreExporterStackSize = builder.comment("Controls how many items the exporter will try to export per operation.")
					.translation(StaticPower.MOD_ID + ".config." + "digistoreExporterStackSize").define("DigistoreExporterstackSize", 8);
			builder.pop();
			builder.pop();

			builder.push("Ore_Generation");
			generateZincOre = builder.comment("Disable or Enable Zinc Ore Generation").translation(StaticPower.MOD_ID + ".config." + "zincore").define("GenerateZincOre", true);
			generateMagnesiumOre = builder.comment("Disable or Enable Magnesium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "magnesiumore").define("GenerateMagnesiumOre", true);
			generateAluminiumOre = builder.comment("Disable or Enable Aluminium Ore Generation").translation(StaticPower.MOD_ID + ".config." + "aluminiumore").define("GenerateAluminiumOre", true);
			generateCopperOre = builder.comment("Disable or Enable Copper Ore Generation").translation(StaticPower.MOD_ID + ".config." + "copperore").define("GenerateCopperOre", true);
			generateTinOre = builder.comment("Disable or Enable Tin Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tinore").define("GenerateTinOre", true);
			generateLeadOre = builder.comment("Disable or Enable Lead Ore Generation").translation(StaticPower.MOD_ID + ".config." + "leadore").define("GenerateLeadOre", true);
			generateSilverOre = builder.comment("Disable or Enable Silver Ore Generation").translation(StaticPower.MOD_ID + ".config." + "silverore").define("GenerateSilverOre", true);
			generatePlatinumOre = builder.comment("Disable or Enable Platinum Ore Generation").translation(StaticPower.MOD_ID + ".config." + "platinumore").define("GeneratePlatinumOre", true);
			generateTungstenOre = builder.comment("Disable or Enable Tunsgten Ore Generation").translation(StaticPower.MOD_ID + ".config." + "tungstenore").define("GenerateTungstenOre", true);
			generateSapphireOre = builder.comment("Disable or Enable Sapphire Ore Generation").translation(StaticPower.MOD_ID + ".config." + "sapphireore").define("GenerateSapphireOre", true);
			generateRubyOre = builder.comment("Disable or Enable Ruby Ore Generation").translation(StaticPower.MOD_ID + ".config." + "rubyore").define("GenerateRubyOre", true);
			builder.pop();
		}
	}
}
